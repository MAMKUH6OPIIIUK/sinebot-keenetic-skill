package ru.oke.sinebot.oauth.component.provider;

import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ru.oke.sinebot.oauth.component.crypto.Encoder;
import ru.oke.sinebot.oauth.component.crypto.Sha256Encoder;
import ru.oke.sinebot.oauth.model.UsernamePasswordAuthenticationTokenWithKey;

/**
 * Расширение для {@link DaoAuthenticationProvider}, позволяющее получить доступ к введенному пользователем паролю
 * после успешной аутентификации и до очистки (erase) credentials.
 * Не переопеределяет никакую логику аутентификации пользователей из исходной реализации провайдера, но подменяет
 * итоговый объект успешной аутентификации {@link Authentication} на кастомную реализацию, содержащую хэш пароля
 * Для повышения безопасности следует убедиться, что для создания ключа шифрования на основе введенного пароля
 * используется алгоритм, отличный от noop или алгоритма, используемого для хранения паролей в базе данных.
 * <p>
 * Провайдер требует полной настройки через Java-based конфигурацию.
 *
 * @author k.oshoev
 */
@Setter
public class PasswordHashingDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @NonNull
    private Encoder keyEncoder;

    public PasswordHashingDaoAuthenticationProvider() {
        super();
        Encoder defaultEncoder = new Sha256Encoder();
        setKeyEncoder(defaultEncoder);
    }

    /**
     * Метод, переопределяющий действия, выполняемые провайдером после успешной аутентификации. Все необходимые действия
     * делегируются родительской реализации, а после их завершения на основе объекта успешной аутентификации
     * инициализируется объект класса {@link UsernamePasswordAuthenticationTokenWithKey}, содержащий хэш введенного
     * пользователем пароля и возвращаемый в качестве результата
     *
     * @param principal
     * @param authentication
     * @param user
     * @return
     */
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
                                                         UserDetails user) {
        Authentication successAuthentication = super.createSuccessAuthentication(principal, authentication, user);
        UsernamePasswordAuthenticationTokenWithKey result = new UsernamePasswordAuthenticationTokenWithKey(
                successAuthentication.getPrincipal(), successAuthentication.getCredentials(),
                successAuthentication.getAuthorities());
        String clearPassword = successAuthentication.getCredentials().toString();
        String encryptionKey = keyEncoder.encode(clearPassword);
        result.setEncryptionKey(encryptionKey);
        return result;
    }
}
