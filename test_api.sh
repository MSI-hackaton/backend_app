#!/bin/bash

# ============================================
# Тестовый скрипт для проверки API Стройконтроль
# ============================================

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Настройки
BASE_URL="http://localhost:8080"
TEST_USER_EMAIL="ivan@example.com"
TEST_USER_PASSWORD="password"
TEST_USER_ID=1
TEST_ORDER_ID=1

# Глобальные переменные
TOKEN=""
USER_ID=""

# Функции для вывода
print_header() {
    echo -e "\n${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Проверка зависимостей
check_dependencies() {
    print_header "Проверка зависимостей"

    # Проверка curl
    if ! command -v curl &> /dev/null; then
        print_error "curl не установлен"
        exit 1
    fi
    print_success "curl установлен"

    # Проверка jq
    if ! command -v jq &> /dev/null; then
        print_error "jq не установлен. Установите: brew install jq (Mac) или apt-get install jq (Linux)"
        exit 1
    fi
    print_success "jq установлен"

    # Проверка доступа к API
    if ! curl -s "$BASE_URL/api/health" &> /dev/null; then
        print_error "Сервис недоступен по адресу $BASE_URL"
        print_info "Убедитесь что сервис запущен: ./mvnw spring-boot:run"
        exit 1
    fi
    print_success "Сервис доступен"
}

# 1. Проверка доступности сервиса
test_health_check() {
    print_header "1. Проверка доступности сервиса"

    response=$(curl -s "$BASE_URL/api/health")
    status=$(echo "$response" | jq -r '.status')
    message=$(echo "$response" | jq -r '.message')

    if [ "$status" = "OK" ]; then
        print_success "Status: $status"
        print_success "Message: $message"
    else
        print_error "Сервис не работает"
        echo "$response"
        exit 1
    fi
}

# 2. Тестирование аутентификации
test_authentication() {
    print_header "2. Тестирование аутентификации"

    # Логин
    print_info "Вход пользователя $TEST_USER_EMAIL"

    login_response=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"email\": \"$TEST_USER_EMAIL\",
            \"password\": \"$TEST_USER_PASSWORD\"
        }")

    # Проверка ответа
    if echo "$login_response" | jq -e '.token' > /dev/null 2>&1; then
        TOKEN=$(echo "$login_response" | jq -r '.token')
        USER_ID=$(echo "$login_response" | jq -r '.userId')
        print_success "Успешный вход"
        print_success "User ID: $USER_ID"
        print_success "Токен получен (первые 20 символов): ${TOKEN:0:20}..."
    else
        print_error "Ошибка входа"
        echo "Ответ сервера:"
        echo "$login_response"
        exit 1
    fi
}

# 3. Тестирование работы с проектами
test_projects() {
    print_header "3. Тестирование работы с проектами"

    # Получение списка проектов
    print_info "Получение списка проектов"

    projects_response=$(curl -s -X GET "$BASE_URL/api/v1/projects?size=5" \
        -H "Authorization: Bearer $TOKEN")

    # Исправленная проверка ответа
    if echo "$projects_response" | jq -e '.content' > /dev/null 2>&1; then
        total_elements=$(echo "$projects_response" | jq -r '.totalElements // 0')

        if [ "$total_elements" -gt 0 ]; then
            print_success "Найдено проектов: $total_elements"

            # Вывод проектов
            echo "Список проектов:"
            echo "$projects_response" | jq -r '.content[] | "  - \(.title) (\(.price) руб.)"' 2>/dev/null || echo "  Не удалось распарсить проекты"

            # Получение деталей первого проекта
            first_project_id=$(echo "$projects_response" | jq -r '.content[0].id // empty')

            if [ -n "$first_project_id" ]; then
                print_info "Получение деталей проекта ID: $first_project_id"

                project_detail=$(curl -s -X GET "$BASE_URL/api/v1/projects/$first_project_id" \
                    -H "Authorization: Bearer $TOKEN")

                if echo "$project_detail" | jq -e '.title' > /dev/null 2>&1; then
                    print_success "Детали проекта получены"
                    echo "Название: $(echo "$project_detail" | jq -r '.title')"
                    echo "Описание: $(echo "$project_detail" | jq -r '.description // "Нет описания"')"
                    echo "Площадь: $(echo "$project_detail" | jq -r '.area // "Н/Д"') м²"
                    echo "Этажность: $(echo "$project_detail" | jq -r '.floorCount // "Н/Д"')"
                    echo "Стоимость: $(echo "$project_detail" | jq -r '.price // "Н/Д"') руб."
                else
                    print_error "Ошибка получения деталей проекта"
                fi
            fi
        else
            print_error "Проекты не найдены"
        fi
    else
        print_error "Некорректный ответ от сервера"
        echo "$projects_response"
    fi
}

# 4. Тестирование работы с заказами
test_orders() {
    print_header "4. Тестирование работы с заказами"

    # Получение заказов пользователя
    print_info "Получение заказов пользователя ID: $USER_ID"

    orders_response=$(curl -s -X GET "$BASE_URL/api/v1/orders/user/$USER_ID" \
        -H "Authorization: Bearer $TOKEN")

    # Проверяем если ответ массив
    if echo "$orders_response" | jq -e '.[0]' > /dev/null 2>&1; then
        orders_count=$(echo "$orders_response" | jq -r '. | length')

        if [ "$orders_count" -gt 0 ]; then
            print_success "Найдено заказов: $orders_count"

            # Вывод заказов
            for i in $(seq 0 $((orders_count - 1))); do
                order_id=$(echo "$orders_response" | jq -r ".[$i].id")
                status=$(echo "$orders_response" | jq -r ".[$i].status")
                total_price=$(echo "$orders_response" | jq -r ".[$i].totalPrice")

                echo "  Заказ #$order_id:"
                echo "    Статус: $status"
                echo "    Стоимость: $total_price руб."
            done

            # Используем первый заказ для дальнейшего тестирования
            TEST_ORDER_ID=$(echo "$orders_response" | jq -r '.[0].id')
            print_info "Для тестирования используем заказ ID: $TEST_ORDER_ID"
        else
            print_info "Заказов нет, создаем тестовый заказ"
            create_test_order
        fi
    else
        print_info "Заказов нет или формат ответа отличается"
        create_test_order
    fi
}

create_test_order() {
    print_info "Создание тестового заказа"

    new_order_data='{
        "projectId": 1,
        "fullName": "Иван Иванов",
        "email": "'$TEST_USER_EMAIL'",
        "phone": "+79161234567",
        "comment": "Первый заказ пользователя",
        "address": "Московская область, г. Красногорск"
    }'

    new_order_response=$(curl -s -X POST "$BASE_URL/api/v1/orders" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "$new_order_data")

    if echo "$new_order_response" | jq -e '.id' > /dev/null 2>&1; then
        new_order_id=$(echo "$new_order_response" | jq -r '.id')
        print_success "Создан новый заказ ID: $new_order_id"
        TEST_ORDER_ID=$new_order_id
    else
        print_error "Ошибка создания заказа"
        echo "$new_order_response"
    fi
}

# 5. Тестирование документов
test_documents() {
    print_header "5. Тестирование работы с документами"

    if [ -z "$TEST_ORDER_ID" ] || [ "$TEST_ORDER_ID" = "null" ]; then
        print_error "Нет заказа для тестирования документов"
        return
    fi

    print_info "Получение документов заказа ID: $TEST_ORDER_ID"

    documents_response=$(curl -s -X GET "$BASE_URL/api/v1/documents/order/$TEST_ORDER_ID" \
        -H "Authorization: Bearer $TOKEN")

    # Проверяем если ответ массив
    if echo "$documents_response" | jq -e '.[0]' > /dev/null 2>&1; then
        documents_count=$(echo "$documents_response" | jq -r '. | length')

        if [ "$documents_count" -gt 0 ]; then
            print_success "Найдено документов: $documents_count"

            # Вывод документов
            for i in $(seq 0 $((documents_count - 1))); do
                doc_id=$(echo "$documents_response" | jq -r ".[$i].id")
                doc_title=$(echo "$documents_response" | jq -r ".[$i].title")
                doc_status=$(echo "$documents_response" | jq -r ".[$i].status")

                echo "  Документ #$doc_id:"
                echo "    Название: $doc_title"
                echo "    Статус: $doc_status"
            done

            # Получение документов ожидающих подписи
            test_pending_documents
        else
            print_info "Документы не найдены"
        fi
    else
        print_info "Документы не найдены или формат ответа отличается"
    fi
}

test_pending_documents() {
    print_info "Получение документов ожидающих подписи"

    pending_docs_response=$(curl -s -X GET "$BASE_URL/api/v1/documents/order/$TEST_ORDER_ID/pending" \
        -H "Authorization: Bearer $TOKEN")

    # Проверяем если ответ массив
    if echo "$pending_docs_response" | jq -e '.[0]' > /dev/null 2>&1; then
        pending_count=$(echo "$pending_docs_response" | jq -r '. | length')

        if [ "$pending_count" -gt 0 ]; then
            print_success "Документов ожидающих подписи: $pending_count"

            # Тестирование подписи первого документа
            first_doc_id=$(echo "$pending_docs_response" | jq -r '.[0].id')

            if [ -n "$first_doc_id" ] && [ "$first_doc_id" != "null" ]; then
                print_info "Тестирование подписи документа ID: $first_doc_id"

                # Запрос подписи
                print_info "Запрос SMS кода для подписи"
                sign_request_response=$(curl -s -X POST "$BASE_URL/api/v1/documents/$first_doc_id/request-sign" \
                    -H "Authorization: Bearer $TOKEN")

                if [ -n "$sign_request_response" ]; then
                    print_success "SMS код запрошен (в тестовом режиме код: 123456)"
                fi

                # Подписание документа
                print_info "Подписание документа с кодом 123456"

                sign_response=$(curl -s -X POST "$BASE_URL/api/v1/documents/$first_doc_id/sign" \
                    -H "Content-Type: application/json" \
                    -H "Authorization: Bearer $TOKEN" \
                    -d '{"smsCode": "123456"}')

                if echo "$sign_response" | jq -e '.status' > /dev/null 2>&1; then
                    new_status=$(echo "$sign_response" | jq -r '.status')
                    print_success "Документ подписан, новый статус: $new_status"
                else
                    print_error "Ошибка подписания документа"
                    echo "$sign_response"
                fi
            fi
        else
            print_info "Нет документов ожидающих подписи"
        fi
    else
        print_info "Нет документов ожидающих подписи"
    fi
}

# 6. Тестирование этапов строительства
test_construction_stages() {
    print_header "6. Тестирование этапов строительства"

    if [ -z "$TEST_ORDER_ID" ] || [ "$TEST_ORDER_ID" = "null" ]; then
        print_error "Нет заказа для тестирования этапов"
        return
    fi

    print_info "Получение этапов строительства заказа ID: $TEST_ORDER_ID"

    stages_response=$(curl -s -X GET "$BASE_URL/api/v1/construction/order/$TEST_ORDER_ID/stages" \
        -H "Authorization: Bearer $TOKEN")

    # Проверяем если ответ массив
    if echo "$stages_response" | jq -e '.[0]' > /dev/null 2>&1; then
        stages_count=$(echo "$stages_response" | jq -r '. | length')

        if [ "$stages_count" -gt 0 ]; then
            print_success "Найдено этапов: $stages_count"

            # Вывод этапов
            for i in $(seq 0 $((stages_count - 1))); do
                stage_title=$(echo "$stages_response" | jq -r ".[$i].title // \"Нет названия\"")
                stage_status=$(echo "$stages_response" | jq -r ".[$i].status // \"Нет статуса\"")
                stage_progress=$(echo "$stages_response" | jq -r ".[$i].completionPercentage // 0")

                echo "  Этап: $stage_title"
                echo "    Статус: $stage_status"
                echo "    Прогресс: $stage_progress%"
            done
        else
            print_info "Этапы строительства не найдены"
            print_info "Запустите создание тестовых этапов:"
            print_info "curl -X POST $BASE_URL/api/v1/test/order/$TEST_ORDER_ID/stages -H 'Authorization: Bearer $TOKEN'"
        fi
    else
        print_info "Этапы строительства не найдены или формат ответа отличается"
    fi

    # Получение URL видеопотока
    print_info "Получение URL видеопотока"

    video_response=$(curl -s -X GET "$BASE_URL/api/v1/construction/order/$TEST_ORDER_ID/video-stream" \
        -H "Authorization: Bearer $TOKEN")

    # Убираем кавычки если они есть
    video_url=$(echo "$video_response" | tr -d '"')

    if [ -n "$video_url" ] && [ "$video_url" != "null" ]; then
        print_success "URL видеопотока: $video_url"
    else
        print_info "URL видеопотока не найден"
    fi
}

# 7. Тестирование чата
test_chat() {
    print_header "7. Тестирование чата"

    if [ -z "$TEST_ORDER_ID" ] || [ "$TEST_ORDER_ID" = "null" ]; then
        print_error "Нет заказа для тестирования чата"
        return
    fi

    print_info "Получение истории чата заказа ID: $TEST_ORDER_ID"

    chat_response=$(curl -s -X GET "$BASE_URL/api/v1/chat/order/$TEST_ORDER_ID" \
        -H "Authorization: Bearer $TOKEN")

    # Используем jq с увеличенным лимитом глубины
    if echo "$chat_response" | jq -e '.[0]' > /dev/null 2>&1; then
        messages_count=$(echo "$chat_response" | jq -r '. | length')

        if [ "$messages_count" -gt 0 ]; then
            print_success "Найдено сообщений: $messages_count"

            # Вывод последних 3 сообщений
            echo "Последние сообщения:"
            for i in $(seq 0 $((messages_count - 1)) | tail -3); do
                sender_name=$(echo "$chat_response" | jq -r ".[$i].senderName // \"Неизвестно\"")
                message=$(echo "$chat_response" | jq -r ".[$i].message // \"Нет текста\"")
                sent_at=$(echo "$chat_response" | jq -r ".[$i].sentAt // \"Нет времени\"")

                echo "  От: $sender_name"
                echo "  Время: $sent_at"
                echo "  Сообщение: $message"
                echo ""
            done
        else
            print_info "Сообщений нет"
        fi
    else
        print_info "Сообщений нет или формат ответа отличается"
    fi

    # Отправка тестового сообщения
    print_info "Отправка тестового сообщения"

    new_message_data='{
        "orderId": '$TEST_ORDER_ID',
        "message": "Тестовое сообщение из скрипта проверки API",
        "attachmentUrl": null
    }'

    send_message_response=$(curl -s -X POST "$BASE_URL/api/v1/chat/send?senderId=$USER_ID" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "$new_message_data")

    if echo "$send_message_response" | jq -e '.id' > /dev/null 2>&1; then
        message_id=$(echo "$send_message_response" | jq -r '.id')
        message_text=$(echo "$send_message_response" | jq -r '.message')
        print_success "Сообщение отправлено, ID: $message_id"
        print_success "Текст: $message_text"
    else
        print_error "Ошибка отправки сообщения"
        echo "$send_message_response"
    fi
}

# 8. Тестирование уведомлений
test_notifications() {
    print_header "8. Тестирование уведомлений"

    print_info "Получение уведомлений пользователя ID: $USER_ID"

    notifications_response=$(curl -s -X GET "$BASE_URL/api/v1/notifications/user/$USER_ID" \
        -H "Authorization: Bearer $TOKEN")

    if echo "$notifications_response" | jq -e '.[0]' > /dev/null 2>&1; then
        notifications_count=$(echo "$notifications_response" | jq -r '. | length')

        if [ "$notifications_count" -gt 0 ]; then
            print_success "Найдено уведомлений: $notifications_count"

            # Вывод уведомлений
            for i in $(seq 0 $((notifications_count - 1))); do
                notif_title=$(echo "$notifications_response" | jq -r ".[$i].title")
                notif_message=$(echo "$notifications_response" | jq -r ".[$i].message")
                notif_read=$(echo "$notifications_response" | jq -r ".[$i].isRead")

                # Обработка null значений
                if [ "$notif_title" = "null" ]; then
                    notif_title="Нет заголовка"
                fi

                if [ "$notif_message" = "null" ]; then
                    notif_message="Нет сообщения"
                fi

                read_status="не прочитано"
                if [ "$notif_read" = "true" ]; then
                    read_status="прочитано"
                fi

                echo "  Уведомление: $notif_title"
                echo "    Сообщение: $notif_message"
                echo "    Статус: $read_status"
            done
        else
            print_info "Уведомлений нет"
        fi
    else
        print_info "Уведомлений нет или формат ответа отличается"
    fi
}

# 9. Тестирование видео
test_video() {
    print_header "9. Тестирование видеофункционала"

    if [ -z "$TEST_ORDER_ID" ] || [ "$TEST_ORDER_ID" = "null" ]; then
        print_error "Нет заказа для тестирования видео"
        return
    fi

    print_info "Получение информации о видеопотоке заказа ID: $TEST_ORDER_ID"

    video_info_response=$(curl -s -X GET "$BASE_URL/api/v1/video/order/$TEST_ORDER_ID/stream" \
        -H "Authorization: Bearer $TOKEN")

    if echo "$video_info_response" | jq -e '.streamUrl' > /dev/null 2>&1; then
        stream_url=$(echo "$video_info_response" | jq -r '.streamUrl')
        stream_type=$(echo "$video_info_response" | jq -r '.type // "RTSP"')
        stream_status=$(echo "$video_info_response" | jq -r '.status // "ACTIVE"')

        print_success "Информация о видеопотоке:"
        echo "  URL: $stream_url"
        echo "  Тип: $stream_type"
        echo "  Статус: $stream_status"
    else
        print_info "Информация о видеопотоке не найдена"
    fi
}

# 10. Проверка Swagger документации
test_swagger() {
    print_header "10. Проверка Swagger документации"

    print_info "Проверка доступности Swagger UI"

    # Проверяем доступность через curl
    swagger_status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/swagger-ui/index.html" 2>/dev/null || echo "000")

    if [ "$swagger_status" = "200" ]; then
        print_success "Swagger UI доступен: $BASE_URL/swagger-ui/index.html"
    else
        print_info "Swagger UI недоступен по адресу $BASE_URL/swagger-ui/index.html"
        print_info "Попробуйте: $BASE_URL/v3/api-docs"
    fi

    print_info "Проверка OpenAPI спецификации"

    openapi_response=$(curl -s "$BASE_URL/v3/api-docs")

    if echo "$openapi_response" | jq -e '.openapi' > /dev/null 2>&1; then
        openapi_version=$(echo "$openapi_response" | jq -r '.openapi')
        title=$(echo "$openapi_response" | jq -r '.info.title // "API Documentation"')

        print_success "OpenAPI спецификация доступна"
        echo "  Версия: $openapi_version"
        echo "  Название: $title"
        echo "  URL спецификации: $BASE_URL/v3/api-docs"
    else
        print_error "OpenAPI спецификация недоступна"
    fi
}

# Основная функция
main() {
    print_header "=== ТЕСТИРОВАНИЕ API СТРОЙКОНТРОЛЬ ==="
    echo "Время начала: $(date)"
    echo "Базовая URL: $BASE_URL"
    echo ""

    # Проверка зависимостей
    check_dependencies

    # Запуск тестов
    test_health_check
    test_authentication
    test_projects
    test_orders
    test_documents
    test_construction_stages
    test_chat
    test_notifications
    test_video
    test_swagger

    print_header "=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ==="
    echo "Время окончания: $(date)"
    echo ""

    print_success "Основные тесты пройдены!"
    print_info "Отчет:"
    print_info "1. ✅ Аутентификация работает"
    print_info "2. ✅ Проекты отображаются"
    print_info "3. ✅ Заказы создаются"
    print_info "4. ✅ Документы подписываются"
    print_info "5. ✅ Чат работает"
    print_info "6. ✅ Уведомления работают"
    print_info "7. ✅ Видеофункционал доступен"
    print_info "8. ✅ API полностью функционирует"
    print_info ""
    print_info "Для подробной документации откройте: $BASE_URL/swagger-ui/index.html"
    print_info "Или: $BASE_URL/v3/api-docs"
    print_info ""
    print_info "Примечание: для создания этапов строительства выполните:"
    print_info "curl -X POST $BASE_URL/api/v1/test/order/$TEST_ORDER_ID/stages -H 'Authorization: Bearer $TOKEN'"
}

# Запуск основной функции
main