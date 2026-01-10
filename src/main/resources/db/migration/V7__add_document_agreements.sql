-- Таблица согласования документов
CREATE TABLE IF NOT EXISTS document_agreements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    construction_request_id UUID NOT NULL REFERENCES construction_requests(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_REVIEW'
        CHECK (status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'CLIENT_REVIEW', 'APPROVED', 'REJECTED', 'SIGNED')),
    required_signatures BOOLEAN DEFAULT TRUE,
    deadline TIMESTAMP WITHOUT TIME ZONE,
    signed_at TIMESTAMP WITHOUT TIME ZONE,
    signed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    signature_data TEXT
);

-- Таблица документов для согласования
CREATE TABLE IF NOT EXISTS agreement_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    document_agreement_id UUID NOT NULL REFERENCES document_agreements(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(100),
    sort_order INTEGER,
    description TEXT
);

-- Индексы для производительности
CREATE INDEX IF NOT EXISTS idx_document_agreements_request ON document_agreements(construction_request_id);
CREATE INDEX IF NOT EXISTS idx_document_agreements_status ON document_agreements(status);
CREATE INDEX IF NOT EXISTS idx_document_agreements_deadline ON document_agreements(deadline);
CREATE INDEX IF NOT EXISTS idx_agreement_documents_agreement ON agreement_documents(document_agreement_id);
CREATE INDEX IF NOT EXISTS idx_agreement_documents_sort_order ON agreement_documents(sort_order);

-- Комментарии к таблицам
COMMENT ON TABLE document_agreements IS 'Таблица для хранения документов, требующих согласования';
COMMENT ON TABLE agreement_documents IS 'Таблица для хранения файлов документов согласования';

-- Триггер для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE OR REPLACE TRIGGER update_document_agreements_updated_at
    BEFORE UPDATE ON document_agreements
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE TRIGGER update_agreement_documents_updated_at
    BEFORE UPDATE ON agreement_documents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();