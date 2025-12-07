-- Добавляем поле email_verified (если нужно)
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false;

-- Обновляем пароли для тестовых пользователей (bcrypt хэш для "password")
UPDATE users SET password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE password_hash IS NULL;

-- Создаем дополнительные индексы для оптимизации
CREATE INDEX IF NOT EXISTS idx_projects_active ON projects(is_active);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_chat_messages_order_sender ON chat_messages(order_id, sender_id);

-- Добавляем триггер для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_projects_updated_at BEFORE UPDATE ON projects
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_documents_updated_at BEFORE UPDATE ON documents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

    -- Обновляем существующие записи с новыми полями
    UPDATE chat_messages SET is_read = false WHERE is_read IS NULL;
    UPDATE notifications SET is_read = false WHERE is_read IS NULL;

    -- Убедимся что нет циклических зависимостей
    ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_users_self_ref;
    ALTER TABLE orders DROP CONSTRAINT IF EXISTS fk_orders_self_ref;
    ALTER TABLE chat_messages DROP CONSTRAINT IF EXISTS fk_chat_messages_self_ref;

    -- Создаем индексы для улучшения производительности
    CREATE INDEX IF NOT EXISTS idx_chat_messages_order_sent ON chat_messages(order_id, sent_at DESC);
    CREATE INDEX IF NOT EXISTS idx_notifications_user_created ON notifications(user_id, created_at DESC);