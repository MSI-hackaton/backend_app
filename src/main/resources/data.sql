-- Тестовый пользователь для чата
INSERT INTO users (id, created_at, updated_at, email, phone, password_hash, salt, full_name, role)
SELECT '550e8400-e29b-41d4-a716-446655440001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'customer@test.com', '+79991112233', 'hash1', 'salt1', 'Иван Петров', 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = '550e8400-e29b-41d4-a716-446655440001');

INSERT INTO users (id, created_at, updated_at, email, phone, password_hash, salt, full_name, role)
SELECT '550e8400-e29b-41d4-a716-446655440002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'specialist@test.com', '+79992223344', 'hash2', 'salt2', 'Алексей Смирнов', 'SPECIALIST'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = '550e8400-e29b-41d4-a716-446655440002');

-- Тестовый проект
INSERT INTO projects (id, created_at, updated_at, title, description, area, floors, price, construction_time, status)
SELECT '660e8400-e29b-41d4-a716-446655440001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Дом "Стандарт"', 'Комфортный одноэтажный дом для семьи', 120.5, 1, 3500000, 90, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE id = '660e8400-e29b-41d4-a716-446655440001');