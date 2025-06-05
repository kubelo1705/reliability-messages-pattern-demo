# Kafka-RabbitMQ Bridge

A Spring Boot application that acts as a bridge between Apache Kafka and RabbitMQ, helping to manage high traffic by buffering messages in RabbitMQ and reducing Kafka lag.

## Features

- Consumes messages from Kafka topics
- Forwards messages to RabbitMQ queues
- Manual acknowledgment for reliable message processing
- Error handling and logging
- Configurable through application.yml
- Implements Inbox Pattern for reliable message processing

## Prerequisites

- Java 17 or higher
- Maven
- Apache Kafka
- RabbitMQ
- Docker (optional)
- kafkacat (for testing)

## Quick Start

### Using Docker Compose (Recommended)

1. Start the entire stack:
```bash
docker-compose up -d
```

2. Verify services are running:
```bash
docker-compose ps
```

3. Check logs:
```bash
docker-compose logs -f
```
## Testing the Application

### 1. Create Kafka Topic

```bash
docker exec -it demo-reliability-kafka-1 kafka-topics --create --topic input-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

```bash
docker exec -it demo-reliability-localstack-1 aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name output-queue --region us-east-1
```

### 2. Send Test Messages

Using the provided script:
```bash
./scripts/produce-messages.sh
```

Or manually:
```bash
KAFKA_CONTAINER=$(docker ps | grep kafka | awk '{print $1}') && KAFKA_BOOTSTRAP_SERVERS="localhost:9092" && TOPIC="input-topic" && message='{"id":"test-1","content":"test message","source":"test","timestamp":1234567890}' && echo "$message" | docker exec -i $KAFKA_CONTAINER kafka-console-producer --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --topic $TOPIC
```

### 3. Verify Message Flow

1. Check Kafka consumer lag:
```bash
docker exec -i $(docker ps | grep kafka | awk '{print $1}') kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group kafka-sqs-bridge-group
```