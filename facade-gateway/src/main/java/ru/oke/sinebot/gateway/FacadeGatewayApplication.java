package ru.oke.sinebot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FacadeGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FacadeGatewayApplication.class, args);
    }
}
