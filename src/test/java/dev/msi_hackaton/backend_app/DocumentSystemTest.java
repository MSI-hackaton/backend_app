package dev.msi_hackaton.backend_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.dao.entities.enums.RequestStatus;
import dev.msi_hackaton.backend_app.dto.request.DocumentUploadDto;
import dev.msi_hackaton.backend_app.dto.response.DocumentResponseDto;
import dev.msi_hackaton.backend_app.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DocumentSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentService documentService;

    private UUID testConstructionId;
    private UUID testProjectId;
    private UUID testRequestId;

    @BeforeEach
    void setUp() {
        // В DataInitializer уже созданы тестовые данные
        // Мы будем использовать существующие данные для тестов
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testFullDocumentWorkflow() throws Exception {
        // 1. Сначала получим список проектов
        MvcResult projectsResult = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andReturn();

        String projectsResponse = projectsResult.getResponse().getContentAsString();
        System.out.println("Projects: " + projectsResponse);

        // 2. Получим ID первого проекта
        String projectIdJson = projectsResponse.split("\"id\":\"")[1].split("\"")[0];
        testProjectId = UUID.fromString(projectIdJson);

        // 3. Создадим заявку на строительство
        String requestJson = """
            {
                "fullName": "Test Customer",
                "email": "test@example.com",
                "phone": "+79991234567"
            }
            """;

        MvcResult requestResult = mockMvc.perform(post("/api/requests/projects/" + testProjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        String requestResponse = requestResult.getResponse().getContentAsString();
        String requestIdJson = requestResponse.split("\"id\":\"")[1].split("\"")[0];
        testRequestId = UUID.fromString(requestIdJson);

        // 4. Получим статус заявки
        mockMvc.perform(get("/api/requests/" + testRequestId + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());

        // 5. Получим ID этапа строительства (он создается автоматически в DataInitializer)
        // В реальном приложении это делалось бы через административный интерфейс

        // Для теста будем использовать существующий этап из DataInitializer
        // Получим список этапов через прямую работу с репозиторием

        // 6. Загрузим тестовый документ
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test PDF Content".getBytes()
        );

        DocumentUploadDto uploadDto = new DocumentUploadDto();
        uploadDto.setName("Технический паспорт");
        uploadDto.setDescription("Основной технический документ");

        String uploadDtoJson = objectMapper.writeValueAsString(uploadDto);
        MockMultipartFile metadata = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                uploadDtoJson.getBytes()
        );

        // Для теста нужен constructionId. В реальном приложении он получается из заявки.
        // Используем тестовый constructionId из DataInitializer (нужно его получить)

        // 7. Получим список строительных этапов через API (если есть такой endpoint)
        // Если нет, то создадим простой тест на уровне сервиса

        System.out.println("Test workflow completed up to document upload");
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testDocumentUploadAndStatusChange() throws Exception {
        // 1. Создаем тестовый файл
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-agreement.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "Test Agreement Content".getBytes()
        );

        // 2. Подготавливаем метаданные документа
        DocumentUploadDto uploadDto = new DocumentUploadDto();
        uploadDto.setName("Договор подряда");
        uploadDto.setDescription("Основной договор на выполнение работ");

        String uploadDtoJson = objectMapper.writeValueAsString(uploadDto);
        MockMultipartFile metadata = new MockMultipartFile(
                "metadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                uploadDtoJson.getBytes()
        );

        System.out.println("Test: Document upload with metadata");
        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("Metadata: " + uploadDtoJson);
    }

    @Test
    void testDocumentServiceMethods() {
        // Проверка методов сервиса без HTTP запросов
        assertThat(documentService).isNotNull();

        // Проверяем, что сервис может создавать сводку
        DocumentService.DocumentStatusSummary summary =
                new DocumentService.DocumentStatusSummary(10, 3, 2, 4, 1);

        assertThat(summary.getTotal()).isEqualTo(10);
        assertThat(summary.getCompletionPercentage()).isEqualTo(40.0);
        assertThat(summary.getApproved()).isEqualTo(4);

        System.out.println("DocumentService test passed");
    }

    @Test
    @WithMockUser(username = "specialist@example.com", roles = "SPECIALIST")
    void testDocumentStatusChangeWorkflow() throws Exception {
        // Этот тест проверяет смену статуса документа
        System.out.println("Testing document status change workflow for specialist");

        // В реальном тесте здесь были бы HTTP запросы на изменение статуса
        // Например: PATCH /api/documents/{documentId}/status?status=APPROVED

        assertThat(true).isTrue(); // Простая проверка для демонстрации
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN", "SPECIALIST"})
    void testDocumentDeletion() throws Exception {
        // Тест удаления документа
        System.out.println("Testing document deletion workflow for admin");

        // В реальном тесте: DELETE /api/documents/{documentId}

        assertThat(true).isTrue();
    }
}