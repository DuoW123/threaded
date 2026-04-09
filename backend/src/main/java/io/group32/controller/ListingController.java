package io.group32.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.group32.dto.request.listings.CreateListingRequest;
import io.group32.dto.request.listings.UpdateListingRequest;
import io.group32.model.Listing;
import io.group32.service.ListingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
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
    public Page<Listing> getMyListings(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        return listingService.getListingsForCurrentUser(request, page, pageSize);
    }

    @GetMapping("/{id}")
    public Listing getListing(@PathVariable Long id) {
        return listingService.getListingById(id);
    }

    @GetMapping
    public Page<Listing> getListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "newest") String sortBy
    ) {
        return listingService.getListings(search, size, condition, category, minPrice, maxPrice, page, pageSize, sortBy);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateListing(
            @PathVariable Long id,
            @RequestPart("data") String rawJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deleteImages", required = false) String deleteImagesJson,
            HttpServletRequest httpRequest
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UpdateListingRequest request = mapper.readValue(rawJson, UpdateListingRequest.class);

            List<String> deleteImages = null;
            if (deleteImagesJson != null) {
                deleteImages = mapper.readValue(deleteImagesJson, List.class);
            }

            return listingService.handleUpdateListing(id, request, newImages, deleteImages, httpRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }


    @DeleteMapping("/{id}")
    public String deleteListing(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        return listingService.deleteListing(id, httpRequest);
    }
}
