package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.OrderRequest;
import dev.msi_hackaton.backend_app.dto.response.OrderResponse;
import dev.msi_hackaton.backend_app.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Заказы", description = "API для работы с заказами")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить заказы пользователя")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/user/{userId}")
    @Operation(summary = "Получить детали заказа")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            @PathVariable Long userId) {
        OrderResponse order = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(order);
    }
}