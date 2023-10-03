package ru.oke.sinebot.oauth.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oke.sinebot.oauth.dto.UserRequestDto;
import ru.oke.sinebot.oauth.dto.UserResponseDto;
import ru.oke.sinebot.oauth.exception.AlreadyExistsException;
import ru.oke.sinebot.oauth.mapper.UserMapper;
import ru.oke.sinebot.oauth.model.User;
import ru.oke.sinebot.oauth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @CircuitBreaker(name = "security-service")
    @Transactional
    public UserResponseDto registerUser(UserRequestDto user) {
        this.validateNewUser(user);
        User newUser = this.userMapper.mapToDefaultUser(user);
        User createdUser = this.userRepository.save(newUser);
        return this.userMapper.mapToUserResponseDto(createdUser);
    }

    private void validateNewUser(UserRequestDto user) {
        long countByLogin = this.userRepository.countByLogin(user.getLogin());
        if (countByLogin > 0) {
            throw new AlreadyExistsException("Пользователь с указанным логином уже зарегистрирован");
        }
    }
}
