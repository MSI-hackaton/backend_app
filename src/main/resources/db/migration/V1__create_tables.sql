-- Таблица пользователей
CREATE TABLE users
(
    id            UUID                                                                               NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE                                                        NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE                                                        NOT NULL,
    email         VARCHAR(255)                                                                       NOT NULL,
    phone         VARCHAR(255)                                                                       NOT NULL,
    password_hash VARCHAR(255)                                                                       NOT NULL,
    salt          VARCHAR(255)                                                                       NOT NULL,
    full_name     VARCHAR(255)                                                                       NOT NULL,
    role          VARCHAR(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'SPECIALIST', 'ADMIN')) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uc_users_email UNIQUE (email),
    CONSTRAINT uc_users_phone UNIQUE (phone)
);

-- Таблица проектов
CREATE TABLE projects
(
    id                UUID                                                                                               NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE                                                                        NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE                                                                        NOT NULL,
    title             VARCHAR(255)                                                                                       NOT NULL,
    description       TEXT,
    area              DOUBLE PRECISION,
    floors            INTEGER,
    price             DOUBLE PRECISION,
    construction_time INTEGER,
    status            VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'UNDER_CONSTRUCTION', 'COMPLETED')) NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

-- Таблица фотографий
CREATE TABLE photos
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    url        VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_photos PRIMARY KEY (id)
);

-- Таблица фотографий проектов
CREATE TABLE project_photos
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    project_id  UUID                        NOT NULL,
    photo_id    UUID                        NOT NULL,
    sort_order  INTEGER,
    description TEXT,
    CONSTRAINT pk_project_photos PRIMARY KEY (id),
    CONSTRAINT fk_project_photos_on_photo FOREIGN KEY (photo_id) REFERENCES photos (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_photos_on_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

-- Таблица заявок на строительство
CREATE TABLE construction_requests
(
    id                  UUID                                                                                NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE                                                         NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE                                                         NOT NULL,
    project_id          UUID                                                                                NOT NULL,
    status              VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')) NOT NULL,
    anonymous_full_name VARCHAR(255),
    anonymous_email     VARCHAR(255),
    anonymous_phone     VARCHAR(255),
    CONSTRAINT pk_construction_requests PRIMARY KEY (id),
    CONSTRAINT fk_construction_requests_on_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

-- Таблица документов
CREATE TABLE documents
(
    id          UUID                                                                                                  NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                                                           NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                                                           NOT NULL,
    request_id  UUID                                                                                                  NOT NULL,
    name        VARCHAR(255)                                                                                          NOT NULL,
    file_url    VARCHAR(255)                                                                                          NOT NULL,
    status      VARCHAR(20) DEFAULT 'UPLOADED' CHECK (status IN ('UPLOADED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')) NOT NULL,
    reviewed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_documents PRIMARY KEY (id),
    CONSTRAINT fk_documents_on_request FOREIGN KEY (request_id) REFERENCES construction_requests (id) ON DELETE CASCADE
);

-- Таблица этапов строительства
CREATE TABLE construction_stages
(
    id          UUID                                                                                    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                                             NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                                             NOT NULL,
    request_id  UUID                                                                                    NOT NULL,
    name        VARCHAR(255)                                                                            NOT NULL,
    description TEXT,
    start_date  TIMESTAMP WITHOUT TIME ZONE,
    end_date    TIMESTAMP WITHOUT TIME ZONE,
    status      VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED')) NOT NULL,
    CONSTRAINT pk_construction_stages PRIMARY KEY (id),
    CONSTRAINT fk_construction_stages_on_request FOREIGN KEY (request_id) REFERENCES construction_requests (id) ON DELETE CASCADE
);

-- Таблица отчетов по этапам
CREATE TABLE stage_reports
(
    id          UUID                                                                 NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    stage_id    UUID                                                                 NOT NULL,
    description TEXT                                                                 NOT NULL,
    status      VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')) NOT NULL,
    CONSTRAINT pk_stage_reports PRIMARY KEY (id),
    CONSTRAINT fk_stage_reports_on_stage FOREIGN KEY (stage_id) REFERENCES construction_stages (id) ON DELETE CASCADE
);

-- Таблица фотографий отчетов
CREATE TABLE report_photos
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    report_id   UUID                        NOT NULL,
    url         VARCHAR(255)                NOT NULL,
    description TEXT,
    CONSTRAINT pk_report_photos PRIMARY KEY (id),
    CONSTRAINT fk_report_photos_on_report FOREIGN KEY (report_id) REFERENCES stage_reports (id) ON DELETE CASCADE
);

-- Таблица сообщений в чате
CREATE TABLE chat_messages
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id UUID                        NOT NULL,
    sender_id  UUID                        NOT NULL,
    message    TEXT                        NOT NULL,
    is_read    BOOLEAN DEFAULT FALSE       NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id),
    CONSTRAINT fk_chat_messages_on_request FOREIGN KEY (request_id) REFERENCES construction_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_messages_on_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Таблица видеопотоков
CREATE TABLE video_streams
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id  UUID                        NOT NULL,
    stream_url  VARCHAR(255)                NOT NULL,
    camera_name VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_video_streams PRIMARY KEY (id),
    CONSTRAINT fk_video_streams_on_request FOREIGN KEY (request_id) REFERENCES construction_requests (id) ON DELETE CASCADE
);

-- Индексы для производительности
CREATE INDEX idx_project_photos_project_id ON project_photos(project_id);
CREATE INDEX idx_construction_requests_project_id ON construction_requests(project_id);
CREATE INDEX idx_documents_request_id ON documents(request_id);
CREATE INDEX idx_construction_stages_request_id ON construction_stages(request_id);
CREATE INDEX idx_stage_reports_stage_id ON stage_reports(stage_id);
CREATE INDEX idx_report_photos_report_id ON report_photos(report_id);
CREATE INDEX idx_chat_messages_request_id ON chat_messages(request_id);
CREATE INDEX idx_chat_messages_sender_id ON chat_messages(sender_id);
CREATE INDEX idx_chat_messages_is_read ON chat_messages(is_read);
CREATE INDEX idx_video_streams_request_id ON video_streams(request_id);

-- Комментарии к таблицам
COMMENT ON TABLE users IS 'Пользователи системы (клиенты, специалисты, администраторы)';
COMMENT ON TABLE projects IS 'Строительные проекты (каталог домов)';
COMMENT ON TABLE photos IS 'Фотографии (общая таблица для хранения ссылок на изображения)';
COMMENT ON TABLE project_photos IS 'Связь проектов с фотографиями';
COMMENT ON TABLE construction_requests IS 'Заявки на строительство проектов';
COMMENT ON TABLE documents IS 'Документы по заявкам (договоры, сметы, акты)';
COMMENT ON TABLE construction_stages IS 'Этапы строительства по заявкам';
COMMENT ON TABLE stage_reports IS 'Отчеты по выполненным этапам строительства';
COMMENT ON TABLE report_photos IS 'Фотографии к отчетам по этапам';
COMMENT ON TABLE chat_messages IS 'Сообщения в чате между клиентом и специалистом';
COMMENT ON TABLE video_streams IS 'Видеопотоки с камер на стройке';