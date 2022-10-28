package com.example.herokudemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HerokuDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HerokuDemoApplication.class, args);
    }

}
