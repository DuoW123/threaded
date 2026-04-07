package io.group32.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "listings")
@Data
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String itemCondition;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private ListingStatus listingStatus;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListingImage> images = new ArrayList<>();
}
