-- Добавляем новые поля в video_streams (по одной колонке для H2)
ALTER TABLE video_streams ADD COLUMN is_active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE video_streams ADD COLUMN camera_location VARCHAR(255);
ALTER TABLE video_streams ADD COLUMN thumbnail_url VARCHAR(255);

-- Создаём таблицу для снимков с камер
CREATE TABLE video_snapshots (
                                 id UUID NOT NULL,
                                 created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 stream_id UUID NOT NULL,
                                 image_url VARCHAR(255) NOT NULL,
                                 timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 description TEXT,
                                 CONSTRAINT pk_video_snapshots PRIMARY KEY (id)
);

-- Добавляем foreign key для video_snapshots
ALTER TABLE video_snapshots ADD CONSTRAINT fk_video_snapshots_stream FOREIGN KEY (stream_id) REFERENCES video_streams(id) ON DELETE CASCADE;

-- Создаём индексы для быстрого поиска
CREATE INDEX idx_video_snapshots_stream_timestamp ON video_snapshots(stream_id, timestamp);