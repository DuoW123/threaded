package io.group32.controller;

import io.group32.dto.request.listings.CreateListingRequest;
import io.group32.model.Listing;
import io.group32.service.ListingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createListing(@RequestPart("data")CreateListingRequest request, @RequestPart("images")List<MultipartFile> images, HttpServletRequest httpRequest) {
        return listingService.handleCreateListing(request, images, httpRequest);
    }

    @GetMapping("/{id}")
    public Listing getListing(@PathVariable Long id) {
        return listingService.getListingById(id);
    }

    @GetMapping
    public List<Listing> getListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return listingService.getListings(search, size, condition, category, minPrice, maxPrice);
    }

    @GetMapping("/mine")
    public List<Listing> getMyListings(HttpServletRequest request) {
        return listingService.getListingsForCurrentUser(request);
    }
}
