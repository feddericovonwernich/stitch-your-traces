import logging

from aiokafka import AIOKafkaConsumer
from opentelemetry import trace, propagate, context

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

    tracer = trace.get_tracer(__name__)

    try:
        async for msg in consumer:
            # ---- Propagation ---------------------------------------------------
            headers_map = {k: v.decode() for k, v in (msg.headers or [])}
            ctx = propagate.extract(headers_map)  # Remote context from Kafka headers
            token = context.attach(ctx)  # Make it the current context

            try:
                # ---- Processing ------------------------------------------------
                with tracer.start_as_current_span(
                        "consume", context=context.get_current()
                ) as span:
                    logger.info("Processing message: %s", msg)
                    await process_message(msg.value)
            except Exception:
                logger.exception("Error processing message=%s", msg)
            finally:
                context.detach(token)
    finally:
        await consumer.stop()
