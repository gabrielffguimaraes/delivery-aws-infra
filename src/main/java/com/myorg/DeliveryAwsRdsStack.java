package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;

public class DeliveryAwsRdsStack extends Stack {
    public DeliveryAwsRdsStack(final Construct scope, final String id,final Vpc vpc) {
        this(scope, id, null,vpc);
    }

    public DeliveryAwsRdsStack(final Construct scope, final String id, final StackProps props,final Vpc vpc) {
        super(scope, id, props);

        CfnParameter senha = CfnParameter.Builder.create(this,"senha")
                .type("string")
                .description("Senha do database delivery-order")
                .build();

        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId( this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));


        DatabaseInstance database = DatabaseInstance.Builder
                .create(this, "Rds-orders")
                .instanceIdentifier("aws-delivery-order-db")
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_8_0)
                        .build()))
                .vpc(vpc)
                .credentials(Credentials.fromUsername("root",
                CredentialsFromUsernameOptions.builder()
                        .password(SecretValue.unsafePlainText(senha.getValueAsString()))
                        .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .multiAz(false)
                .allocatedStorage(10)
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets())
                        .build())
                .build();

        CfnOutput.Builder.create(this, "delivery-order-db-endpoint")
                .exportName("delivery-order-db-endpoint")
                .value(database.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create( this, "delivery-order-db-senha")
                .exportName("delivery-order-db-senha")
                .value(senha.getValueAsString())
                .build();
        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "DeliveryAwsInfraQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();
    }
}
