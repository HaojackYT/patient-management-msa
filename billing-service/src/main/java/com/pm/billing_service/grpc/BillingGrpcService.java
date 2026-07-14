package com.pm.billing_service.grpc;

import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    
    private static final Logger log = LoggerFactory.getLogger(
        BillingGrpcService.class);

    // StreamObserver once the client establishes a connection with the server,
    // they can exchange data in real time
    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount has received a request:\n" + billingRequest.toString());

        // Business logic

        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId(UUID.randomUUID().toString())
                .setStatus("ACTIVE")
                .build();

        // Send (multiple) response back to the client
        responseObserver.onNext(billingResponse);
        // End the cycle in this response
        responseObserver.onCompleted();
    }
}
