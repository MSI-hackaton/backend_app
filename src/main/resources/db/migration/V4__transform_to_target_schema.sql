-- 1. Удаляем устаревшие таблицы и индексы (если есть)
DROP INDEX IF EXISTS idx_chat_messages_construction;
DROP INDEX IF EXISTS idx_chat_messages_sender;
DROP INDEX IF EXISTS idx_construction_stages_project;
DROP INDEX IF EXISTS idx_construction_stages_customer;
DROP INDEX IF EXISTS idx_construction_stages_specialist;
DROP INDEX IF EXISTS idx_documents_construction;
DROP INDEX IF EXISTS idx_video_streams_construction;

-- Удаляем таблицу video_snapshots если она существует
DROP TABLE IF EXISTS video_snapshots CASCADE;

-- 2. Обновляем таблицу users
-- Удаляем старые constraints если есть
ALTER TABLE users DROP CONSTRAINT IF EXISTS uc_users_email;
ALTER TABLE users DROP CONSTRAINT IF EXISTS uc_users_phone;

-- Добавляем новые constraints
ALTER TABLE users ADD CONSTRAINT uc_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uc_users_phone UNIQUE (phone);

-- 3. Обновляем construction_stages
-- Удаляем старые foreign keys
ALTER TABLE construction_stages DROP CONSTRAINT IF EXISTS FK_CONSTRUCTION_STAGES_ON_REQUEST;
ALTER TABLE construction_stages DROP CONSTRAINT IF EXISTS FK_CONSTRUCTION_STAGES_ON_PROJECT;
ALTER TABLE construction_stages DROP CONSTRAINT IF EXISTS FK_CONSTRUCTION_STAGES_ON_CUSTOMER;
ALTER TABLE construction_stages DROP CONSTRAINT IF EXISTS FK_CONSTRUCTION_STAGES_ON_SPECIALIST;

-- Добавляем новые foreign keys с правильными настройками каскада
ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_REQUEST
    FOREIGN KEY (request_id) REFERENCES construction_requests(id) ON DELETE CASCADE;

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_PROJECT
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_CUSTOMER
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_SPECIALIST
    FOREIGN KEY (specialist_id) REFERENCES users(id) ON DELETE SET NULL;

-- 4. Обновляем construction_requests
ALTER TABLE construction_requests
    DROP CONSTRAINT IF EXISTS FK_CONSTRUCTION_REQUESTS_ON_PROJECT;

ALTER TABLE construction_requests
    ADD CONSTRAINT FK_CONSTRUCTION_REQUESTS_ON_PROJECT
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- 5. Обновляем chat_messages
-- Сначала переименуем колонку text -> message если она еще не переименована
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'chat_messages' AND column_name = 'text'
    ) THEN
        ALTER TABLE chat_messages RENAME COLUMN text TO message;
    END IF;
END
$$;

-- Удаляем старый foreign key
ALTER TABLE chat_messages
    DROP CONSTRAINT IF EXISTS FK_CHAT_MESSAGES_ON_CONSTRUCTION;

-- Добавляем новый с каскадом
ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_CONSTRUCTION
    FOREIGN KEY (construction_id) REFERENCES construction_stages(id) ON DELETE CASCADE;

-- Обновляем foreign key для sender
ALTER TABLE chat_messages
    DROP CONSTRAINT IF EXISTS FK_CHAT_MESSAGES_ON_SENDER;

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_SENDER
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;

-- 6. Обновляем documents
-- Добавляем новые колонки если их нет
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'documents' AND column_name = 'reviewed_by'
    ) THEN
        ALTER TABLE documents ADD COLUMN reviewed_by UUID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'documents' AND column_name = 'description'
    ) THEN
        ALTER TABLE documents ADD COLUMN description TEXT;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'documents' AND column_name = 'original_filename'
    ) THEN
        ALTER TABLE documents ADD COLUMN original_filename VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'documents' AND column_name = 'file_size'
    ) THEN
        ALTER TABLE documents ADD COLUMN file_size BIGINT;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'documents' AND column_name = 'mime_type'
    ) THEN
        ALTER TABLE documents ADD COLUMN mime_type VARCHAR(100);
    END IF;
END
$$;

-- Удаляем старый foreign key
ALTER TABLE documents
    DROP CONSTRAINT IF EXISTS FK_DOCUMENTS_ON_CONSTRUCTION;

-- Добавляем новый с каскадом
ALTER TABLE documents
    ADD CONSTRAINT FK_DOCUMENTS_ON_CONSTRUCTION
    FOREIGN KEY (construction_id) REFERENCES construction_stages(id) ON DELETE CASCADE;

-- Добавляем foreign key для reviewed_by
ALTER TABLE documents
    ADD CONSTRAINT FK_DOCUMENTS_ON_REVIEWED_BY
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL;

-- 7. Обновляем video_streams
-- Проверяем и добавляем недостающие колонки
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'video_streams' AND column_name = 'is_active'
    ) THEN
        ALTER TABLE video_streams ADD COLUMN is_active BOOLEAN DEFAULT TRUE NOT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'video_streams' AND column_name = 'camera_location'
    ) THEN
        ALTER TABLE video_streams ADD COLUMN camera_location VARCHAR(255);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'video_streams' AND column_name = 'thumbnail_url'
    ) THEN
        ALTER TABLE video_streams ADD COLUMN thumbnail_url VARCHAR(255);
    END IF;
END
$$;

-- Удаляем старый foreign key
ALTER TABLE video_streams
    DROP CONSTRAINT IF EXISTS FK_VIDEO_STREAMS_ON_CONSTRUCTION;

-- Добавляем новый с каскадом
ALTER TABLE video_streams
    ADD CONSTRAINT FK_VIDEO_STREAMS_ON_CONSTRUCTION
    FOREIGN KEY (construction_id) REFERENCES construction_stages(id) ON DELETE CASCADE;

-- 8. Обновляем stage_reports
-- Удаляем старый foreign key
ALTER TABLE stage_reports
    DROP CONSTRAINT IF EXISTS FK_STAGE_REPORTS_ON_STAGE;

-- Добавляем новый с каскадом
ALTER TABLE stage_reports
    ADD CONSTRAINT FK_STAGE_REPORTS_ON_STAGE
    FOREIGN KEY (stage_id) REFERENCES construction_stages(id) ON DELETE CASCADE;

-- 9. Обновляем report_photos
-- Удаляем старый foreign key
ALTER TABLE report_photos
    DROP CONSTRAINT IF EXISTS FK_REPORT_PHOTOS_ON_REPORT;

-- Добавляем новый с каскадом
ALTER TABLE report_photos
    ADD CONSTRAINT FK_REPORT_PHOTOS_ON_REPORT
    FOREIGN KEY (report_id) REFERENCES stage_reports(id) ON DELETE CASCADE;

-- 10. Обновляем project_photos
-- Удаляем старые foreign keys
ALTER TABLE project_photos
    DROP CONSTRAINT IF EXISTS FK_PROJECT_PHOTOS_ON_PROJECT;
ALTER TABLE project_photos
    DROP CONSTRAINT IF EXISTS FK_PROJECT_PHOTOS_ON_PHOTO;

-- Добавляем новые с каскадом
ALTER TABLE project_photos
    ADD CONSTRAINT FK_PROJECT_PHOTOS_ON_PROJECT
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE project_photos
    ADD CONSTRAINT FK_PROJECT_PHOTOS_ON_PHOTO
    FOREIGN KEY (photo_id) REFERENCES photos(id) ON DELETE CASCADE;

-- 11. Создаем все необходимые индексы
CREATE INDEX IF NOT EXISTS idx_construction_requests_project ON construction_requests(project_id);
CREATE INDEX IF NOT EXISTS idx_construction_stages_request ON construction_stages(request_id);
CREATE INDEX IF NOT EXISTS idx_construction_stages_customer ON construction_stages(customer_id);
CREATE INDEX IF NOT EXISTS idx_construction_stages_specialist ON construction_stages(specialist_id);
CREATE INDEX IF NOT EXISTS idx_project_photos_project ON project_photos(project_id);
CREATE INDEX IF NOT EXISTS idx_documents_construction ON documents(construction_id);
CREATE INDEX IF NOT EXISTS idx_documents_status ON documents(status);
CREATE INDEX IF NOT EXISTS idx_video_streams_construction ON video_streams(construction_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_construction ON chat_messages(construction_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sender ON chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_stage_reports_stage ON stage_reports(stage_id);
CREATE INDEX IF NOT EXISTS idx_stage_reports_status ON stage_reports(status);
CREATE INDEX IF NOT EXISTS idx_report_photos_report ON report_photos(report_id);