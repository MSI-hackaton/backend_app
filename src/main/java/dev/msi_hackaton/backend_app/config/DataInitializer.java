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
            ProjectPhotoRepository projectPhotoRepository) {

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

                ConstructionStage construction = new ConstructionStage();
                construction.setRequest(request);
                construction.setProject(project);
                construction.setCustomer(customer);
                construction.setName("Фундамент");
                construction.setDescription("Заливка фундамента для здания");
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
                stream1.setStreamUrl("rtsp://demo.stream:554/live.sdp");
                stream1.setCameraName("Камера 1 - Фасад");
                stream1.setCameraLocation("Северная сторона");
                stream1.setThumbnailUrl("https://via.placeholder.com/320x240?text=Фасад");
                stream1.setIsActive(true);

                VideoStream stream2 = new VideoStream();
                stream2.setConstruction(construction);
                stream2.setStreamUrl("rtsp://demo.stream:554/backyard.sdp");
                stream2.setCameraName("Камера 2 - Внутренний двор");
                stream2.setCameraLocation("Южная сторона");
                stream2.setThumbnailUrl("https://via.placeholder.com/320x240?text=Двор");
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
            }
        };
    }
}