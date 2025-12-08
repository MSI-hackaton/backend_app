CREATE TABLE chat_messages
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id UUID                        NOT NULL,
    sender_id  UUID                        NOT NULL,
    text       VARCHAR(5000)               NOT NULL,
    is_read    BOOLEAN DEFAULT FALSE       NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id)
);

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
    CONSTRAINT pk_construction_requests PRIMARY KEY (id)
);

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
    CONSTRAINT pk_construction_stages PRIMARY KEY (id)
);

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
    CONSTRAINT pk_documents PRIMARY KEY (id)
);

CREATE TABLE photos
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    url        VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_photos PRIMARY KEY (id)
);

CREATE TABLE project_photos
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    project_id  UUID                        NOT NULL,
    photo_id    UUID                        NOT NULL,
    sort_order  INTEGER,
    description TEXT,
    CONSTRAINT pk_project_photos PRIMARY KEY (id)
);

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

CREATE TABLE report_photos
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    report_id   UUID                        NOT NULL,
    url         VARCHAR(255)                NOT NULL,
    description TEXT,
    CONSTRAINT pk_report_photos PRIMARY KEY (id)
);

CREATE TABLE stage_reports
(
    id          UUID                                                                 NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE                                          NOT NULL,
    stage_id    UUID                                                                 NOT NULL,
    description TEXT                                                                 NOT NULL,
    status      VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')) NOT NULL,
    CONSTRAINT pk_stage_reports PRIMARY KEY (id)
);

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
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE video_streams
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    request_id  UUID                        NOT NULL,
    stream_url  VARCHAR(255)                NOT NULL,
    camera_name VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_video_streams PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone UNIQUE (phone);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_REQUEST FOREIGN KEY (request_id) REFERENCES construction_requests (id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);

ALTER TABLE construction_requests
    ADD CONSTRAINT FK_CONSTRUCTION_REQUESTS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_REQUEST FOREIGN KEY (request_id) REFERENCES construction_requests (id);

ALTER TABLE documents
    ADD CONSTRAINT FK_DOCUMENTS_ON_REQUEST FOREIGN KEY (request_id) REFERENCES construction_requests (id);

ALTER TABLE project_photos
    ADD CONSTRAINT FK_PROJECT_PHOTOS_ON_PHOTO FOREIGN KEY (photo_id) REFERENCES photos (id);

ALTER TABLE project_photos
    ADD CONSTRAINT FK_PROJECT_PHOTOS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE report_photos
    ADD CONSTRAINT FK_REPORT_PHOTOS_ON_REPORT FOREIGN KEY (report_id) REFERENCES stage_reports (id);

ALTER TABLE stage_reports
    ADD CONSTRAINT FK_STAGE_REPORTS_ON_STAGE FOREIGN KEY (stage_id) REFERENCES construction_stages (id);

ALTER TABLE video_streams
    ADD CONSTRAINT FK_VIDEO_STREAMS_ON_REQUEST FOREIGN KEY (request_id) REFERENCES construction_requests (id);