package com.example.kafkasqsbridge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageKafka {
    private String id;
    private String content;
    private String source;
    private long timestamp;
} 