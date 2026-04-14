package io.group32.service;

import io.group32.model.CartItem;
import io.group32.model.Listing;
import io.group32.model.ListingStatus;
import io.group32.repository.CartItemRepository;
import io.group32.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final ListingRepository listingRepo;

    public CartService(CartItemRepository cartRepo, ListingRepository listingRepo) {
        this.cartRepo = cartRepo;
        this.listingRepo = listingRepo;
    }

    public List<CartItem> getCart(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    public void addToCart(Long userId, Long listingId) {
        Listing listing = listingRepo.findById(listingId).orElseThrow();

        if (listing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cannot buy your own listing");
        }

        if (listing.getListingStatus() == ListingStatus.SOLD) {
            throw new RuntimeException("Listing is already sold");
        }

        if (!cartRepo.existsByUserIdAndListingId(userId, listingId)) {
            cartRepo.save(new CartItem(userId, listingId));
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long listingId) {
        cartRepo.deleteByUserIdAndListingId(userId, listingId);
    }

//    @Transactional
//    public void checkout(Long userId) {
//        List<CartItem> items = cartRepo.findByUserId(userId);
//
//        for (CartItem item : items) {
//            Listing listing = listingRepo.findById(item.getListingId()).orElseThrow();
//            listing.setListingStatus(ListingStatus.SOLD);
//            listingRepo.save(listing);
//        }
//
//        cartRepo.deleteAll(items);
//    }
}
