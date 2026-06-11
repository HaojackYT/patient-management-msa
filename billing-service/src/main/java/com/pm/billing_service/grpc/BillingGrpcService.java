package com.pm.billing_service.grpc;

import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.Logger;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(
            BillingGrpcService.class);

    // StreamObserver once the clients establishes a connection with the server,
    // they can exchange data in real time
    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount has received a request:\n" + billingRequest.toString());

        // Business logic

        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        // Send (multiple) response back to the client
        responseObserver.onNext(billingResponse);
        // End the cycle in this response
        responseObserver.onCompleted();
    }
}
