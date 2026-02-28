package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;

public class PaitentMapper {
    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setDateofBirth(patient.getDateOfBirth().toString());
        return patientDTO;
    }
}
