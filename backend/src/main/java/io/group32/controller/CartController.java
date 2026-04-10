package io.group32.controller;

import io.group32.model.CartItem;
import io.group32.service.CartService;
import io.group32.service.SessionService;
import io.group32.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final SessionService sessionService;

    public CartController(CartService cartService, SessionService sessionService) {
        this.cartService = cartService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<CartItem> getCart(HttpServletRequest request) {
        User user = sessionService.getUser(request);
        return cartService.getCart(user.getId());
    }

    @PostMapping("/add/{listingId}")
    public void addToCart(@PathVariable Long listingId, HttpServletRequest request) {
        User user = sessionService.getUser(request);
        cartService.addToCart(user.getId(), listingId);
    }

    @DeleteMapping("/remove/{listingId}")
    public void removeFromCart(@PathVariable Long listingId, HttpServletRequest request) {
        User user = sessionService.getUser(request);
        cartService.removeFromCart(user.getId(), listingId);
    }

    @PostMapping("/checkout")
    public void checkout(HttpServletRequest request) {
        User user = sessionService.getUser(request);
        cartService.checkout(user.getId());
    }
}
