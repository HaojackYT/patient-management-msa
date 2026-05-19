package com.pm.patient_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;

import jakarta.validation.groups.Default;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        return ResponseEntity.ok(patientService.getPatients());
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({ Default.class,
            CreatePatientValidationGroup.class }) @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok(patientResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id,
            @Validated({ Default.class }) @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }
}
