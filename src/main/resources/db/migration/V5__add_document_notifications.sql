-- Таблица шаблонов документов
CREATE TABLE IF NOT EXISTS document_templates (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    required BOOLEAN DEFAULT TRUE NOT NULL,
    category VARCHAR(100)
);

-- Таблица связей шаблонов и этапов строительства
CREATE TABLE IF NOT EXISTS construction_stage_document_templates (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    construction_stage_id UUID NOT NULL REFERENCES construction_stages(id) ON DELETE CASCADE,
    document_template_id UUID NOT NULL REFERENCES document_templates(id) ON DELETE CASCADE,
    sort_order INTEGER,
    UNIQUE(construction_stage_id, document_template_id)
);

-- Таблица уведомлений о документах
CREATE TABLE IF NOT EXISTS document_notifications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    read_at TIMESTAMP WITHOUT TIME ZONE
);

-- Индексы для новых таблиц
CREATE INDEX IF NOT EXISTS idx_document_templates_category ON document_templates(category);
CREATE INDEX IF NOT EXISTS idx_cs_doc_templates_stage ON construction_stage_document_templates(construction_stage_id);
CREATE INDEX IF NOT EXISTS idx_cs_doc_templates_template ON construction_stage_document_templates(document_template_id);
CREATE INDEX IF NOT EXISTS idx_doc_notifications_user_unread ON document_notifications(user_id, is_read, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_doc_notifications_document ON document_notifications(document_id);