-- Проверяем существование таблицы chat_messages
DO $$
BEGIN
    -- Проверяем существование колонки message
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'chat_messages'
        AND column_name = 'message'
        AND table_schema = 'public'
    ) THEN
        -- Если колонки message нет, добавляем её
        ALTER TABLE chat_messages ADD COLUMN message TEXT;
        RAISE NOTICE 'Column "message" added to chat_messages table';
    END IF;

    -- Проверяем существование колонки text (если есть - удаляем)
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'chat_messages'
        AND column_name = 'text'
        AND table_schema = 'public'
    ) THEN
        -- Если колонка text есть, удаляем её
        ALTER TABLE chat_messages DROP COLUMN text;
        RAISE NOTICE 'Column "text" removed from chat_messages table';
    END IF;
END $$;