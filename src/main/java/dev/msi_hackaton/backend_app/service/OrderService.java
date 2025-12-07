package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dto.request.OrderRequest;
import dev.msi_hackaton.backend_app.dto.response.OrderResponse;
import dev.msi_hackaton.backend_app.entity.*;
import dev.msi_hackaton.backend_app.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.repository.OrderRepository;
import dev.msi_hackaton.backend_app.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final DocumentService documentService;
    private final ConstructionStageRepository constructionStageRepository;

    public OrderService(OrderRepository orderRepository,
                        ProjectRepository projectRepository,
                        UserRepository userRepository,
                        DocumentService documentService,
                        ConstructionStageRepository constructionStageRepository) {
        this.orderRepository = orderRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.documentService = documentService;
        this.constructionStageRepository = constructionStageRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Проект не найден с id: " + request.getProjectId()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> createUser(request));

        Order order = new Order();
        order.setUser(user);
        order.setProject(project);
        order.setComment(request.getComment());
        order.setConstructionAddress(request.getAddress());
        order.setTotalPrice(project.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Создаем начальные документы
        documentService.createInitialDocuments(savedOrder);

        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUser_Id(userId).stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus(status);

        if (status == Order.OrderStatus.IN_PROGRESS && order.getStartDate() == null) {
            order.setStartDate(LocalDateTime.now());
            order.setEstimatedEndDate(LocalDateTime.now().plusMonths(
                    order.getProject().getConstructionTimeMonths()
            ));
        }

        orderRepository.save(order);
    }

    @Transactional
    public void createConstructionStages(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        // Создаем стандартные этапы строительства
        List<ConstructionStage> stages = List.of(
                createConstructionStage("Подготовка участка", 1, 5, order),
                createConstructionStage("Фундамент", 2, 20, order),
                createConstructionStage("Стены", 3, 30, order),
                createConstructionStage("Кровля", 4, 15, order),
                createConstructionStage("Окна и двери", 5, 10, order),
                createConstructionStage("Внутренняя отделка", 6, 15, order),
                createConstructionStage("Наружная отделка", 7, 5, order),
                createConstructionStage("Благоустройство", 8, 10, order)
        );

        // Сохраняем этапы
        for (ConstructionStage stage : stages) {
            constructionStageRepository.save(stage);
        }
    }

    private ConstructionStage createConstructionStage(String title, int sequence, int days, Order order) {
        ConstructionStage stage = new ConstructionStage();
        stage.setOrder(order);
        stage.setTitle(title);
        stage.setDescription("Этап строительства: " + title + " для заказа #" + order.getId());
        stage.setSequence(sequence);
        stage.setDurationDays(days);
        stage.setStatus(ConstructionStage.StageStatus.PENDING);
        stage.setCompletionPercentage(0);
        stage.setCreatedAt(LocalDateTime.now());
        return stage;
    }

    @Transactional
    public OrderResponse updateOrderAddress(Long orderId, String newAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setConstructionAddress(newAddress);
        Order updated = orderRepository.save(order);

        return OrderResponse.fromEntity(updated);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() == Order.OrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Невозможно отменить заказ в процессе строительства");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setActualEndDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders(Long userId) {
        List<Order> orders = orderRepository.findByUser_Id(userId);

        return orders.stream()
                .filter(order -> !order.isCompleted())
                .map(OrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double getOrderProgress(Long orderId) {
        Order order = getOrderEntity(orderId);
        return order.getProgressPercentage();
    }

    @Transactional(readOnly = true)
    public ConstructionStage getCurrentStage(Long orderId) {
        Order order = getOrderEntity(orderId);
        return order.getCurrentStage();
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (order.getStatus() != Order.OrderStatus.COMPLETION) {
            throw new RuntimeException("Заказ должен быть в статусе COMPLETION для завершения");
        }

        order.setStatus(Order.OrderStatus.FINAL_DOCUMENTS);
        order.setActualEndDate(LocalDateTime.now());
        orderRepository.save(order);

        // Создаем финальные документы
        documentService.createFinalDocuments(order);
    }

    private User createUser(OrderRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return userRepository.save(user);
    }
}