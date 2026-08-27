package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidDeceasedChoice;
import com.pm.patient_service.dto.PatientRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DeceasedChoiceValidator implements ConstraintValidator<ValidDeceasedChoice, PatientRequestDTO> {

    @Override
    public boolean isValid(PatientRequestDTO dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        // {}
        // {"deceasedBoolean": false}
        // {"deceasedBoolean": true}
        // {"deceasedDateTime": "2026-08-24T03:15:30Z"}
        return !(dto.getDeceasedBoolean() != null && dto.getDeceasedDateTime() != null);
    }

}
