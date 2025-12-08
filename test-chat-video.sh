#!/bin/bash

echo "=== ТЕСТИРОВАНИЕ ЧАТА И ВИДЕО ==="
echo ""

# Установите jq если нет: brew install jq

# 1. Создаем проект
echo "1. Создаем проект..."
PROJECT_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/projects" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Тестовый дом",
    "description": "Для тестирования API"
  }')

echo "   Ответ: $PROJECT_RESPONSE"

# Извлекаем ID более надежным способом
PROJECT_ID=$(echo $PROJECT_RESPONSE | grep -o '[a-f0-9]\{8\}-[a-f0-9]\{4\}-[a-f0-9]\{4\}-[a-f0-9]\{4\}-[a-f0-9]\{12\}' | head -1)
echo "   Project ID: $PROJECT_ID"

if [ -z "$PROJECT_ID" ]; then
    echo "   ❌ Ошибка: не удалось получить Project ID"
    exit 1
fi

echo ""

# 2. Создаем заявку
echo "2. Создаем заявку..."
REQUEST_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/requests/projects/$PROJECT_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Тестовый Клиент",
    "email": "test@example.com",
    "phone": "+79991234567"
  }')

echo "   Ответ: $REQUEST_RESPONSE"

REQUEST_ID=$(echo $REQUEST_RESPONSE | grep -o '[a-f0-9]\{8\}-[a-f0-9]\{4\}-[a-f0-9]\{4\}-[a-f0-9]\{4\}-[a-f0-9]\{12\}' | head -1)
echo "   Request ID: $REQUEST_ID"

if [ -z "$REQUEST_ID" ]; then
    echo "   ❌ Ошибка: не удалось получить Request ID"
    exit 1
fi

echo ""

# 3. Тестируем чат
echo "3. Тестируем чат..."
echo "   Отправляем сообщение..."
curl -X POST "http://localhost:8080/api/chat/$REQUEST_ID/messages" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Привет! Это тестовое сообщение из curl"
  }'
echo ""

echo "   Получаем историю сообщений..."
curl -s "http://localhost:8080/api/chat/$REQUEST_ID/messages"
echo ""

# 4. Тестируем видео
echo "4. Тестируем видео..."
echo "   Получаем видеопоток..."
curl -s "http://localhost:8080/api/video/$REQUEST_ID/stream"
echo ""

echo "   Получаем скриншот..."
curl -s "http://localhost:8080/api/video/$REQUEST_ID/snapshot"
echo ""

echo "=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ==="