package io.group32.dto.response.orders;

import io.group32.model.Order;
import lombok.Data;

@Data
public class OrderDTO {
    private Long listingId;
    private String title;
    private String mainImageUrl;
    private double priceAtPurchase;
    private String sellerUsername;
    private String createdAt;

    public static OrderDTO fromOrder(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setListingId(order.getListing().getId());
        dto.setTitle(order.getListing().getTitle());

        String mainImage = order.getListing().getImages().isEmpty()
                ? "/placeholder.png"
                : order.getListing().getImages().get(0).getImageUrl();

        dto.setMainImageUrl(mainImage);
        dto.setPriceAtPurchase(order.getPriceAtPurchase());
        dto.setSellerUsername(order.getListing().getUser().getUsername());
        dto.setCreatedAt(order.getCreatedAt().toString());
        return dto;
    }
}
