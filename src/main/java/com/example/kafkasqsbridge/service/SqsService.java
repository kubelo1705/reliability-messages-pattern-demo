package com.example.kafkasqsbridge.service;

import com.example.kafkasqsbridge.model.MessageKafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsService {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue.output}")
    private String queueUrl;

    public String sendMessage(MessageKafka message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);
            log.info("Message sent to SQS - MessageId: {}, Content: {}", response.messageId(), messageBody);
            return response.messageId();
        } catch (Exception e) {
            log.error("Failed to send message to SQS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
} 