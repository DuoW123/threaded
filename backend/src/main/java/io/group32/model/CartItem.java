package io.group32.model;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long listingId;

    public CartItem() {}

    public CartItem(Long userId, Long listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getListingId() {
        return listingId;
    }
}
