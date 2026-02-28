package com.pm.patient_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.mapper.PaitentMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(PaitentMapper::toDTO).toList();
    }
}
