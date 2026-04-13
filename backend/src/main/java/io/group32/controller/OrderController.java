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
        orderService.createOrdersFromCart(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Checkout complete", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getMyOrders(HttpServletRequest request) {
        User user = sessionService.getUser(request);
        List<OrderDTO> orders = orderService.getOrdersForUser(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders loaded", orders));
    }
}
