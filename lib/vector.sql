CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
                              id TEXT PRIMARY KEY,
                              content TEXT,
                              metadata JSONB,
                              embedding VECTOR(768)  -- nomic-embed-text 模型的维度是768
);
