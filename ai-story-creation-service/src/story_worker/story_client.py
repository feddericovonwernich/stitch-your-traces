import httpx
from .config import settings
from .schemas import SaveStoryRequest

class StoryClient:
    """
    Client for interacting with the story service API.
    """

    def __init__(self, base_url: str = settings.story_service_url):
        """
        Initialize the StoryClient.

        Args:
            base_url (str): The base URL of the story service API.
        """
        self.base_url = base_url

    async def save_story(self, story_id: int, title: str, content: str):
        """
        Save or update a story by sending a PUT request to the story service.

        Args:
            story_id (int): The ID of the story to save.
            title (str): The title of the story.
            content (str): The content of the story.

        Raises:
            httpx.HTTPStatusError: If the response status is an error.
        """
        url = f"{self.base_url}/{story_id}"

        save_req = SaveStoryRequest(title=title, content=content)

        async with httpx.AsyncClient() as client:
            response = await client.put(url, json=save_req.dict())
            response.raise_for_status()