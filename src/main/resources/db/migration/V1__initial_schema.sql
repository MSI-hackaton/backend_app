-- Таблица проектов
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    area DECIMAL(10,2),
    floor_count INTEGER,
    bedroom_count INTEGER,
    bathroom_count INTEGER,
    price DECIMAL(15,2) NOT NULL,
    construction_time_months INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для изображений проектов
CREATE TABLE IF NOT EXISTS project_images (
    project_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (project_id, image_url),
    CONSTRAINT fk_project_images_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(50) DEFAULT 'CLIENT',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    comment TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_price DECIMAL(15,2),
    address TEXT,
    start_date TIMESTAMP,
    estimated_end_date TIMESTAMP,
    actual_end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Таблица документов
CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_url VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    signed_at TIMESTAMP,
    signature_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Таблица этапов строительства
CREATE TABLE IF NOT EXISTS construction_stages (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    sequence INTEGER NOT NULL,
    duration_days INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    completion_percentage INTEGER DEFAULT 0,
    video_stream_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_construction_stages_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Таблица для изображений этапов
CREATE TABLE IF NOT EXISTS stage_images (
    stage_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (stage_id, image_url),
    CONSTRAINT fk_stage_images_stage FOREIGN KEY (stage_id) REFERENCES construction_stages(id) ON DELETE CASCADE
);

-- Таблица сообщений чата
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    attachment_url VARCHAR(500),
    is_read BOOLEAN DEFAULT false,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_messages_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица для уведомлений
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT false,
    related_entity_id BIGINT,
    related_entity_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица для хранения кодов верификации
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_codes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица для видеостримов
CREATE TABLE IF NOT EXISTS video_streams (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    camera_name VARCHAR(255),
    stream_url VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    last_active TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_video_streams_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Индексы для производительности
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_documents_order_id ON documents(order_id);
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_construction_stages_order_id ON construction_stages(order_id);
CREATE INDEX idx_chat_messages_order_id ON chat_messages(order_id);
CREATE INDEX idx_chat_messages_sent_at ON chat_messages(sent_at);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
CREATE INDEX idx_verification_codes_user_type ON verification_codes(user_id, type, expires_at);
CREATE INDEX idx_video_streams_order ON video_streams(order_id, is_active);