import logging

from aiokafka import AIOKafkaConsumer
from src.story_worker.config import settings
from src.story_worker.processor import process_message


logger = logging.getLogger(__name__)


async def consume_loop():
    """
    Asynchronously consumes messages from a Kafka topic and processes them.

    - Initializes an `AIOKafkaConsumer` with the configured topic, bootstrap servers, and group ID.
    - Starts the consumer and enters an asynchronous loop to process incoming messages.
    - For each message, calls `process_message` with the message value.
    - Logs any exceptions that occur during message processing.
    - Ensures the consumer is stopped gracefully on exit.

    Raises:
        Any exception raised by `process_message` will be logged but not propagated.
    """

    consumer = AIOKafkaConsumer(
        settings.kafka_topic,
        bootstrap_servers=settings.kafka_bootstrap_servers,
        group_id="story-generator-group",
        value_deserializer=lambda v: v
    )

    await consumer.start()
    logger.info("Kafka consumer started, listening to topic: %s", settings.kafka_topic)

    try:
        async for msg in consumer:
            try:
                logger.exception("Processing message: %s", msg)
                await process_message(msg.value)
            except Exception:
                logger.exception("Error processing message=%s", msg)
    finally:
        await consumer.stop()
