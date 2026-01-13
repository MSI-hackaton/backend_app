package dev.msi_hackaton.backend_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.msi_hackaton.backend_app.dao.entities.*;
import dev.msi_hackaton.backend_app.dao.entities.enums.*;
import dev.msi_hackaton.backend_app.dao.repository.*;

import java.time.Instant;
import java.util.List;

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
            StageReportRepository stageReportRepository,
            ReportPhotoRepository reportPhotoRepository) {

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

                // Создаем несколько этапов строительства
                ConstructionStage foundationStage = new ConstructionStage();
                foundationStage.setRequest(request);
                foundationStage.setProject(project);
                foundationStage.setCustomer(customer);
                foundationStage.setName("Фундамент");
                foundationStage.setDescription("Заливка фундамента для здания");
                foundationStage.setStartDate(Instant.now());
                foundationStage.setEndDate(Instant.now().plusSeconds(2592000)); // +30 дней
                foundationStage.setStatus(StageStatus.COMPLETED);
                foundationStage.setSpecialist(specialist);
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

                ConstructionStage wallsStage = new ConstructionStage();
                wallsStage.setRequest(request);
                wallsStage.setProject(project);
                wallsStage.setCustomer(customer);
                wallsStage.setName("Стены и перекрытия");
                wallsStage.setDescription("Возведение стен и монтаж перекрытий");
                wallsStage.setStartDate(Instant.now().plusSeconds(2592000)); // +30 дней
                wallsStage.setEndDate(Instant.now().plusSeconds(5184000)); // +60 дней
                wallsStage.setStatus(StageStatus.IN_PROGRESS);
                wallsStage.setSpecialist(specialist);

                ConstructionStage roofStage = new ConstructionStage();
                roofStage.setRequest(request);
                roofStage.setProject(project);
                roofStage.setCustomer(customer);
                roofStage.setName("Кровля");
                roofStage.setDescription("Монтаж кровельной системы");
                roofStage.setStartDate(Instant.now().plusSeconds(5184000)); // +60 дней
                roofStage.setEndDate(Instant.now().plusSeconds(7776000)); // +90 дней
                roofStage.setStatus(StageStatus.PLANNED);
                roofStage.setSpecialist(specialist);

                ConstructionStage finishingStage = new ConstructionStage();
                finishingStage.setRequest(request);
                finishingStage.setProject(project);
                finishingStage.setCustomer(customer);
                finishingStage.setName("Отделка");
                finishingStage.setDescription("Внутренняя и внешняя отделка");
                finishingStage.setStartDate(Instant.now().plusSeconds(7776000)); // +90 дней
                finishingStage.setEndDate(Instant.now().plusSeconds(10368000)); // +120 дней
                finishingStage.setStatus(StageStatus.PLANNED);
                finishingStage.setSpecialist(specialist);

                stageRepository.save(foundationStage);
                stageRepository.save(wallsStage);
                stageRepository.save(roofStage);
                stageRepository.save(finishingStage);

                System.out.println("✔ Test construction stages inserted (4 stages)");
            }

            // Тестовое видео
            if (videoStreamRepository.count() == 0) {
                ConstructionStage construction = stageRepository.findByRequestId(
                        requestRepository.findAll().get(0).getId()
                ).get(0);

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

            // Добавляем тестовые фотографии к проекту
            if (photoRepository.count() == 0 && projectPhotoRepository.count() == 0) {
                Photo photo1 = new Photo();
                photo1.setUrl("https://via.placeholder.com/800x600?text=Проект+1");

                Photo photo2 = new Photo();
                photo2.setUrl("https://via.placeholder.com/800x600?text=Интерьер");

                Photo photo3 = new Photo();
                photo3.setUrl("https://via.placeholder.com/800x600?text=Фасад");

                Photo photo4 = new Photo();
                photo4.setUrl("https://via.placeholder.com/800x600?text=План+этажа");

                photo1 = photoRepository.save(photo1);
                photo2 = photoRepository.save(photo2);
                photo3 = photoRepository.save(photo3);
                photo4 = photoRepository.save(photo4);

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

                ProjectPhoto projectPhoto3 = new ProjectPhoto();
                projectPhoto3.setProject(projectWithPhotos);
                projectPhoto3.setPhoto(photo3);
                projectPhoto3.setSortOrder(3);
                projectPhoto3.setDescription("Фасад с другого ракурса");

                ProjectPhoto projectPhoto4 = new ProjectPhoto();
                projectPhoto4.setProject(projectWithPhotos);
                projectPhoto4.setPhoto(photo4);
                projectPhoto4.setSortOrder(4);
                projectPhoto4.setDescription("Планировка первого этажа");

                projectPhotoRepository.save(projectPhoto1);
                projectPhotoRepository.save(projectPhoto2);
                projectPhotoRepository.save(projectPhoto3);
                projectPhotoRepository.save(projectPhoto4);

                System.out.println("✔ Test photos inserted for projects");
            }

            // Тестовые отчеты об этапах
            if (stageReportRepository.count() == 0) {
                // Получаем первый этап (Фундамент)
                List<ConstructionStage> stages = stageRepository.findAll();
                ConstructionStage foundationStage = stages.stream()
                        .filter(stage -> "Фундамент".equals(stage.getName()))
                        .findFirst()
                        .orElse(stages.get(0));

                ConstructionStage wallsStage = stages.stream()
                        .filter(stage -> "Стены и перекрытия".equals(stage.getName()))
                        .findFirst()
                        .orElse(null);

                // Отчет по завершенному этапу (Фундамент)
                StageReport foundationReport = new StageReport();
                foundationReport.setStage(foundationStage);
                foundationReport.setDescription("""
                    Отчет по завершению работ по устройству фундамента.
                    
                    Выполненные работы:
                    1. Разработка котлована - 100%
                    2. Устройство песчаной подушки - 100%
                    3. Монтаж опалубки - 100%
                    4. Установка арматурного каркаса - 100%
                    5. Бетонирование - 100%
                    6. Демонтаж опалубки - 100%
                    7. Гидроизоляция - 100%
                    
                    Качество работ соответствует проектной документации.
                    Все работы выполнены в срок.
                    """);
                foundationReport.setStatus(ReportStatus.PUBLISHED);

                // Черновик отчета для текущего этапа (Стены)
                if (wallsStage != null) {
                    StageReport wallsReport = new StageReport();
                    wallsReport.setStage(wallsStage);
                    wallsReport.setDescription("""
                        Черновик отчета по возведению стен.
                        
                        Текущий прогресс:
                        - Кладка несущих стен: 70%
                        - Установка перекрытий: 40%
                        - Монтаж оконных проемов: 30%
                        
                        Ожидаемое завершение: через 2 недели.
                        """);
                    wallsReport.setStatus(ReportStatus.DRAFT);
                    stageReportRepository.save(wallsReport);
                }

                stageReportRepository.save(foundationReport);
                System.out.println("✔ Test stage reports inserted");
            }

            // Тестовые фотографии для отчетов
            if (reportPhotoRepository.count() == 0) {
                // Получаем отчет по фундаменту
                List<StageReport> reports = stageReportRepository.findAll();
                StageReport foundationReport = reports.stream()
                        .filter(report -> report.getStatus() == ReportStatus.PUBLISHED)
                        .findFirst()
                        .orElse(reports.get(0));

                // Добавляем фото к отчету
                ReportPhoto photo1 = new ReportPhoto();
                photo1.setReport(foundationReport);
                photo1.setUrl("https://via.placeholder.com/800x600?text=Готовый+фундамент");
                photo1.setDescription("Готовый ленточный фундамент перед гидроизоляцией");

                ReportPhoto photo2 = new ReportPhoto();
                photo2.setReport(foundationReport);
                photo2.setUrl("https://via.placeholder.com/800x600?text=Арматурный+каркас");
                photo2.setDescription("Арматурный каркас перед заливкой бетона");

                ReportPhoto photo3 = new ReportPhoto();
                photo3.setReport(foundationReport);
                photo3.setUrl("https://via.placeholder.com/800x600?text=Бетонирование");
                photo3.setDescription("Процесс заливки бетона в опалубку");

                ReportPhoto photo4 = new ReportPhoto();
                photo4.setReport(foundationReport);
                photo4.setUrl("https://via.placeholder.com/800x600?text=Гидроизоляция");
                photo4.setDescription("Нанесение гидроизоляционного материала");

                reportPhotoRepository.save(photo1);
                reportPhotoRepository.save(photo2);
                reportPhotoRepository.save(photo3);
                reportPhotoRepository.save(photo4);

                // Если есть отчет по стенам, добавляем фото для него
                StageReport wallsReport = reports.stream()
                        .filter(report -> report.getStatus() == ReportStatus.DRAFT)
                        .findFirst()
                        .orElse(null);

                if (wallsReport != null) {
                    ReportPhoto wallsPhoto1 = new ReportPhoto();
                    wallsPhoto1.setReport(wallsReport);
                    wallsPhoto1.setUrl("https://via.placeholder.com/800x600?text=Кладка+стен");
                    wallsPhoto1.setDescription("Процесс кладки несущих стен");

                    ReportPhoto wallsPhoto2 = new ReportPhoto();
                    wallsPhoto2.setReport(wallsReport);
                    wallsPhoto2.setUrl("https://via.placeholder.com/800x600?text=Монтаж+перекрытий");
                    wallsPhoto2.setDescription("Установка межэтажных перекрытий");

                    reportPhotoRepository.save(wallsPhoto1);
                    reportPhotoRepository.save(wallsPhoto2);
                }

                System.out.println("✔ Test report photos inserted");
            }

            // Тестовые документы (если нужно, можно добавить)
            System.out.println("\n" + "=".repeat(50));
            System.out.println("✅ Все тестовые данные успешно загружены:");
            System.out.println("- 2 проекта");
            System.out.println("- 2 пользователя (customer, specialist)");
            System.out.println("- 1 заявка на строительство");
            System.out.println("- 4 этапа строительства (Фундамент, Стены, Кровля, Отделка)");
            System.out.println("- 2 видеопотока");
            System.out.println("- 4 фотографии проекта");
            System.out.println("- 2 отчета по этапам (опубликованный и черновик)");
            System.out.println("- 6 фотографий в отчетах");
            System.out.println("=".repeat(50));
        };
    }
}