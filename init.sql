-- Создаем дополнительного пользователя и настраиваем права
CREATE USER msi_user WITH PASSWORD 'msi_password';
GRANT ALL PRIVILEGES ON DATABASE msi_db TO msi_user;
GRANT CREATE ON SCHEMA public TO msi_user;

-- Создаем расширение для UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";