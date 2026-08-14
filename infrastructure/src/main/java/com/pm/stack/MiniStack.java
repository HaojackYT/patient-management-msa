package com.pm.stack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.rds.CfnDBInstance;

public class MiniStack extends Stack {

	private final Vpc vpc;
	private final Cluster ecsCluster;

	private Vpc createVpc() {

		return Vpc.Builder.create(this, "PatientManagementVpc")
				.vpcName("PatientManagementVpc")
				// AZs: Availability Zones
				.maxAzs(2)
				// MiniStack does not support AWS::EC2::EIP / AWS::EC2::NatGateway.
				// Disable NAT gateways to synthesize template that only contain esource types
				// MiniStack can provide.
				.natGateways(0)
				.build();
	}

	private Cluster createEcsCluster() {

		return Cluster.Builder.create(this, "PatientManagementCluster")
				.vpc(vpc)
				// MiniStack does not support AWS::ServiceDiscovery::PrivateDnsNamespace,
				// instead use the docker-compose network for service-to-service
				// communication (service name == container hostname).
				.build();
	}

	private CfnDBInstance createDatabase(String id, String dbName,
			String masterUsername, String masterPassword) {

		return CfnDBInstance.Builder.create(this, id)
				.engine("postgres")
				.dbInstanceClass("db.t3.micro")
				.allocatedStorage("20") // 20 GB
				.masterUsername(masterUsername)
				.masterUserPassword(masterPassword)
				.dbName(dbName)
				.build();
	}

	// FargateService: a type of ECS service
	// easy to start, stop and scale ECS task that run in difference containers
	private FargateService createFargateService(String id, String imageName, List<Integer> ports,
			CfnDBInstance db, String dbName, String dbUsername, String dbPassword,
			Map<String, String> additionalEnvVars) {

		FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
				.cpu(256) // cpu units
				.memoryLimitMiB(512)
				.build();

		// .Builder: configure additional properties later before calling .build().
		ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
				.image(ContainerImage.fromRegistry(imageName))
				.portMappings(ports.stream()
						.map(port -> PortMapping.builder()
								.containerPort(port)
								.hostPort(port)
								.protocol(Protocol.TCP)
								.build())
						.toList())
				.logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
						.logGroup(LogGroup.Builder.create(this, id + "LogGroup")
								.logGroupName("/ecs/" + imageName)
								.removalPolicy(RemovalPolicy.DESTROY)
								.retention(RetentionDays.ONE_DAY)
								.build())
						.streamPrefix(imageName)
						.build()));

		Map<String, String> envVars = new HashMap<>();
		// MiniStack does not support AWS::MSK::Cluster,
		// so Kafka runs as a docker-compose service.
		envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "kafka:9092");

		if (additionalEnvVars != null) {
			envVars.putAll(additionalEnvVars);
		}

		if (db != null) {
			envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s".formatted(
					db.getAttrEndpointAddress(),
					db.getAttrEndpointPort(),
					dbName));

			envVars.put("SPRING_DATASOURCE_USERNAME", dbUsername);
			envVars.put("SPRING_DATASOURCE_PASSWORD", dbPassword);
			envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
			envVars.put("SPRING_SQL_INIT_MODE", "always");
			// Make sure the database is ready
			envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "600000");
		}

		containerOptions.environment(envVars);
		// image -> container -> taskDefinition -> service
		taskDefinition.addContainer(imageName + "Container", containerOptions.build());

		return FargateService.Builder.create(this, id)
				.cluster(ecsCluster)
				.taskDefinition(taskDefinition)
				.assignPublicIp(false)
				.serviceName(imageName)
				.build();
	}

	private Path findEnvFile() { // in the current or any parent directory

		// cwd: current working directory
		Path cwd = Path.of("").toAbsolutePath();
		Path candidate = cwd;

		while (candidate != null) {
			// {cwd}/.env
			Path envFile = candidate.resolve(".env");
			if (Files.exists(envFile)) {
				return envFile;
			}
			// ../../.. -> ../..
			candidate = candidate.getParent();
		}

		return null;
	}

	private Map<String, String> loadEnvVariables() {

		Map<String, String> result = new HashMap<>();
		Path envFile = findEnvFile();

		if (envFile != null) {
			try {
				List<String> lines = Files.readAllLines(envFile);

				for (String line : lines) {

					String trimmed = line.trim();

					// Skip empty and comment lines
					if (trimmed.isEmpty() || trimmed.startsWith("#")) {
						continue;
					}

					int eqIndex = trimmed.indexOf('=');

					// Skip lines that don't contain '=' or have it at the start
					if (eqIndex <= 0) {
						continue;
					}

					String key = trimmed.substring(0, eqIndex).trim();
					String value = trimmed.substring(eqIndex + 1).trim();

					// Strip surrounding quotes (' or ") if present
					if (value.length() >= 2 &&
							((value.startsWith("\"") && value.endsWith("\"")) ||
									(value.startsWith("'") && value.endsWith("'")))) {

						value = value.substring(1, value.length() - 1);
					}

					if (!key.isEmpty()) {
						result.put(key, value);
					}
				}
			} catch (IOException e) {
				System.err.println("Couldn't read .env file at " + envFile + ": " + e.getMessage());

			}
		}

		// Prioritize system environment variables over .env
		result.putAll(System.getenv());
		return result;
	}

	private void createApiGatewayService() {

		FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this,
				"APIGateWayTaskDefinition")
				.cpu(256)
				.memoryLimitMiB(512)
				.build();
		Map<String, String> envVars = loadEnvVariables();

		ContainerDefinitionOptions containerOptions = ContainerDefinitionOptions.builder()
				.image(ContainerImage.fromRegistry("api-gateway"))
				.environment(Map.of(
						"SPRING_PROFILES_ACTIVE", "prod", // = application-prod.yml
						"AUTH_SERVICE_ADDRESS", "http://host.docker.internal:" +
								envVars.getOrDefault("AUTH_SERVICE_PORT", "4004")))
				.portMappings(List.of(Integer.parseInt(envVars.getOrDefault(
						"API_GATEWAY_PORT", "4003"))).stream()
						.map(port -> PortMapping.builder()
								.containerPort(port)
								.hostPort(port)
								.protocol(Protocol.TCP)
								.build())
						.toList())
				.logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
						.logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
								.logGroupName("/ecs/api-gateway")
								.removalPolicy(RemovalPolicy.DESTROY)
								.retention(RetentionDays.ONE_DAY)
								.build())
						.streamPrefix("api-gateway")
						.build()))
				.build();

		// image -> container -> taskDefinition -> service
		taskDefinition.addContainer("ApiGatewayContainer", containerOptions);

		// MiniStack does not support AWS::ElasticLoadBalancingV2::LoadBalancer /
		// Listener / TargetGroup or standalone SecurityGroupIngress/Egress rules,
		// so the API gateway is deployed as a plain Fargate service reachable on
		// the docker-compose network.
		FargateService.Builder.create(this, "ApiGatewayService")
				.cluster(ecsCluster)
				.taskDefinition(taskDefinition)
				.serviceName("api-gateway")
				.assignPublicIp(false)
				.build();
	}

	public MiniStack(final App scope, final String id, final StackProps props) {

		super(scope, id, props);
		this.vpc = createVpc();

		Map<String, String> envVars = loadEnvVariables();

		String authDbUser = envVars.getOrDefault("AUTH_SERVICE_DB_USER", "admin_user");
		String authDbPassword = envVars.getOrDefault("AUTH_SERVICE_DB_PASSWORD", "password");
		String patientDbUser = envVars.getOrDefault("PATIENT_SERVICE_DB_USER", "admin_user");
		String patientDbPassword = envVars.getOrDefault("PATIENT_SERVICE_DB_PASSWORD", "password");

		// RDS (Relational Database Service)
		CfnDBInstance authServiceDb = createDatabase(
				"AuthServiceDb", "auth-service-db", authDbUser, authDbPassword);
		CfnDBInstance patientServiceDb = createDatabase(
				"PatientServiceDb", "patient-service-db", patientDbUser, patientDbPassword);

		// ECS (Elastic Container Service)
		this.ecsCluster = createEcsCluster();

		String jwtSecret = envVars.get("JWT_SECRET");
		if (jwtSecret == null || jwtSecret.isBlank()) {
			throw new IllegalStateException(
					"Environment variable JWT_SECRET is not set.");
		}

		FargateService authService = createFargateService(
				"AuthService",
				"auth-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"AUTH_SERVICE_PORT", "4004"))),
				authServiceDb, "auth-service-db", authDbUser, authDbPassword,
				Map.of("JWT_SECRET", jwtSecret));
		authService.getNode().addDependency(authServiceDb);

		FargateService billingService = createFargateService(
				"BillingService",
				"billing-service",
				List.of(
						Integer.parseInt(envVars.getOrDefault(
								"BILLING_SERVICE_PORT", "4001")),
						Integer.parseInt(envVars.getOrDefault(
								"GRPC_SERVER_PORT", "9001"))),
				null, null, null, null, null);

		FargateService analyticsService = createFargateService(
				"AnalyticsService",
				"analytics-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"ANALYTICS_SERVICE_PORT", "4002"))),
				null, null, null, null, null);

		FargateService patientService = createFargateService(
				"PatientService",
				"patient-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"PATIENT_SERVICE_PORT", "4000"))),
				patientServiceDb, "patient-service-db", patientDbUser, patientDbPassword,
				Map.of(
						"BILLING_SERVICE_ADDRESS",
						"http://billing-service:" + envVars.getOrDefault(
								"BILLING_SERVICE_PORT", "4001"),
						"BILLING_SERVICE_GRPC_PORT", envVars.getOrDefault(
								"GRPC_SERVER_PORT", "9001")));
		patientService.getNode().addDependency(patientServiceDb);
		patientService.getNode().addDependency(billingService);

		createApiGatewayService();
	}

	public static void main(final String[] args) {

		// AWS CDK application
		// outdir: is where the cloudformation template will be generated
		App app = new App(AppProps.builder().outdir("cdk.out").build());

		// Additional properties that can be applied to the stack
		// synthesizer: convert code to cloudformation template
		// BootstraplessSynthesizer: skip inital boostrapping of the cdk environment
		StackProps stackProps = StackProps.builder()
				.synthesizer(new BootstraplessSynthesizer())
				.build();

		new MiniStack(app, "MiniStack", stackProps);

		// stackProps + miniStack = cloudformation template
		app.synth();

		System.out.println("CloudFormation template generated in cdk.out directory.");
	}

}
