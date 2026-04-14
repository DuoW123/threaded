package io.group32.service;

import io.group32.dto.response.orders.OrderDTO;
import io.group32.model.*;
import io.group32.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            listing.setListingStatus(ListingStatus.SOLD);
            listingRepository.save(listing);
        }

        cartItemRepository.deleteAll(items);
    }

    public List<OrderDTO> getOrdersForUser(User user) {
        List<Order> buyerOrders = orderRepository.findByBuyer(user);
        List<Order> sellerOrders = orderRepository.findByListing_User(user);

        return Stream.concat(buyerOrders.stream(), sellerOrders.stream())
                .distinct()
                .map(OrderDTO::fromOrder)
                .toList();
    }

    public OrderDTO getOrderByIdForUser(User user, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (!order.getBuyer().getId().equals(user.getId()) &&
            !order.getListing().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this order");
        }
        return OrderDTO.fromOrder(order);
    }

    @Transactional
    public void markAsShipped(User user, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getListing().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Only the seller can mark the order as shipped");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Order status cannot be shipped, if it's currently not paid");
        }

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    @Transactional
    public void markAsReceived(User user, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getId().equals(user.getId())) {
            throw new RuntimeException("Only the buyer can confirm the order has arrived");
        }

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Order status must be shipped before it's received");
        }

        order.setStatus(OrderStatus.RECEIVED);
        orderRepository.save(order);
    }
}
