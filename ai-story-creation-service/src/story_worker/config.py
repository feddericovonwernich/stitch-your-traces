from pydantic_settings import BaseSettings


class Settings(BaseSettings):

    kafka_bootstrap_servers: str
    kafka_topic: str
    story_service_url: str

    openai_api_key: str
    ollama_api_url: str
    ollama_model: str

    def __init__(self, **values):
        super().__init__(**values)
        missing = [
            name for name in [
                "kafka_bootstrap_servers",
                "kafka_topic",
                "story_service_url"
            ] if not getattr(self, name)
        ]
        if missing:
            raise ValueError(f"Missing required settings: {', '.join(missing)}")

        if not self.openai_api_key:
            if not (self.ollama_api_url and self.ollama_model):
                raise ValueError(
                    "Either `openai_api_key` must be set, or both `ollama_api_url` and `ollama_model` must be set."
                )

    class Config:
        env_file = ".env"

settings = Settings()

# Configure logging
import logging

logging.basicConfig(
    level=logging.INFO,
    format="[%(asctime)s] %(levelname)s %(name)s: %(message)s",
    datefmt="%Y-%m-%dT%H:%M:%S%z",
)
