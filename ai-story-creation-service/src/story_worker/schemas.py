from pydantic import BaseModel

class SaveStoryRequest(BaseModel):
    """
    Pydantic model for saving a story.

    Attributes:
        title (str): The title of the story.
        content (str): The content of the story.
    """
    title: str
    content: str
