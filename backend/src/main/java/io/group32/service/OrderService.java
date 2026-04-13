package io.group32.service;

import io.group32.dto.response.orders.OrderDTO;
import io.group32.model.*;
import io.group32.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ListingRepository listingRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        ListingRepository listingRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.listingRepository = listingRepository;
    }

    @Transactional
    public void createOrdersFromCart(User user) {
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());

        for (CartItem item : items) {
            Listing listing = listingRepository.findById(item.getListingId()).orElseThrow();

            Order order = new Order();
            order.setBuyer(user);
            order.setListing(listing);
            order.setPriceAtPurchase(listing.getPrice().doubleValue());
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("COMPLETED");

            orderRepository.save(order);
        }

        cartItemRepository.deleteAll(items);
    }

    public List<OrderDTO> getOrdersForUser(User user) {
        return orderRepository.findByBuyer(user)
                .stream()
                .map(OrderDTO::fromOrder)
                .toList();
    }
}
