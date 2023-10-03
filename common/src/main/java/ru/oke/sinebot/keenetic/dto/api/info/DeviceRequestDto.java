package ru.oke.sinebot.keenetic.dto.api.info;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * DTO для запросов на добавление или обновление пользовательского роутера в системе
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
public class DeviceRequestDto {
    private Long id;

    private Long userId;

    @NotNull(message = "{device-model-field-should-be-positive}")
    private Long modelId;

    @NotBlank(message = "{device-name-field-should-not-be-blank}")
    @Size(min = 1, max = 500, message = "{device-name-field-should-has-expected-size}")
    private String name;

    @Size(max = 500, message = "{device-description-field-should-has-expected-size}")
    private String description;

    @Size(min = 4, max = 253, message = "{device-domain-name-field-should-has-expected-size}")
    @Pattern(regexp = "^(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]$",
            message = "{device-domain-name-field-should-be-correct-fqdn}")
    private String domainName;

    @Size(min = 1, max = 50, message = "{device-login-field-should-has-expected-size}")
    private String login;

    @ToString.Exclude()
    @Size(min = 5, max = 15, message = "{device-password-field-should-has-expected-size}")
    private String password;
}
