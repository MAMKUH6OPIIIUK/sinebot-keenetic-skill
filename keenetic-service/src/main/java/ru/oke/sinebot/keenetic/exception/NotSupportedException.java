package ru.oke.sinebot.keenetic.exception;

public class NotSupportedException extends RuntimeException {
    public NotSupportedException(String message) {
        super(message);
    }

    public NotSupportedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
