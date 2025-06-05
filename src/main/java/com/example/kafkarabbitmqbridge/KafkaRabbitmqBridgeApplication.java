package com.example.kafkarabbitmqbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaRabbitmqBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaRabbitmqBridgeApplication.class, args);
    }
} 