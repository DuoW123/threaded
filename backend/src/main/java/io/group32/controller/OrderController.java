package io.group32.controller;

import io.group32.dto.response.ApiResponse;
import io.group32.dto.response.orders.OrderDTO;
import io.group32.model.User;
import io.group32.service.OrderService;
import io.group32.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final SessionService sessionService;

    public OrderController(OrderService orderService, SessionService sessionService) {
        this.orderService = orderService;
        this.sessionService = sessionService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Void>> checkout(HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) return ResponseEntity.status(401).body(new ApiResponse<>(false, "User not found", null));

        orderService.createOrdersFromCart(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Checkout complete", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getMyOrders(HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) return ResponseEntity.status(401).body(new ApiResponse<>(false, "User not found", null));

        List<OrderDTO> orders = orderService.getOrdersForUser(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders loaded", orders));
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<Void>> markAsShipped(@PathVariable Long orderId, HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) return ResponseEntity.status(401).body(new ApiResponse<>(false, "User not found", null));

        try {
            orderService.markAsShipped(user, orderId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order successfully marked as shipped", null));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, exception.getMessage(), null));
        }
    }

    @PostMapping("/{orderId}/receive")
    public ResponseEntity<ApiResponse<Void>> markAsReceived(@PathVariable Long orderId, HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) return ResponseEntity.status(401).body(new ApiResponse<>(false, "User not found", null));

        try {
            orderService.markAsReceived(user, orderId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order successfully marked as received", null));
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, exception.getMessage(), null));
        }
    }
}