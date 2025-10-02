package com.sparta.goatgam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class GoatgamApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoatgamApplication.class, args);
    }

}
