package com.myorg;

import software.amazon.awscdk.App;

public class DeliveryAwsInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        DeliveryAwsVpcStack deliveryVpcStack = new DeliveryAwsVpcStack(app, "Vpc");
        DeliveryAwsClusterStack deliveryAwsClusterStack = new DeliveryAwsClusterStack(app,"Cluster",deliveryVpcStack.getVpc());
        deliveryAwsClusterStack.addDependency(deliveryVpcStack);

        DeliveryAwsRdsStack rdsStack = new DeliveryAwsRdsStack(app, "Rds",deliveryVpcStack.getVpc());
        rdsStack.addDependency(deliveryVpcStack);

        DeliveryAwsServiceStack deliveryAwsServiceStack = new DeliveryAwsServiceStack(app,"Service",deliveryAwsClusterStack.getCluster());
        deliveryAwsServiceStack.addDependency(deliveryAwsClusterStack);
        app.synth();
    }
}

