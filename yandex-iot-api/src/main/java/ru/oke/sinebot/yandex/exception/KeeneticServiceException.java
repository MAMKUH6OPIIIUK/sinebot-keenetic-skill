package ru.oke.sinebot.yandex.exception;

import lombok.Getter;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;

@Getter
public class KeeneticServiceException extends RuntimeException {
    private ErrorDto error;

    public KeeneticServiceException(ErrorDto error) {
        super();
        this.error = error;
    }
}
