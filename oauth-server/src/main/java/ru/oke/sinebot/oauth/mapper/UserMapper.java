package ru.oke.sinebot.oauth.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.oauth.dto.UserRequestDto;
import ru.oke.sinebot.oauth.dto.UserResponseDto;
import ru.oke.sinebot.oauth.model.Authority;
import ru.oke.sinebot.oauth.model.AuthorityId;
import ru.oke.sinebot.oauth.model.User;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User mapToDefaultUser(UserRequestDto userDto) {
        User user = new User();
        user.setLogin(userDto.getLogin());
        String plainTextPassword = userDto.getPassword();
        String password = passwordEncoder.encode(plainTextPassword);
        user.setPassword(password);
        user.setEnabled(true);
        AuthorityId authorityId = new AuthorityId();
        authorityId.setAuthority("ROLE_USER");
        Authority authority = new Authority(authorityId);
        user.setAuthorities(Set.of(authority));
        authority.setUser(user);
        return user;
    }

    public UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setPassword(user.getPassword());
        return dto;
    }
}
