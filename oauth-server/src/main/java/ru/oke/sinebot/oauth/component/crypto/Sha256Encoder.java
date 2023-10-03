package ru.oke.sinebot.oauth.component.crypto;

import org.springframework.security.crypto.codec.Utf8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Реализация {@link Encoder}, использующая для хэширования исходного текста алгоритм SHA-256
 * Часть кода честно получена методом ctrl+c ctrl+v из PasswordEncoder'ов spring security
 *
 * @author k.oshoev
 */
public class Sha256Encoder implements Encoder {
    private static final String FIXED_SALT = "U2luZWJvdCBsaXR0bGUgc2VjcmV0IHNhbHQ6IGZhc2hpb24gaXMgbXkgcHJvZmVzc2lvbg==";

    private static final String ALGORITHM = "SHA-256";

    private static final int ITERATIONS = 2;

    /**
     * Реализация метода хэширования исходного текста при помощи алгоритма SHA-256. Перед применением алгоритма к
     * исходному тексту добавляется фиксированная соль
     *
     * @param plainText текст с чувствительной информацией, хэш которой необходимо получить
     * @return результат хэширования
     */
    @Override
    public String encode(String plainText) {
        String saltedText = plainText + FIXED_SALT;
        byte[] digest = digest(Utf8.encode(saltedText));
        return Utf8.decode(Base64.getEncoder().encode(digest));
    }

    private byte[] digest(byte[] value) {
        MessageDigest messageDigest = createDigest();
        for (int i = 0; i < ITERATIONS; i++) {
            value = messageDigest.digest(value);
        }
        return value;
    }

    private static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No such hashing algorithm", ex);
        }
    }
}
