package dev.msi_hackaton.backend_app.config;

import dev.msi_hackaton.backend_app.service.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.msi_hackaton.backend_app.dao.entities.*;
import dev.msi_hackaton.backend_app.dao.entities.enums.*;
import dev.msi_hackaton.backend_app.dao.repository.*;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            ConstructionRequestRepository requestRepository,
            ConstructionStageRepository stageRepository,
            VideoStreamRepository videoStreamRepository,
            PhotoRepository photoRepository,
            ProjectPhotoRepository projectPhotoRepository,
            FileStorageService storageService) {

        return args -> {
            // Проекты
            if (projectRepository.count() == 0) {
                Project p1 = new Project();
                p1.setTitle("Дом 120 м²");
                p1.setDescription("Современный дом с панорамными окнами.");
                p1.setArea(120.0);
                p1.setFloors(2);
                p1.setPrice(7800000.0);
                p1.setConstructionTime(8);
                p1.setStatus(ProjectStatus.AVAILABLE);

                Project p2 = new Project();
                p2.setTitle("Коттедж 200 м²");
                p2.setDescription("Просторный коттедж для семьи.");
                p2.setArea(200.0);
                p2.setFloors(3);
                p2.setPrice(12500000.0);
                p2.setConstructionTime(12);
                p2.setStatus(ProjectStatus.AVAILABLE);

                projectRepository.save(p1);
                projectRepository.save(p2);

                System.out.println("✔ Test projects inserted");

                // Загружаем фото для каждого проекта
                // корневая папка с проектами
                Path photosRoot = Path.of("src/main/resources/photos");

                // связь папка → проект
                Map<String, Project> projectByFolder = Map.of(
                        "project-1", p1,
                        "project-2", p2
                );

                for (Map.Entry<String, Project> entry : projectByFolder.entrySet()) {

                    String folderName = entry.getKey();   // "project-1"
                    Project project = entry.getValue();   // p1

                    Path projectDir = photosRoot.resolve(folderName);

                    // если папки нет — пропускаем
                    if (!Files.exists(projectDir) || !Files.isDirectory(projectDir)) {
                        System.out.println("⚠ Папка не найдена: " + projectDir);
                        continue;
                    }

                    // идём по файлам внутри папки
                    Files.list(projectDir)
                            .filter(Files::isRegularFile)
                            .forEach(photoPath -> {
                                try {
                                    String fileName = photoPath.getFileName().toString();

                                    MockMultipartFile file = new MockMultipartFile(
                                            fileName,
                                            fileName,
                                            Files.probeContentType(photoPath),
                                            Files.readAllBytes(photoPath)
                                    );

                                    String fileUrl = storageService.upload(file);

                                    Photo photo = new Photo();
                                    photo.setUrl("api/files/" + fileUrl);
                                    photo = photoRepository.save(photo);

                                    ProjectPhoto projectPhoto = new ProjectPhoto();
                                    projectPhoto.setProject(project);
                                    projectPhoto.setPhoto(photo);

                                    projectPhotoRepository.save(projectPhoto);

                                    System.out.println("✔ " + fileName + " → " + project.getTitle());

                                } catch (Exception e) {
                                    throw new RuntimeException("Ошибка загрузки фото: " + photoPath, e);
                                }
                            });

                }
            }

            // Пользователи
            if (userRepository.count() == 0) {
                User customer = new User();
                customer.setEmail("customer@example.com");
                customer.setPhone("+79990000001");
                customer.setPasswordHash("111");
                customer.setFullName("Иван Иванов");
                customer.setRole(UserRole.CUSTOMER);

                User specialist = new User();
                specialist.setEmail("specialist@example.com");
                specialist.setPhone("+79990000002");
                specialist.setPasswordHash("111");
                specialist.setFullName("Петр Петров (Специалист)");
                specialist.setRole(UserRole.SPECIALIST);

                userRepository.save(customer);
                userRepository.save(specialist);
                System.out.println("✔ Test users inserted");
            }

            // Тестовая заявка
            if (requestRepository.count() == 0) {
                Project project = projectRepository.findAll().get(0);
                User customer = userRepository.findByEmail("customer@example.com").orElseThrow();

                ConstructionRequest request = new ConstructionRequest();
                request.setProject(project);
                request.setStatus(RequestStatus.APPROVED);
                request.setAnonymousFullName(customer.getFullName());
                request.setAnonymousEmail(customer.getEmail());
                request.setAnonymousPhone(customer.getPhone());

                request = requestRepository.save(request);
                System.out.println("✔ Test construction request is inserted");
            }

            // Тестовое строительство
            if (stageRepository.count() == 0) {
                Project project = projectRepository.findAll().get(0);
                User customer = userRepository.findByEmail("customer@example.com").orElseThrow();
                User specialist = userRepository.findByEmail("specialist@example.com").orElseThrow();
                ConstructionRequest request = requestRepository.findAll().get(0);

                ConstructionStage construction = new ConstructionStage();
                construction.setRequest(request);
                construction.setProject(project);
                construction.setCustomer(customer);
                construction.setName("Частный жилой дом");
                construction.setDescription("Строительство дома на ул. Лесной");
                construction.setStartDate(Instant.now()); // Текущая дата/время
                construction.setEndDate(Instant.now().plusSeconds(2592000)); // +30 дней
                construction.setStatus(StageStatus.PLANNED);
                construction.setSpecialist(specialist);

                construction = stageRepository.save(construction);

                System.out.println("✔ Test construction stage is inserted");
            }

            // Тестовое видео
            if (videoStreamRepository.count() == 0) {
                ConstructionStage construction = stageRepository.findAll().get(0);

                // Добавляем тестовые видеопотоки
                VideoStream stream1 = new VideoStream();
                stream1.setConstruction(construction);
                stream1.setStreamUrl("https://test-streams.mux.dev/test_001/stream.m3u8");
                stream1.setCameraName("Камера 1 - Фасад");
                stream1.setCameraLocation("Северная сторона");
                stream1.setThumbnailUrl("https://test-streams.mux.dev/test_001/stream.m3u8?text=Фасад");
                stream1.setIsActive(true);

                VideoStream stream2 = new VideoStream();
                stream2.setConstruction(construction);
                stream2.setStreamUrl("https://test-streams.mux.dev/dai-discontinuity-deltatre/manifest.m3u8");
                stream2.setCameraName("Камера 2 - Внутренний двор");
                stream2.setCameraLocation("Южная сторона");
                stream2.setThumbnailUrl("https://test-streams.mux.dev/dai-discontinuity-deltatre/manifest.m3u8?text=Двор");
                stream2.setIsActive(true);

                videoStreamRepository.save(stream1);
                videoStreamRepository.save(stream2);
                System.out.println("✔ Test video streams inserted");
            }

            /*// Добавляем тестовые фотографии к проекту
            if (photoRepository.count() == 0 && projectPhotoRepository.count() == 0) {
                Photo photo1 = new Photo();
                photo1.setUrl("https://via.placeholder.com/800x600?text=Проект+1");

                Photo photo2 = new Photo();
                photo2.setUrl("https://via.placeholder.com/800x600?text=Интерьер");

                photo1 = photoRepository.save(photo1);
                photo2 = photoRepository.save(photo2);

                Project projectWithPhotos = projectRepository.findAll().get(0);

                ProjectPhoto projectPhoto1 = new ProjectPhoto();
                projectPhoto1.setProject(projectWithPhotos);
                projectPhoto1.setPhoto(photo1);
                projectPhoto1.setSortOrder(1);
                projectPhoto1.setDescription("Вид фасада");

                ProjectPhoto projectPhoto2 = new ProjectPhoto();
                projectPhoto2.setProject(projectWithPhotos);
                projectPhoto2.setPhoto(photo2);
                projectPhoto2.setSortOrder(2);
                projectPhoto2.setDescription("Интерьер гостиной");

                projectPhotoRepository.save(projectPhoto1);
                projectPhotoRepository.save(projectPhoto2);

                System.out.println("✔ Test photos inserted");
            }*/
        };
    }
}