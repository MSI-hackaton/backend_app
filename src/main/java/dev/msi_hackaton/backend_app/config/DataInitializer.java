package dev.msi_hackaton.backend_app.config;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import dev.msi_hackaton.backend_app.dao.entities.enums.UserRole;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DataInitializer(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initializing test data...");
        initUsers();
        initProjects();
        log.info("✅ Test data initialized successfully");
    }

    private void initUsers() {
        // Проверяем, есть ли уже пользователи
        List<User> existingUsers = userRepository.findAll();

        // Тестовый клиент
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        if (existingUsers.stream().noneMatch(u -> u.getId().equals(customerId))) {
            User customer = new User();
            customer.setId(customerId);
            customer.setEmail("customer@test.com");
            customer.setPhone("+79991112233");
            customer.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK"); // password: test123
            customer.setSalt("salt1");
            customer.setFullName("Иван Петров");
            customer.setRole(UserRole.CUSTOMER);

            try {
                userRepository.saveAndFlush(customer);
                log.info("Created customer user: {}", customer.getEmail());
            } catch (Exception e) {
                log.error("Failed to create customer user: {}", e.getMessage());
            }
        }

        // Тестовый специалист
        UUID specialistId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        if (existingUsers.stream().noneMatch(u -> u.getId().equals(specialistId))) {
            User specialist = new User();
            specialist.setId(specialistId);
            specialist.setEmail("specialist@test.com");
            specialist.setPhone("+79992223344");
            specialist.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK"); // password: test123
            specialist.setSalt("salt2");
            specialist.setFullName("Алексей Смирнов");
            specialist.setRole(UserRole.SPECIALIST);

            try {
                userRepository.saveAndFlush(specialist);
                log.info("Created specialist user: {}", specialist.getEmail());
            } catch (Exception e) {
                log.error("Failed to create specialist user: {}", e.getMessage());
            }
        }

        // Тестовый администратор
        UUID adminId = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
        if (existingUsers.stream().noneMatch(u -> u.getId().equals(adminId))) {
            User admin = new User();
            admin.setId(adminId);
            admin.setEmail("admin@test.com");
            admin.setPhone("+79993334455");
            admin.setPasswordHash("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK"); // password: test123
            admin.setSalt("salt3");
            admin.setFullName("Администратор Системы");
            admin.setRole(UserRole.ADMIN);

            try {
                userRepository.saveAndFlush(admin);
                log.info("Created admin user: {}", admin.getEmail());
            } catch (Exception e) {
                log.error("Failed to create admin user: {}", e.getMessage());
            }
        }
    }

    private void initProjects() {
        List<Project> existingProjects = projectRepository.findAll();

        // Проект 1
        UUID project1Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
        if (existingProjects.stream().noneMatch(p -> p.getId().equals(project1Id))) {
            Project project1 = new Project();
            project1.setId(project1Id);
            project1.setTitle("Дом \"Стандарт\"");
            project1.setDescription("Комфортный одноэтажный дом для семьи из 3-4 человек. Площадь 120 м², 3 спальни, гостиная, кухня-столовая, 2 санузла.");
            project1.setArea(120.5);
            project1.setFloors(1);
            project1.setPrice(3500000.0);
            project1.setConstructionTime(90);
            project1.setStatus(ProjectStatus.AVAILABLE);

            try {
                projectRepository.saveAndFlush(project1);
                log.info("Created project: {}", project1.getTitle());
            } catch (Exception e) {
                log.error("Failed to create project: {}", e.getMessage());
            }
        }

        // Проект 2
        UUID project2Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440002");
        if (existingProjects.stream().noneMatch(p -> p.getId().equals(project2Id))) {
            Project project2 = new Project();
            project2.setId(project2Id);
            project2.setTitle("Дом \"Премиум\"");
            project2.setDescription("Двухэтажный дом с гаражом на 2 машины. Площадь 185 м², 4 спальни, кабинет, большая гостиная, кухня-столовая, 3 санузла.");
            project2.setArea(185.0);
            project2.setFloors(2);
            project2.setPrice(5500000.0);
            project2.setConstructionTime(120);
            project2.setStatus(ProjectStatus.AVAILABLE);

            try {
                projectRepository.saveAndFlush(project2);
                log.info("Created project: {}", project2.getTitle());
            } catch (Exception e) {
                log.error("Failed to create project: {}", e.getMessage());
            }
        }

        // Проект 3
        UUID project3Id = UUID.fromString("660e8400-e29b-41d4-a716-446655440003");
        if (existingProjects.stream().noneMatch(p -> p.getId().equals(project3Id))) {
            Project project3 = new Project();
            project3.setId(project3Id);
            project3.setTitle("Дом \"Эконом\"");
            project3.setDescription("Бюджетный вариант для молодой семьи. Площадь 85 м², 2 спальни, совмещенная гостиная-кухня, 1 санузел.");
            project3.setArea(85.0);
            project3.setFloors(1);
            project3.setPrice(2500000.0);
            project3.setConstructionTime(60);
            project3.setStatus(ProjectStatus.AVAILABLE);

            try {
                projectRepository.saveAndFlush(project3);
                log.info("Created project: {}", project3.getTitle());
            } catch (Exception e) {
                log.error("Failed to create project: {}", e.getMessage());
            }
        }
    }
}