#!/bin/bash

echo "=== Запуск миграций Flyway ==="

# Проверяем что база данных запущена
if ! docker ps | grep -q "stroycontrol-db"; then
    echo "Запускаем базу данных..."
    docker compose up -d postgres
    sleep 5
fi

echo "Выполняем миграцию V4..."
./mvnw flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5433/stroycontrol \
  -Dflyway.user=stroycontrol_user \
  -Dflyway.password=stroycontrol_pass \
  -Dflyway.locations=filesystem:src/main/resources/db/migration

echo "=== Миграции завершены ==="
echo "Теперь запустите приложение: ./mvnw spring-boot:run"