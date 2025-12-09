CREATE TABLE IF NOT EXISTS users
(
    id            UUID                                                                               NOT NULL PRIMARY KEY,
    created_at    TIMESTAMP WITHOUT TIME ZONE                                                        NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE                                                        NOT NULL,
    email         VARCHAR(255)                                                                       NOT NULL UNIQUE,
    phone         VARCHAR(255)                                                                       NOT NULL UNIQUE,
    password_hash VARCHAR(255)                                                                       NOT NULL,
    salt          VARCHAR(255)                                                                       NOT NULL,
    full_name     VARCHAR(255)                                                                       NOT NULL,
    role          VARCHAR(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'SPECIALIST', 'ADMIN')) NOT NULL
);

CREATE TABLE IF NOT EXISTS projects
(
    id                UUID                                                                                               NOT NULL PRIMARY KEY,
    created_at        TIMESTAMP WITHOUT TIME ZONE                                                                        NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE                                                                        NOT NULL,
    title             VARCHAR(255)                                                                                       NOT NULL,
    description       TEXT,
    area              DOUBLE PRECISION,
    floors            INTEGER,
    price             DOUBLE PRECISION,
    construction_time INTEGER,
    status            VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'UNDER_CONSTRUCTION', 'COMPLETED')) NOT NULL
);

CREATE TABLE IF NOT EXISTS photos
(
    id         UUID                        NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    url        VARCHAR(255)                NOT NULL
);

CREATE TABLE IF NOT EXISTS project_photos
(
    id          UUID                        NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    project_id  UUID                        NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    photo_id    UUID                        NOT NULL REFERENCES photos (id) ON DELETE CASCADE,
    sort_order  INTEGER,
    description TEXT
);

CREATE TABLE IF NOT EXISTS construction_requests
(
    id                  UUID                                                                                NOT NULL PRIMARY KEY,
    created_at          TIMESTAMP WITHOUT TIME ZONE                                                         NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE                                                         NOT NULL,
    project_id          UUID                                                                                NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    status              VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')) NOT NULL,
    anonymous_full_name VARCHAR(255),
    anonymous_email     VARCHAR(255),
    anonymous_phone     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS documents
(
    id          UUID                                                                                                  NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                                                           NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                                                           NOT NULL,
    request_id  UUID                                                                                                  NOT NULL REFERENCES construction_requests (id) ON DELETE CASCADE,
    name        VARCHAR(255)                                                                                          NOT NULL,
    file_url    VARCHAR(255)                                                                                          NOT NULL,
    status      VARCHAR(20) DEFAULT 'UPLOADED' CHECK (status IN ('UPLOADED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')) NOT NULL,
    reviewed_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS construction_stages
(
    id          UUID                                                                                    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                                             NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                                             NOT NULL,
    request_id  UUID                                                                                    NOT NULL REFERENCES construction_requests (id) ON DELETE CASCADE,
    name        VARCHAR(255)                                                                            NOT NULL,
    description TEXT,
    start_date  TIMESTAMP WITHOUT TIME ZONE,
    end_date    TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED')) NOT NULL
);

CREATE TABLE IF NOT EXISTS stage_reports
(
    id          UUID                                                                 NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    stage_id    UUID                                                                 NOT NULL REFERENCES construction_stages (id) ON DELETE CASCADE,
    description TEXT                                                                 NOT NULL,
    status      VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')) NOT NULL
);

CREATE TABLE IF NOT EXISTS report_photos
(
    id          UUID                        NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    report_id   UUID                        NOT NULL REFERENCES stage_reports (id) ON DELETE CASCADE,
    url         VARCHAR(255)                NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS chat_messages
(
    id         UUID                        NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id UUID                        NOT NULL REFERENCES construction_requests (id) ON DELETE CASCADE,
    sender_id  UUID                        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message    TEXT                        NOT NULL,
    is_read    BOOLEAN DEFAULT FALSE       NOT NULL
);

CREATE TABLE IF NOT EXISTS video_streams
(
    id          UUID                        NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id  UUID                        NOT NULL REFERENCES construction_requests (id) ON DELETE CASCADE,
    stream_url  VARCHAR(255)                NOT NULL,
    camera_name VARCHAR(255)                NOT NULL
);

-- Создаем индексы для производительности
CREATE INDEX IF NOT EXISTS idx_project_photos_project_id ON project_photos(project_id);
CREATE INDEX IF NOT EXISTS idx_construction_requests_project_id ON construction_requests(project_id);
CREATE INDEX IF NOT EXISTS idx_documents_request_id ON documents(request_id);
CREATE INDEX IF NOT EXISTS idx_construction_stages_request_id ON construction_stages(request_id);
CREATE INDEX IF NOT EXISTS idx_stage_reports_stage_id ON stage_reports(stage_id);
CREATE INDEX IF NOT EXISTS idx_report_photos_report_id ON report_photos(report_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_request_id ON chat_messages(request_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sender_id ON chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_is_read ON chat_messages(is_read);
CREATE INDEX IF NOT EXISTS idx_video_streams_request_id ON video_streams(request_id);