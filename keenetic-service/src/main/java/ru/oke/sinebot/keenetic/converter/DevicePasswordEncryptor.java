package ru.oke.sinebot.keenetic.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.service.TokenService;
import ru.oke.sinebot.oauth.config.CustomClaimConstants;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Конвертер для шифрования\дешифрования паролей от устройств пользователя.
 * Шифрование выполняется на основе ключа, сгенерированного на основе хэша введенного пользователем пароля (который
 * хранится как claim токена аутентификации). Используется не самый надежный режим AES (ECB), но и так сойдёёёёт :)
 * Если реализовывать функционал изменения пароля УЗ пользователя в системе, нужно будет решать вопрос с обновлением
 * паролей от устройств в БД, иначе все они перестанут быть доступны
 *
 * @author k.oshoev
 */
@Component
public class DevicePasswordEncryptor implements AttributeConverter<String, String> {
    private static final String SALT = "uzumymw";

    private final TokenService tokenService;

    private final Cipher cipher;

    public DevicePasswordEncryptor(TokenService tokenService) throws Exception {
        this.tokenService = tokenService;
        this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    @Override
    public String convertToDatabaseColumn(String plainTextPassword) {
        try {
            Key key = this.getKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainTextPassword.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encryptedPassword) {
        try {
            Key key = this.getKey();
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedPassword)));
        } catch (Exception e) {
            return "***ENCRYPTED***";
        }
    }

    private SecretKey getKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String userSecret = (String) this.tokenService
                .getTokenClaimValue(CustomClaimConstants.KEY_USER_ENCRYPTION_PASS);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(userSecret.toCharArray(), SALT.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

}
