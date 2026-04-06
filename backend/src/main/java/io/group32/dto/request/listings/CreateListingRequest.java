package io.group32.dto.request.listings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateListingRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String brand;
    private String size;
    private String itemCondition;
    private String category;
}
