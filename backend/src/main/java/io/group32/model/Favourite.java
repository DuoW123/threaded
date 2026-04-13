package io.group32.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favourites")
@Data
public class Favourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long listingId;
}
