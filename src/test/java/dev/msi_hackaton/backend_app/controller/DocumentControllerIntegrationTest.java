package dev.msi_hackaton.backend_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConstructionStageRepository constructionStageRepository;

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetDocumentsEndpoint() throws Exception {
        // Получаем список этапов строительства
        List<ConstructionStage> stages = constructionStageRepository.findAll();

        assertThat(stages).isNotEmpty();

        if (!stages.isEmpty()) {
            UUID constructionId = stages.get(0).getId();

            // Тестируем endpoint получения документов
            mockMvc.perform(get("/api/documents/constructions/" + constructionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            System.out.println("GET /api/documents/constructions/" + constructionId + " - OK");
        }
    }

    @Test
    @WithMockUser(username = "specialist@example.com", roles = "SPECIALIST")
    void testDocumentStatusUpdate() throws Exception {
        // Тест изменения статуса документа
        List<ConstructionStage> stages = constructionStageRepository.findAll();

        assertThat(stages).isNotEmpty();

        if (!stages.isEmpty()) {
            UUID constructionId = stages.get(0).getId();

            // Сначала создаем тестовый документ
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );

            String metadata = """
                {
                    "name": "Тестовый документ",
                    "description": "Для тестирования изменения статуса"
                }
                """;

            MockMultipartFile metadataPart = new MockMultipartFile(
                    "metadata",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    metadata.getBytes()
            );

            mockMvc.perform(multipart("/api/documents/constructions/" + constructionId + "/upload")
                            .file(file)
                            .file(metadataPart))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Тестовый документ"))
                    .andExpect(jsonPath("$.status").value("UPLOADED"));
        }
    }
}