package ru.oke.sinebot.oauth.config;

/**
 * Константы с наименованиями кастомных claim для oauth2 токенов
 */
public class CustomClaimConstants {
    /**
    наименование (ключ) claim, содержащего идентификатор аутентифицированного пользователя
     */
    public static final String KEY_USER_ID = "internal.sec.user.id";

    /**
    наименование (ключ) claim, содержащего хэш введенного аутентифицированным пользователем пароля
     */
    public static final String KEY_USER_ENCRYPTION_PASS = "internal.sec.key";
}
