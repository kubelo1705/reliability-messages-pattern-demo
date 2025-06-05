package com.example.kafkasqsbridge.service;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsConsumerService {

    private final SqsClient sqsClient;
    private final AtomicReference<CompletableFuture<Void>> currentTask = new AtomicReference<>();

    @Value("${aws.sqs.queue.output}")
    private String queueUrl;

    @PostConstruct
    public void init() {
        log.info("Initializing SQS Consumer Service");
        log.info("Queue URL: {}", queueUrl);
    }

    @Scheduled(fixedRate = 1000)
    public void pollMessages() throws InterruptedException {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(5)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        // simulate real process
        Thread.currentThread().sleep(1000);

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
    }

    @PreDestroy
    public void shutdown() {
        CompletableFuture<Void> task = currentTask.get();
        if (task != null && !task.isDone()) {
            task.join(); // Wait for the current task to complete before shutting down
        }
        log.info("Shutting down SQS Consumer Service");
    }
} 