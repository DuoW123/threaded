package io.group32.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.group32.dto.request.listings.CreateListingRequest;
import io.group32.model.Listing;
import io.group32.model.ListingImage;
import io.group32.model.ListingStatus;
import io.group32.model.User;
import io.group32.repository.ListingImageRepository;
import io.group32.repository.ListingRepository;
import io.group32.repository.specification.ListingSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ListingService {
    private final Cloudinary cloudinary;
    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final SessionService sessionService;

    public ListingService(Cloudinary cloudinary, ListingRepository listingRepository,
                          ListingImageRepository listingImageRepository, SessionService sessionService) {

        this.cloudinary = cloudinary;
        this.listingRepository = listingRepository;
        this.listingImageRepository = listingImageRepository;
        this.sessionService = sessionService;
    }

    @Transactional
    public String handleCreateListing(CreateListingRequest request, List<MultipartFile> images, HttpServletRequest httpRequest) {
        User user = sessionService.getUser(httpRequest);

        if (user == null) {
            return "User error";
        }

        List<String> imageUrls = new ArrayList<>();

        try {
            for (MultipartFile image : images) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                imageUrls.add(uploadResult.get("url").toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Listing listing = new Listing();
        listing.setUser(user);
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setBrand(request.getBrand());
        listing.setSize(request.getSize());
        listing.setItemCondition(request.getItemCondition());
        listing.setCategory(request.getCategory());
        listing.setListingStatus(ListingStatus.OPEN);

        listingRepository.save(listing);

        addImagesToListing(listing, imageUrls);

        //TODO
        return "Temporary String (return actual responses in the future)";
    }

    @Transactional
    public void addImagesToListing(Listing listing, List<String> imageUrls) {
        for (String url : imageUrls) {
            ListingImage listingImage = new ListingImage();
            listingImage.setListing(listing);
            listingImage.setImageUrl(url);
            listingImageRepository.save(listingImage);
        }
    }

    public Listing getListingById(Long id) {
        Optional<Listing> optionalListing = listingRepository.findById(id);

        if (optionalListing.isEmpty()) {
            throw new RuntimeException("Listing not found with id: " + id.toString());
        }

        return optionalListing.get();
    }

    public List<Listing> getListings(String search, String size, String condition, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Listing> specification = Specification.where((listing, query, builder) -> builder.conjunction());

        if (search != null && !search.isBlank()) {
            specification = specification.and(ListingSpecifications.fuzzySearch(search));
        }

        if (size != null && !size.isBlank()) {
            specification = specification.and(ListingSpecifications.hasSize(size));
        }

        if (condition != null && !condition.isBlank()) {
            specification = specification.and(ListingSpecifications.hasCondition(condition));
        }

        if (category != null && !category.isBlank()) {
            specification = specification.and(ListingSpecifications.hasCategory(category));
        }

        specification = specification.and(ListingSpecifications.priceBetween(minPrice, maxPrice));

        return listingRepository.findAll(specification);
    }
}
