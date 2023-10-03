package ru.oke.sinebot.keenetic.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AccessPointChangeStatusValidator.class)
@Documented
public @interface UniqueAccessPoints {
    String message() default "Точки доступа должны быть уникальны";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
