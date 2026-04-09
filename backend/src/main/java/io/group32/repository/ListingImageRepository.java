package io.group32.repository;

import io.group32.model.Listing;
import io.group32.model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListing(Listing listing);
    Optional<ListingImage> findByPublicId(String publicId);
}
