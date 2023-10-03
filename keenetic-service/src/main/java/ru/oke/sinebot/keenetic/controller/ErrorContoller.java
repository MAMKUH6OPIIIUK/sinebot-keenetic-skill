package ru.oke.sinebot.keenetic.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.keenetic.dto.api.ValidationErrorDto;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.exception.NotSupportedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorContoller extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception ex, WebRequest request) {
        log.error("Возникло неожиданное исключение", ex);
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        ErrorDto responseBody = new ErrorDto(ErrorCode.INTERNAL_ERROR, stackTrace);
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.error("Обработка NotFoundException", ex);
        ErrorDto responseBody = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, ex.getMessage());
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = NotSupportedException.class)
    protected ResponseEntity<Object> handleNotSupportedException(NotFoundException ex, WebRequest request) {
        log.error("Обработка NotSupportedException", ex);
        ErrorDto responseBody = new ErrorDto(ErrorCode.INVALID_ACTION, ex.getMessage());
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Обработка AccessDeniedException", ex);
        ErrorDto responseBody = new ErrorDto(ErrorCode.DEVICE_NOT_FOUND, "Устройство удалено, " +
                "либо Вам не принадлежит");
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            List<String> messagesForField = fields.get(fieldName);
            if (messagesForField == null) {
                messagesForField = new ArrayList<>();
                messagesForField.add(errorMessage);
                fields.put(fieldName, messagesForField);
            } else {
                messagesForField.add(errorMessage);
            }
        });
        ValidationErrorDto responseBody = new ValidationErrorDto(ErrorCode.INVALID_ACTION,
                "Ошибка валидации запроса", fields);
        log.error("Ошибка валидации входящего запроса: {}", responseBody);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }
}
