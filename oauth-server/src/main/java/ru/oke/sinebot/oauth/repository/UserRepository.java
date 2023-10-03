package ru.oke.sinebot.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oke.sinebot.oauth.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    long countByLogin(String login);
}
