package com.pm.patient_service.annotation.validators;

import com.pm.patient_service.annotation.ValidMultipleBirthChoice;
import com.pm.patient_service.dto.PatientRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MultipleBirthChoiceValidator implements
        ConstraintValidator<ValidMultipleBirthChoice, PatientRequestDTO> {

    @Override
    public boolean isValid(PatientRequestDTO dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        // {}
        // {"multipleBirthBoolean": false}
        // {"multipleBirthBoolean": true}
        // {"multipleBirthInteger": 2}
        return !(dto.getMultipleBirthBoolean() != null && dto.getMultipleBirthInteger() != null);
    }

}
