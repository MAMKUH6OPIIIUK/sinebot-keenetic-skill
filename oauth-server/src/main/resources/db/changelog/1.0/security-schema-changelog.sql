--liquibase formatted sql

--
--changeset k.oshoev:1
CREATE TABLE IF NOT EXISTS users
(
    user_id bigserial NOT NULL PRIMARY KEY,
    login varchar(50) NOT NULL UNIQUE,
    password varchar(500) NOT NULL,
    enabled boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities
(
    user_id bigint NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username on authorities (user_id,authority);