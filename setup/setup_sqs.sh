#!/bin/sh

set -e

echo "⏳ Waiting for LocalStack SQS..."
# Wait for localstack to start up
sleep 10

echo "✅ Creating SQS queue 'my-queue'..."
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name output-queue --region us-east-1

echo "🎉 Initialization complete."
