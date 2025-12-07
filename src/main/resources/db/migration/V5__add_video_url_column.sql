-- ===========================================
-- Добавление колонки video_url для видеопотоков
-- ===========================================

-- Добавляем колонку video_url в таблицу construction_stages
ALTER TABLE construction_stages ADD COLUMN IF NOT EXISTS video_url VARCHAR(500);

-- Добавляем комментарий к колонке
COMMENT ON COLUMN construction_stages.video_url IS 'URL видеопотока для этапа строительства';

-- Обновляем существующие записи (если нужно)
UPDATE construction_stages SET video_url = 'rtsp://stream.example.com/stage-' || id WHERE video_url IS NULL;

-- Создаем индекс для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_construction_stages_video_url ON construction_stages(video_url) WHERE video_url IS NOT NULL;