package io.group32.service;

import io.group32.model.Favourite;
import io.group32.model.Listing;
import io.group32.repository.FavouriteRepository;
import io.group32.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final ListingRepository listingRepository;

    public FavouriteService(FavouriteRepository favouriteRepository, ListingRepository listingRepository) {
        this.favouriteRepository = favouriteRepository;
        this.listingRepository = listingRepository;
    }

    public void addFavourite(Long userId, Long listingId) {
        if (!favouriteRepository.existsByUserIdAndListingId(userId, listingId)) {
            Favourite f = new Favourite();
            f.setUserId(userId);
            f.setListingId(listingId);
            favouriteRepository.save(f);
        }
    }

    public void removeFavourite(Long userId, Long listingId) {
        if (favouriteRepository.existsByUserIdAndListingId(userId, listingId)) {
            favouriteRepository.deleteByUserIdAndListingId(userId, listingId);
        }
    }

    public List<Listing> getFavourites(Long userId) {
        return favouriteRepository.findByUserId(userId)
                .stream()
                .map(f -> listingRepository.findById(f.getListingId()).orElse(null))
                .filter(l -> l != null)
                .toList();
    }
}
