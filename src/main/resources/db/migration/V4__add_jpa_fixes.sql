
-- Для ChatMessage репозитория
CREATE INDEX IF NOT EXISTS idx_chat_messages_order_id ON chat_messages(order_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_is_read ON chat_messages(is_read);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sent_at ON chat_messages(sent_at DESC);

-- Для Document репозитория
CREATE INDEX IF NOT EXISTS idx_documents_order_id ON documents(order_id);
CREATE INDEX IF NOT EXISTS idx_documents_status ON documents(status);
CREATE INDEX IF NOT EXISTS idx_documents_type ON documents(type);

-- Для ConstructionStage репозитория
CREATE INDEX IF NOT EXISTS idx_construction_stages_order_id ON construction_stages(order_id);
CREATE INDEX IF NOT EXISTS idx_construction_stages_sequence ON construction_stages(sequence);
CREATE INDEX IF NOT EXISTS idx_construction_stages_status ON construction_stages(status);

-- Для Notification репозитория
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at DESC);

-- Для Order репозитория
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at DESC);


DO $$
BEGIN
    -- Проверяем и добавляем недостающие внешние ключи
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_chat_messages_order'
                   AND table_name = 'chat_messages') THEN
        ALTER TABLE chat_messages
        ADD CONSTRAINT fk_chat_messages_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_chat_messages_sender'
                   AND table_name = 'chat_messages') THEN
        ALTER TABLE chat_messages
        ADD CONSTRAINT fk_chat_messages_sender
        FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_documents_order'
                   AND table_name = 'documents') THEN
        ALTER TABLE documents
        ADD CONSTRAINT fk_documents_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_construction_stages_order'
                   AND table_name = 'construction_stages') THEN
        ALTER TABLE construction_stages
        ADD CONSTRAINT fk_construction_stages_order
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_notifications_user'
                   AND table_name = 'notifications') THEN
        ALTER TABLE notifications
        ADD CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_orders_user'
                   AND table_name = 'orders') THEN
        ALTER TABLE orders
        ADD CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_orders_project'
                   AND table_name = 'orders') THEN
        ALTER TABLE orders
        ADD CONSTRAINT fk_orders_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;
    END IF;
END $$;


-- Устанавливаем значения по умолчанию для nullable полей
UPDATE chat_messages SET is_read = false WHERE is_read IS NULL;
UPDATE notifications SET is_read = false WHERE is_read IS NULL;
UPDATE construction_stages SET completion_percentage = 0 WHERE completion_percentage IS NULL;

-- 4. Добавляем комментарии к таблицам для документации
COMMENT ON TABLE chat_messages IS 'Сообщения в чате между клиентом и менеджером';
COMMENT ON TABLE documents IS 'Документы проекта (договоры, сметы, акты)';
COMMENT ON TABLE construction_stages IS 'Этапы строительства проекта';
COMMENT ON TABLE notifications IS 'Уведомления пользователей';
COMMENT ON TABLE orders IS 'Заказы на строительство';
COMMENT ON TABLE projects IS 'Типовые проекты домов';
COMMENT ON TABLE users IS 'Пользователи системы';