package com.pm.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class MiniStack extends Stack {

    private final Vpc vpc;

    private Vpc createVpc() {

        return Vpc.Builder.create(this, "PatientManagementVpc")
                .vpcName("PatientManagementVpc")
                .maxAzs(2) // Avalilability Zones: 2
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
                        // try 3 times once every 30 seconds before report a failure
                        .failureThreshold(3)
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
