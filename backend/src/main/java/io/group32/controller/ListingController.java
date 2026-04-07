package io.group32.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.group32.dto.request.listings.CreateListingRequest;
import io.group32.dto.request.listings.UpdateListingRequest;
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
    public String createListing(
            @RequestPart("data") String rawJson,
            @RequestPart("images") List<MultipartFile> images,
            HttpServletRequest httpRequest
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CreateListingRequest request = mapper.readValue(rawJson, CreateListingRequest.class);

            return listingService.handleCreateListing(request, images, httpRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/me")
    public List<Listing> getMyListings(HttpServletRequest request) {
        return listingService.getListingsForCurrentUser(request);
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

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateListing(
            @PathVariable Long id,
            @RequestPart("data") String rawJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            HttpServletRequest httpRequest
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UpdateListingRequest request = mapper.readValue(rawJson, UpdateListingRequest.class);

            return listingService.handleUpdateListing(id, request, newImages, httpRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteListing(
            Long id,
            HttpServletRequest httpRequest
    ) {
        return listingService.deleteListing(id, httpRequest);
    }
}
