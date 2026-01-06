ALTER TABLE construction_stages ADD COLUMN project_id UUID;
ALTER TABLE construction_stages ADD COLUMN customer_id UUID;
ALTER TABLE construction_stages ADD COLUMN specialist_id UUID;

ALTER TABLE chat_messages
    RENAME COLUMN request_id TO construction_id;

ALTER TABLE documents
    RENAME COLUMN request_id TO construction_id;

ALTER TABLE video_streams
    RENAME COLUMN request_id TO construction_id;

ALTER TABLE chat_messages
    DROP CONSTRAINT FK_CHAT_MESSAGES_ON_REQUEST;

ALTER TABLE documents
    DROP CONSTRAINT FK_DOCUMENTS_ON_REQUEST;

ALTER TABLE video_streams
    DROP CONSTRAINT FK_VIDEO_STREAMS_ON_REQUEST;

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_CONSTRUCTION FOREIGN KEY (construction_id) REFERENCES construction_stages (id);

ALTER TABLE documents
    ADD CONSTRAINT FK_DOCUMENTS_ON_CONSTRUCTION FOREIGN KEY (construction_id) REFERENCES construction_stages (id);

ALTER TABLE video_streams
    ADD CONSTRAINT FK_VIDEO_STREAMS_ON_CONSTRUCTION FOREIGN KEY (construction_id) REFERENCES construction_stages (id);

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES users (id);

ALTER TABLE construction_stages
    ADD CONSTRAINT FK_CONSTRUCTION_STAGES_ON_SPECIALIST FOREIGN KEY (specialist_id) REFERENCES users (id);

ALTER TABLE construction_stages ALTER COLUMN project_id SET NOT NULL;
ALTER TABLE construction_stages ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE users DROP COLUMN salt;

CREATE INDEX idx_chat_messages_construction ON chat_messages (construction_id);
CREATE INDEX idx_chat_messages_sender ON chat_messages (sender_id);
CREATE INDEX idx_construction_stages_project ON construction_stages (project_id);
CREATE INDEX idx_construction_stages_customer ON construction_stages (customer_id);
CREATE INDEX idx_construction_stages_specialist ON construction_stages (specialist_id);
CREATE INDEX idx_documents_construction ON documents (construction_id);
CREATE INDEX idx_video_streams_construction ON video_streams (construction_id);