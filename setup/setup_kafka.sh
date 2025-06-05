#!/bin/sh

set -e

echo "⏳ Waiting for Kafka broker to be ready..."

# Wait for Kafka (basic wait, replace with health check if needed)
sleep 10

echo "✅ Creating Kafka topic 'my-topic'..."
kafka-topics --create --topic input-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

echo "🎉 Initialization complete."
