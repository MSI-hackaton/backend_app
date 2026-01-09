-- Таблица пользователей
CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    role          VARCHAR(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'SPECIALIST', 'ADMIN'))
);

-- Таблица проектов
CREATE TABLE projects
(
    id                UUID PRIMARY KEY,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    area              DOUBLE PRECISION,
    floors            INTEGER,
    price             DOUBLE PRECISION,
    construction_time INTEGER,
    status            VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'UNDER_CONSTRUCTION', 'COMPLETED'))
);

-- Таблица заявок на строительство
CREATE TABLE construction_requests
(
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    project_id          UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    status              VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    anonymous_full_name VARCHAR(255),
    anonymous_email     VARCHAR(255),
    anonymous_phone     VARCHAR(255)
);

-- Таблица этапов строительства
CREATE TABLE construction_stages
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id   UUID NOT NULL REFERENCES construction_requests(id) ON DELETE CASCADE,
    project_id   UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    customer_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    specialist_id UUID REFERENCES users(id) ON DELETE SET NULL,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED'))
);

-- Таблица фотографий
CREATE TABLE photos
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    url        VARCHAR(255) NOT NULL
);

-- Таблица связей проектов с фотографиями
CREATE TABLE project_photos
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    project_id  UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    photo_id    UUID NOT NULL REFERENCES photos(id) ON DELETE CASCADE,
    sort_order  INTEGER,
    description TEXT
);

-- Таблица документов
CREATE TABLE documents
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    construction_id  UUID NOT NULL REFERENCES construction_stages(id) ON DELETE CASCADE,
    reviewed_by      UUID REFERENCES users(id) ON DELETE SET NULL,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    file_url         VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255),
    file_size        BIGINT,
    mime_type        VARCHAR(100),
    status           VARCHAR(20) DEFAULT 'UPLOADED' CHECK (status IN ('UPLOADED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')),
    reviewed_at      TIMESTAMP WITHOUT TIME ZONE
);

-- Таблица видеопотоков
CREATE TABLE video_streams
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    construction_id UUID NOT NULL REFERENCES construction_stages(id) ON DELETE CASCADE,
    stream_url      VARCHAR(255) NOT NULL,
    camera_name     VARCHAR(255) NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE NOT NULL,
    camera_location VARCHAR(255),
    thumbnail_url   VARCHAR(255)
);

-- Таблица сообщений чата
CREATE TABLE chat_messages
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    construction_id UUID NOT NULL REFERENCES construction_stages(id) ON DELETE CASCADE,
    sender_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message         TEXT NOT NULL,
    is_read         BOOLEAN DEFAULT FALSE NOT NULL
);

-- Таблица отчетов об этапах строительства
CREATE TABLE stage_reports
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    stage_id    UUID NOT NULL REFERENCES construction_stages(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    status      VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED'))
);

-- Таблица фотографий отчетов
CREATE TABLE report_photos
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    report_id   UUID NOT NULL REFERENCES stage_reports(id) ON DELETE CASCADE,
    url         VARCHAR(255) NOT NULL,
    description TEXT
);

-- Индексы для производительности
CREATE INDEX idx_construction_requests_project ON construction_requests(project_id);
CREATE INDEX idx_construction_stages_request ON construction_stages(request_id);
CREATE INDEX idx_construction_stages_customer ON construction_stages(customer_id);
CREATE INDEX idx_construction_stages_specialist ON construction_stages(specialist_id);
CREATE INDEX idx_project_photos_project ON project_photos(project_id);
CREATE INDEX idx_documents_construction ON documents(construction_id);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_video_streams_construction ON video_streams(construction_id);
CREATE INDEX idx_chat_messages_construction ON chat_messages(construction_id);
CREATE INDEX idx_chat_messages_sender ON chat_messages(sender_id);
CREATE INDEX idx_stage_reports_stage ON stage_reports(stage_id);
CREATE INDEX idx_stage_reports_status ON stage_reports(status);
CREATE INDEX idx_report_photos_report ON report_photos(report_id);