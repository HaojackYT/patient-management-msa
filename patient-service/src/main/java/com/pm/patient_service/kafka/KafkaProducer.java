package com.pm.patient_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.patient_service.exception.GlobalExceptionHandler;
import com.pm.patient_service.model.Patient;

import patient.events.PatientEvent;

@Service
public class KafkaProducer {

    // kafkaTemplate is used to send messages (events) to Kafka topics with
    // key-value pairs.
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(
            GlobalExceptionHandler.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {

        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED") // Describe a particular event inside of a topic
                .build();

        try {
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PATIENT_CREATED event: {}", patientEvent);
        }
    }
}
