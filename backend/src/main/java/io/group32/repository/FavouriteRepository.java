package io.group32.repository;

import io.group32.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUserId(Long userId);
    boolean existsByUserIdAndListingId(Long userId, Long listingId);

    @Transactional
    void deleteByUserIdAndListingId(Long userId, Long listingId);
}
