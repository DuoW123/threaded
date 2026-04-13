package io.group32.repository;

import io.group32.model.Order;
import io.group32.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
}
