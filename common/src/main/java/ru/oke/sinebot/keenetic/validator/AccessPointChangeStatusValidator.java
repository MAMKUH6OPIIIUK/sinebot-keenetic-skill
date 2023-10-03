package ru.oke.sinebot.keenetic.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.oke.sinebot.keenetic.dto.api.status.AccessPointChangeStatusRequestDto;
import ru.oke.sinebot.keenetic.dto.types.AccessPointType;
import ru.oke.sinebot.keenetic.dto.types.WifiFrequency;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessPointChangeStatusValidator implements ConstraintValidator<UniqueAccessPoints,
        List<AccessPointChangeStatusRequestDto>> {
    @Override
    public void initialize(UniqueAccessPoints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<AccessPointChangeStatusRequestDto> accessPointChangeStatusRequestDtos,
                           ConstraintValidatorContext constraintValidatorContext) {
        Set<AccessPointApiIdentifier> identifiers = accessPointChangeStatusRequestDtos.stream()
                .map(dto -> new AccessPointApiIdentifier(dto.getType(), dto.getBand()))
                .collect(Collectors.toSet());
        return identifiers.size() == accessPointChangeStatusRequestDtos.size();
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class AccessPointApiIdentifier {
        private AccessPointType type;

        private WifiFrequency band;
    }
}
