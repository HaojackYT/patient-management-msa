package com.pm.stack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.CloudMapOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class MiniStack extends Stack {

	private final Vpc vpc;
	private final Cluster ecsCluster;

	private Vpc createVpc() {

		return Vpc.Builder.create(this, "PatientManagementVpc")
				.vpcName("PatientManagementVpc")
				// AZs: Availability Zones
				.maxAzs(2)
				.build();
	}

	// auth-service.patient-management.local
	private Cluster createEcsCluster() {

		return Cluster.Builder.create(this, "PatientManagementCluster")
				.vpc(vpc)
				// Cloud Map namespace: service discovery in AWS ECS
				// allow microservices to find and communicate with each other
				.defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
						.name("patient-management.local")
						.build())
				.build();
	}

	private DatabaseInstance createDatabase(String id, String dbName) {

		return DatabaseInstance.Builder
				.create(this, id)
				.engine(DatabaseInstanceEngine.postgres(
						PostgresInstanceEngineProps.builder()
								.version(PostgresEngineVersion.VER_18_3)
								.build()))
				.vpc(vpc)
				// instanceType: a combination of CPU, memory, storage, and networking capacity
				.instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
				.allocatedStorage(20) // 20 GB
				.credentials(Credentials.fromGeneratedSecret("admin_user"))
				.databaseName(dbName)
				// destroy the database when the stack is deleted
				.removalPolicy(RemovalPolicy.DESTROY)
				.build();
	}

	private CfnHealthCheck createHealthCheck(DatabaseInstance db, String id) {

		return CfnHealthCheck.Builder.create(this, id)
				.healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
						.type("TCP")
						.port(Token.asNumber(db.getDbInstanceEndpointPort()))
						.ipAddress(db.getDbInstanceEndpointAddress())
						.requestInterval(30) // seconds
						// Try 3 times once every 30 seconds before report a failure
						.failureThreshold(3)
						.build())
				.build();
	}

	private CfnCluster createMskCluster() {

		return CfnCluster.Builder.create(this, "MskCluster")
				.clusterName("kafka-cluster")
				.kafkaVersion("2.8.1")
				.numberOfBrokerNodes(1)
				// Connect VPC to all the broker nodes in the cluster using private subnets
				.brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
						.instanceType("kafka.t3.small")
						.clientSubnets(vpc.getPrivateSubnets().stream()
								.map(ISubnet::getSubnetId)
								.collect(Collectors.toList()))
						// Specify how many brokers get distributed across AZs
						.brokerAzDistribution("DEFAULT")
						.build())
				.build();
	}

	// FargateService: a type of ECS service
	// easy to start, stop and scale ECS task that run in difference containers
	private FargateService createFargateService(String id, String imageName, List<Integer> ports,
			DatabaseInstance db, Map<String, String> additionalEnvVars) {

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
		envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

		if (additionalEnvVars != null) {
			envVars.putAll(additionalEnvVars);
		}

		if (db != null) {
			envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
					db.getDbInstanceEndpointAddress(),
					db.getDbInstanceEndpointPort(),
					imageName));

			envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
			envVars.put("SPRING_DATASOURCE_PASSWORD",
					db.getSecret().secretValueFromJson("password").toString());
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
				// ECS Service Discovery
				.cloudMapOptions(CloudMapOptions.builder()
						.name(imageName)
						.build())
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
						"SPRING_PROFILES_ACTIVE", "prod",
						"AUTH_SERVICE_ADDRESS", "http://auth-service.patient-management.local:" +
								envVars.getOrDefault("API_GATEWAY_PORT", "4004")))
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

		ApplicationLoadBalancedFargateService apiGatewayService =

				ApplicationLoadBalancedFargateService.Builder
						.create(this, "ApiGatewayService")
						.cluster(ecsCluster)
						.taskDefinition(taskDefinition)
						.serviceName("api-gateway")
						.desiredCount(1) // how many instances of the service
						// How long the application load balance waits for the service to start
						.healthCheckGracePeriod(Duration.seconds(60))
						.cloudMapOptions(CloudMapOptions.builder()
								.name("api-gateway")
								.build())
						.build();
	}

	public MiniStack(final App scope, final String id, final StackProps props) {

		super(scope, id, props);
		this.vpc = createVpc();

		// RDS
		DatabaseInstance authServiceDb = createDatabase(
				"AuthServiceDb", "auth-service-db");
		DatabaseInstance patientServiceDb = createDatabase(
				"PatientServiceDb", "patient-service-db");
		CfnHealthCheck authServiceDbHealthCheck = createHealthCheck(
				authServiceDb, "AuthServiceDbHealthCheck");
		CfnHealthCheck patientServiceDbHealthCheck = createHealthCheck(
				patientServiceDb, "PatientServiceDbHealthCheck");

		// MSK
		CfnCluster mskCluster = createMskCluster();

		// ECS
		this.ecsCluster = createEcsCluster();

		Map<String, String> envVars = loadEnvVariables();

		String jwtSecret = envVars.get("JWT_SECRET");
		if (jwtSecret == null || jwtSecret.isBlank()) {
			throw new IllegalStateException(
					"Environment variable JWT_SECRET is not set.");
		}

		FargateService authService = createFargateService(
				"AuthService",
				"auth-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"API_GATEWAY_PORT", "4004"))),
				authServiceDb,
				Map.of("JWT_SECRET", jwtSecret));
		authService.getNode().addDependency(authServiceDb);
		authService.getNode().addDependency(authServiceDbHealthCheck);

		FargateService billingService = createFargateService(
				"BillingService",
				"billing-service",
				List.of(
						Integer.parseInt(envVars.getOrDefault(
								"API_GATEWAY_PORT", "4001")),
						Integer.parseInt(envVars.getOrDefault(
								"GRPC_SERVER_PORT", "9001"))),
				null, null);

		FargateService analyticsService = createFargateService(
				"AnalyticsService",
				"analytics-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"API_GATEWAY_PORT", "4002"))),
				null, null);
		analyticsService.getNode().addDependency(mskCluster);

		FargateService patientService = createFargateService(
				"PatientService",
				"patient-service",
				List.of(Integer.parseInt(envVars.getOrDefault(
						"API_GATEWAY_PORT", "4000"))),
				patientServiceDb,
				Map.of(
						"BILLING_SERVICE_ADDRESS",
						"http://billing-service.patient-management.local:" + envVars.getOrDefault(
								"API_GATEWAY_PORT", "4001"),
						"BILLING_SERVICE_GRPC_PORT", envVars.getOrDefault(
								"GRPC_SERVER_PORT", "9001")));
		patientService.getNode().addDependency(patientServiceDb);
		patientService.getNode().addDependency(patientServiceDbHealthCheck);
		patientService.getNode().addDependency(billingService);
		patientService.getNode().addDependency(mskCluster);
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
