package ru.oke.sinebot.yandex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class YandexApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(YandexApiApplication.class, args);
    }
}
