package io.group32.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User buyer;

    @ManyToOne
    private Listing listing;

    private double priceAtPurchase;

    private LocalDateTime createdAt;

    private String status;
}
