package io.group32.controller;

import io.group32.dto.response.ApiResponse;
import io.group32.model.Listing;
import io.group32.model.User;
import io.group32.service.FavouriteService;
import io.group32.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;
    private final SessionService sessionService;

    public FavouriteController(FavouriteService favouriteService, SessionService sessionService) {
        this.favouriteService = favouriteService;
        this.sessionService = sessionService;
    }

    @PostMapping("/add/{listingId}")
    public ResponseEntity<ApiResponse<Void>> addFavourite(@PathVariable Long listingId, HttpServletRequest request) {
        User user = sessionService.getUser(request);
        favouriteService.addFavourite(user.getId(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Added to favourites", null));
    }

    @DeleteMapping("/remove/{listingId}")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(@PathVariable Long listingId, HttpServletRequest request) {
        User user = sessionService.getUser(request);
        favouriteService.removeFavourite(user.getId(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Removed from favourites", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Listing>>> getMyFavourites(HttpServletRequest request) {
        User user = sessionService.getUser(request);
        List<Listing> favourites = favouriteService.getFavourites(user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Favourites loaded", favourites));
    }
}
