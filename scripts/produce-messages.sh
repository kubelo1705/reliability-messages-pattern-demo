#!/bin/bash

# Configuration
KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
TOPIC="input-topic"
NUM_MESSAGES=1000
BATCH_SIZE=100
KAFKA_CONTAINER=$(docker ps | grep kafka | awk '{print $1}')

# Function to generate a message with the exact format
generate_message() {
    local id=$(uuidgen)
    local content="Message content at $(date '+%Y-%m-%d %H:%M:%S')"
    local source="producer-script"
    local timestamp=$(date +%s%3)
    
    echo "{\"id\":\"$id\",\"content\":\"$content\",\"source\":\"$source\",\"timestamp\":$timestamp}"
}

# Function to produce messages
produce_messages() {
    local start=$1
    local end=$2
    for ((i=start; i<end; i++)); do
        message=$(generate_message)
        echo "$message" | docker exec -i $KAFKA_CONTAINER kafka-console-producer \
            --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS \
            --topic $TOPIC
    done
}

# Create multiple background processes to produce messages in parallel
for ((i=0; i<NUM_MESSAGES; i+=BATCH_SIZE)); do
    end=$((i + BATCH_SIZE))
    if [ $end -gt $NUM_MESSAGES ]; then
        end=$NUM_MESSAGES
    fi
    produce_messages $i $end &
done

# Wait for all background processes to complete
wait

echo "Finished producing $NUM_MESSAGES messages to topic $TOPIC" 