package com.example.pawgetherbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PawgetherBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawgetherBeApplication.class, args);
    }

}
