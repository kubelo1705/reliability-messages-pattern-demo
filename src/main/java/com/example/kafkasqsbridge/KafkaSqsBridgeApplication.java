package com.example.kafkasqsbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaSqsBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSqsBridgeApplication.class, args);
    }
} 