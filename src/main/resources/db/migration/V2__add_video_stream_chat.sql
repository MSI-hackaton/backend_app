-- Добавляем новые поля в video_streams
ALTER TABLE video_streams
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE NOT NULL,
ADD COLUMN IF NOT EXISTS camera_location VARCHAR(255),
ADD COLUMN IF NOT EXISTS thumbnail_url VARCHAR(255);

-- Создаём таблицу для снимков с камер (опционально)
CREATE TABLE IF NOT EXISTS video_snapshots (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    stream_id UUID NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description TEXT,
    CONSTRAINT fk_video_snapshots_stream FOREIGN KEY (stream_id) REFERENCES video_streams(id) ON DELETE CASCADE
);

-- Создаём индекс для быстрого поиска по запросу
CREATE INDEX IF NOT EXISTS idx_chat_messages_request_created ON chat_messages(request_id, created_at);
CREATE INDEX IF NOT EXISTS idx_video_streams_request ON video_streams(request_id);
CREATE INDEX IF NOT EXISTS idx_video_snapshots_stream_timestamp ON video_snapshots(stream_id, timestamp);