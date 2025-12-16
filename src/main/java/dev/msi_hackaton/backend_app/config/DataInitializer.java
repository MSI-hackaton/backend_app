package dev.msi_hackaton.backend_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import dev.msi_hackaton.backend_app.dao.entities.enums.UserRole;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(ProjectRepository projectRepository, UserRepository userRepository) {
        return args -> {
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

            if (userRepository.count() == 0) {

                User user = new User();
                user.setEmail("test.user@example.com");
                user.setPhone("+79990000000");
                user.setFullName("Тестовый Пользователь");
                user.setRole(UserRole.CUSTOMER);

                userRepository.save(user);

                System.out.println("✔ Test user inserted");
            }

        };
    }
}
