package com.example.kafkarabbitmqbridge.service;

import com.example.kafkarabbitmqbridge.model.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBridgeService {
    private final SqsService sqsService;

    @PostConstruct
    public void init() {
        log.info("Initializing Kafka-SQS bridge service");
    }

    @Bean
    public Function<Message, Message> process() {
        return message -> {
            try {
                String messageId = sqsService.sendMessage(message);
                message.setId(messageId);
                return message;
            } catch (Exception e) {
                log.error("Error processing message: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Kafka-SQS bridge service");
    }
} 