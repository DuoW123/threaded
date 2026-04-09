package io.group32.repository.specification;

import io.group32.model.Listing;
import io.group32.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ListingSpecifications {
    public static Specification<Listing> fuzzySearch(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return (listing, query, builder) -> {
            String lowerText = text.toLowerCase();

            return builder.or(
                    builder.greaterThan(builder.function("similarity", Double.class, builder.lower(listing.get("title")), builder.literal(lowerText)), 0.20),
                    builder.like(builder.lower(listing.get("title")), "%" + lowerText + "%"),
                    builder.greaterThan(builder.function("similarity", Double.class, builder.lower(listing.get("description")), builder.literal(lowerText)), 0.20),
                    builder.greaterThan(builder.function("similarity", Double.class, builder.lower(listing.get("brand")), builder.literal(lowerText)), 0.20)
            );
        };
    }

    public static Specification<Listing> hasSize(String size) {
        return(listing, query, builder) -> size == null ? null : builder.equal(listing.get("size"), size);
    }

    public static Specification<Listing> hasCondition(String condition) {
        return(listing, query, builder) -> condition == null ? null : builder.equal(listing.get("itemCondition"), condition);
    }

    public static Specification<Listing> hasCategory(String category) {
        return(listing, query, builder) -> category == null ? null : builder.equal(listing.get("category"), category);
    }

    public static Specification<Listing> priceBetween(BigDecimal min, BigDecimal max) {
        return (listing, query, builder) -> {
            if (min == null && max == null) {
                return null;
            }

            if (min != null && max != null) {
                return builder.between(listing.get("price"), min, max);
            } else if (max != null) {
                return builder.lessThanOrEqualTo(listing.get("price"), max);
            } else {
                return builder.greaterThanOrEqualTo(listing.get("price"), min);
            }
        };
    }

    public static Specification<Listing> belongsToUser(User user) {
        return (listing, query, builder) ->
                builder.equal(listing.get("user"), user);
    }

}