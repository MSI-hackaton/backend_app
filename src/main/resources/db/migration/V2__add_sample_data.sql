-- Тестовые проекты
INSERT INTO projects (title, description, area, floor_count, bedroom_count, bathroom_count, price, construction_time_months, is_active) VALUES
('Дом «Комфорт»', 'Современный одноэтажный дом с просторной гостиной и двумя спальнями', 85.50, 1, 2, 1, 2500000.00, 4, true),
('Дом «Семейный»', 'Двухэтажный дом для большой семьи с тремя спальнями и двумя ванными', 120.00, 2, 3, 2, 3800000.00, 6, true),
('Коттедж «Премиум»', 'Просторный коттедж с камином, гаражом и террасой', 150.75, 2, 4, 3, 5200000.00, 8, true),
('Дом «Эконом»', 'Компактный и экономичный дом для молодой семьи', 65.00, 1, 1, 1, 1800000.00, 3, true),
('Дом «Классик»', 'Классический двухэтажный дом с мансардой', 95.00, 2, 2, 2, 3200000.00, 5, true);

-- Изображения проектов
INSERT INTO project_images (project_id, image_url) VALUES
(1, 'https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=800'),
(1, 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800'),
(2, 'https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800'),
(3, 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800'),
(4, 'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800'),
(5, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800');

-- Тестовые пользователи
INSERT INTO users (full_name, email, phone, password_hash, role, is_active) VALUES
('Иван Иванов', 'ivan@example.com', '+79161234567', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CLIENT', true),
('Менеджер Петр', 'manager@stroycontrol.ru', '+79169876543', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MANAGER', true),
('Анна Сидорова', 'anna@example.com', '+79167778899', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CLIENT', true);

-- Тестовый заказ
INSERT INTO orders (user_id, project_id, comment, status, total_price, address) VALUES
(1, 1, 'Хотелось бы начать строительство как можно скорее', 'DOCUMENTS_PENDING', 2500000.00, 'Московская обл., г. Красногорск');

-- Документы для заказа
INSERT INTO documents (order_id, title, description, file_url, type, status) VALUES
(1, 'Договор подряда №1', 'Договор на строительство объекта', '/documents/contract_1.pdf', 'CONTRACT', 'PENDING'),
(1, 'Смета к договору №1', 'Детальная смета расходов', '/documents/estimate_1.pdf', 'ESTIMATE', 'PENDING'),
(1, 'График работ', 'График выполнения строительных работ', '/documents/schedule_1.pdf', 'WORK_SCHEDULE', 'PENDING');

-- Этапы строительства
INSERT INTO construction_stages (order_id, title, description, sequence, duration_days, status, completion_percentage) VALUES
(1, 'Подготовка участка', 'Расчистка и разметка участка', 1, 5, 'IN_PROGRESS', 80),
(1, 'Фундамент', 'Закладка фундамента', 2, 20, 'PENDING', 0),
(1, 'Стены', 'Возведение стен', 3, 30, 'PENDING', 0),
(1, 'Кровля', 'Устройство кровли', 4, 15, 'PENDING', 0),
(1, 'Окна и двери', 'Установка окон и дверей', 5, 10, 'PENDING', 0),
(1, 'Внутренняя отделка', 'Внутренние отделочные работы', 6, 15, 'PENDING', 0),
(1, 'Наружная отделка', 'Фасадные работы', 7, 5, 'PENDING', 0),
(1, 'Благоустройство', 'Обустройство территории', 8, 10, 'PENDING', 0);

-- Тестовые сообщения в чате
INSERT INTO chat_messages (order_id, sender_id, message, is_read) VALUES
(1, 1, 'Здравствуйте! Когда можно будет начать строительство?', true),
(1, 2, 'Добрый день! Строительство начнется после подписания договора и сметы.', true),
(1, 1, 'Документы уже готовы к подписанию?', true),
(1, 2, 'Да, все документы готовы. Вы можете их подписать в личном кабинете.', false);

-- Тестовые уведомления
INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
(1, 'Документы готовы к подписанию', 'Пожалуйста, подпишите договор и смету', 'DOCUMENT', false),
(1, 'Этап строительства обновлен', 'Начался этап подготовки участка', 'STAGE', true);

-- Тестовые видеопотоки
INSERT INTO video_streams (order_id, camera_name, stream_url, is_active) VALUES
(1, 'Основная камера', 'rtsp://stream.example.com/order/1/main', true);

-- Тестовые коды верификации
INSERT INTO verification_codes (user_id, code, type, expires_at) VALUES
(1, '123456', 'SIGN', NOW() + INTERVAL '10 minutes');