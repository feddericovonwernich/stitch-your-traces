from abc import ABC, abstractmethod

class LLMProvider(ABC):
    @abstractmethod
    async def generate(self, title: str, content: str) -> str:
        """Generate a story given title and content."""
        pass
