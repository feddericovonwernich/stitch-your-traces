from langchain_community.llms.ollama import Ollama

from src.story_worker.llm_provider import LLMProvider
from src.story_worker.config import settings

class OllamaProvider(LLMProvider):
    """
    Provider for generating creative stories using the Ollama LLM via LangChain.

    Attributes:
        llm (Ollama): The Ollama language model instance.

    Methods:
        generate(title: str, content: str) -> str:
            Asynchronously generates a short story based on the given title and content.
    """

    def __init__(self, **kwargs):
        self.llm = Ollama(
            url=settings.ollama_api_url,
            model=settings.ollama_model,
            **kwargs
        )

    async def generate(self, title: str, content: str) -> str:
        """
        Asynchronously generates a compelling short story based on the provided title and content.

        Args:
            title (str): The title of the story.
            content (str): The brief or prompt for the story.

        Returns:
            str: The generated short story as a string.
        """
        prompt = f"""You are a creative writer. Given the following story brief, write a compelling short story.
        
            Title: {title}
            
            Brief: {content}
            
            Story:
        """

        response = await self.llm.agenerate([prompt])

        return response.generations[0][0].text.strip()
