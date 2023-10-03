package ru.oke.sinebot.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.oauth.validator.PasswordMatches;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PasswordMatches(message = "{user-passwords-fields-should-match}")
public class UserRequestDto {
    @NotBlank(message = "{user-login-field-should-not-be-blank}")
    @Size(min = 1, max = 50, message = "{user-login-field-should-has-expected-size}")
    private String login;

    @NotBlank(message = "{user-password-field-should-not-be-blank}")
    @Size(min = 5, max = 50, message = "{user-password-field-should-has-expected-size}")
    private String password;

    private String matchingPassword;
}
