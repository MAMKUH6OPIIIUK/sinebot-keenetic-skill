package ru.oke.sinebot.oauth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorityId {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "authority", nullable = false, length = 50)
    private String authority;
}
