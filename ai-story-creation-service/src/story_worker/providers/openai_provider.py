from langchain_core.prompts import PromptTemplate
from langchain.chains import LLMChain
from langchain_openai import ChatOpenAI

from src.story_worker.llm_provider import LLMProvider
from src.story_worker.config import settings

class OpenAIProvider(LLMProvider):
    """
    LLMProvider implementation using OpenAI's chat models via LangChain.

    This provider generates creative short stories based on a given title and brief.
    """

    def __init__(self, **kwargs):
        self.llm = ChatOpenAI(openai_api_key=settings.openai_api_key, **kwargs)
        self.prompt = PromptTemplate(
            input_variables=["title", "content"],
            template="""
                You are a creative writer. Given the following story brief, write a compelling short story.
                
                Title: {title}
                
                Brief: {content}
                
                Story:
            """
        )

    async def generate(self, title: str, content: str) -> str:
        """
        Generate a short story based on the provided title and content.

        Args:
            title (str): The title of the story.
            content (str): The brief or summary of the story.

        Returns:
            str: The generated short story.
        """
        chain = LLMChain(llm=self.llm, prompt=self.prompt)
        result = await chain.apredict(title=title, content=content)
        return result.strip()
