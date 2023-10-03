package ru.oke.sinebot.keenetic.dto.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class ValidationErrorDto extends ErrorDto {
    private Map<String, List<String>> fields;

    public ValidationErrorDto(ErrorCode errorCode, String errorMessage, Map<String, List<String>> fields) {
        super(errorCode, errorMessage);
        this.fields = fields;
    }
}
