# main.py
import asyncio
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI

from src.story_worker.consumers.kafka_consumer import consume_loop

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

if __name__ == "__main__":
    uvicorn.run("src.story_worker.main:app", host="0.0.0.0", port=8000, reload=True)
