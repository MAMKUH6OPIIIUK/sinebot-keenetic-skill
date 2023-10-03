package ru.oke.sinebot.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OauthServerApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OauthServerApplication.class, args);
    }
}
