package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.entity.ConstructionStage;
import dev.msi_hackaton.backend_app.entity.Order;
import dev.msi_hackaton.backend_app.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConstructionStageService {

    private final ConstructionStageRepository constructionStageRepository;
    private final OrderRepository orderRepository;

    public ConstructionStageService(ConstructionStageRepository constructionStageRepository,
                                    OrderRepository orderRepository) {
        this.constructionStageRepository = constructionStageRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<ConstructionStage> getOrderStages(Long orderId) {
        return constructionStageRepository.findByOrder_IdOrderBySequence(orderId);
    }

    @Transactional
    public void updateStageProgress(Long stageId, Integer percentage) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Этап не найден"));

        if (percentage < 0 || percentage > 100) {
            throw new RuntimeException("Процент должен быть от 0 до 100");
        }

        stage.setCompletionPercentage(percentage);

        if (percentage == 100) {
            stage.setStatus(ConstructionStage.StageStatus.COMPLETED);
            stage.setEndDate(LocalDateTime.now());
        } else if (percentage > 0) {
            stage.setStatus(ConstructionStage.StageStatus.IN_PROGRESS);
            if (stage.getStartDate() == null) {
                stage.setStartDate(LocalDateTime.now());
            }
        }

        constructionStageRepository.save(stage);
        updateOrderProgress(stage.getOrder().getId());
    }

    @Transactional
    public void createDefaultStages(Order order) {
        List<ConstructionStage> stages = List.of(
                createStage("Подготовка участка", "Расчистка и разметка участка", 1, 5),
                createStage("Фундамент", "Закладка фундамента", 2, 20),
                createStage("Стены", "Возведение стен", 3, 30),
                createStage("Кровля", "Устройство кровли", 4, 15),
                createStage("Окна и двери", "Установка окон и дверей", 5, 10),
                createStage("Внутренняя отделка", "Внутренние отделочные работы", 6, 15),
                createStage("Наружная отделка", "Фасадные работы", 7, 5),
                createStage("Благоустройство", "Обустройство территории", 8, 10)
        );

        for (ConstructionStage stage : stages) {
            stage.setOrder(order);
            constructionStageRepository.save(stage);
        }
    }

    private ConstructionStage createStage(String title, String description, int sequence, int durationDays) {
        ConstructionStage stage = new ConstructionStage();
        stage.setTitle(title);
        stage.setDescription(description);
        stage.setSequence(sequence);
        stage.setDurationDays(durationDays);
        stage.setStatus(ConstructionStage.StageStatus.PENDING);
        stage.setCompletionPercentage(0);
        return stage;
    }

    private void updateOrderProgress(Long orderId) {
        List<ConstructionStage> stages = getOrderStages(orderId);
        if (stages.isEmpty()) return;

        double totalPercentage = stages.stream()
                .mapToInt(ConstructionStage::getCompletionPercentage)
                .average()
                .orElse(0.0);
    }
}