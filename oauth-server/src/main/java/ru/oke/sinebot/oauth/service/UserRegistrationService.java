package ru.oke.sinebot.oauth.service;

import ru.oke.sinebot.oauth.dto.UserRequestDto;
import ru.oke.sinebot.oauth.dto.UserResponseDto;

public interface UserRegistrationService {
    UserResponseDto registerUser(UserRequestDto user);
}
