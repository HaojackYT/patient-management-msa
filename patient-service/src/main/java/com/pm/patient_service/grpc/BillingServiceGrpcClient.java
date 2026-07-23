package com.pm.patient_service.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import billing.BillingServiceGrpc.BillingServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class BillingServiceGrpcClient {
	// BillingServiceBlockingStub provides the client synchronous/blocking calls to
	// the gRPC server
	private final BillingServiceBlockingStub blockingStub;
	private static final Logger log = LoggerFactory.getLogger(
			BillingServiceGrpcClient.class);

	// Environment variables is used to configure the gRPC server
	// address (localhost/aws.grpc) and port (9001/...)
	public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String serverAddress,
			@Value("${billing.service.grpc.port:9001}") int serverPort) {

		log.info("Connecting to Billing Service gRPC at {}:{}", serverAddress, serverPort);

		ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
				.usePlaintext() // Disable encryption for local development and local testing
				.build();
		this.blockingStub = BillingServiceGrpc.newBlockingStub(channel);
	}

	public BillingResponse createBillingAccount(String patientId, String name, String email) {

		BillingRequest request = BillingRequest.newBuilder()
				.setPatientId(patientId)
				.setName(name)
				.setEmail(email)
				.build();

		BillingResponse response = blockingStub.createBillingAccount(request);

		log.info("Received response from Billing Service via gRPC: {}", response);

		return response;
	}
}
