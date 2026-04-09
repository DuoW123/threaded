package io.group32.dto.request.listings;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateListingRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String brand;
    private String size;
    private String itemCondition;
    private String category;
    private List<String> deleteImages;
}
