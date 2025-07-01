import json
import os

from src.story_worker.providers.openai_provider import OpenAIProvider
from .llm_provider import LLMProvider
from .providers.ollama_provider import OllamaProvider
from .story_client import StoryClient

# Instantiate LLM provider
# Choose provider via $LLM_PROVIDER (default=openai)
provider_name = os.getenv("LLM_PROVIDER", "openai").lower()
if provider_name == "ollama":
    provider: LLMProvider = OllamaProvider()
else:
    provider: LLMProvider = OpenAIProvider(temperature=0.7)

# Instantiate client.
client = StoryClient()

async def process_message(msg: bytes):
        """
        Process an incoming message containing story data.

        Args:
            msg (bytes): A JSON-encoded message with keys 'title', 'content', and 'id'.

        Workflow:
            1. Parse the message.
            2. Extract title, content, and story ID.
            3. Generate a story using the selected LLM provider.
            4. Save the generated story using the StoryClient.
        """
        data = json.loads(msg)

        title = data.get("title")
        content = data.get("content")
        story_id = data.get("id")

        generated_story = await provider.generate(title=title, content=content)

        await client.save_story(story_id, title, generated_story)
