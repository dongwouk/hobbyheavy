package com.example.hobbyheavy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HobbyheavyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HobbyheavyApplication.class, args);
    }

}
