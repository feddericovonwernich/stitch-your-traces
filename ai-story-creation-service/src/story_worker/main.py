# main.py
import asyncio
import logging
import debugpy, os
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from src.story_worker.consumers.kafka_consumer import consume_loop

logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # ---- startup ----

    loop = asyncio.get_event_loop()
    loop.create_task(consume_loop())

    # Extra startup steps go here:

    yield

    # ---- shutdown ----
    # Clean up logic goes here:

app = FastAPI(lifespan=lifespan)

@app.get("/health")
async def health():
    return {"status": "ok"}

# If DEBUG is enabled, start listening for debug connections
if os.getenv("DEBUG", "0") == "1":
    debugpy.listen(("0.0.0.0", 5678))
    logger.info("debugpy waiting on :5678 …")

# Start listening for incoming connections
if __name__ == "__main__":
    uvicorn.run("src.story_worker.main:app", host="0.0.0.0", port=8000, reload=True)
