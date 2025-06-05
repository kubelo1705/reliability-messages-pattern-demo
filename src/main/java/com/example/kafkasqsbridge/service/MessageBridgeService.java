package com.example.kafkasqsbridge.service;

import com.example.kafkasqsbridge.model.MessageKafka;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
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
    public Function<Message<MessageKafka>, Message<MessageKafka>> process() {
        return message -> {
            try {
                Acknowledgment ack = message.getHeaders().get("kafka_acknowledgment", Acknowledgment.class);
                MessageKafka payload = message.getPayload();
                
                String messageId = sqsService.sendMessage(payload);
                payload.setId(messageId);
                
                // Acknowledge the message after successful processing
                if (ack != null) {
                    ack.acknowledge();
                    log.debug("Message acknowledged: {}", messageId);
                }
                
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