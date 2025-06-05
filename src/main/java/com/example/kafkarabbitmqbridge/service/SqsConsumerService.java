package com.example.kafkarabbitmqbridge.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsConsumerService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue.output}")
    private String queueUrl;

    @PostConstruct
    public void init() {
        log.info("Initializing SQS Consumer Service");
        log.info("Queue URL: {}", queueUrl);
    }

    @Scheduled(fixedDelay = 1000) // Poll every second
    public void pollMessages() {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message message : messages) {
                log.info("Received message from SQS - MessageId: {}, Body: {}", 
                    message.messageId(), 
                    message.body());

                // Delete the message after processing
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                
                sqsClient.deleteMessage(deleteRequest);
            }
        } catch (Exception e) {
            log.error("Error polling messages from SQS: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SQS Consumer Service");
    }
} 