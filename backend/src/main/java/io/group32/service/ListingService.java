package io.group32.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.group32.dto.request.listings.CreateListingRequest;
import io.group32.dto.request.listings.UpdateListingRequest;
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

    public record ImageRecord(String url, String publicId) {}

    @Transactional
    public String handleCreateListing(CreateListingRequest request, List<MultipartFile> images, HttpServletRequest httpRequest) {
        User user = sessionService.getUser(httpRequest);

        if (user == null) {
            return "User error";
        }

        Listing listing = getListing(request, user);
        listingRepository.save(listing);

        if (images != null && !images.isEmpty()) {
            List<ImageRecord> imageRecords = uploadImages(images);
            addImagesToListing(listing, imageRecords);
        }

        return "Temporary String (return actual responses in the future)";
    }

    public Listing getListing(CreateListingRequest request, User user) {
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

        return listing;
    }

    public List<ImageRecord> uploadImages(List<MultipartFile> images) {
        List<ImageRecord> imageRecords = new ArrayList<>();

        try {
            for (MultipartFile image : images) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                imageRecords.add(new ImageRecord(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageRecords;
    }

    public void addImagesToListing(Listing listing, List<ImageRecord> imageRecords) {
        for (ImageRecord imageRecord : imageRecords) {
            ListingImage listingImage = new ListingImage();
            listingImage.setListing(listing);
            listingImage.setImageUrl(imageRecord.url());
            listingImage.setPublicId(imageRecord.publicId());
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

    public List<Listing> getListingsForCurrentUser(HttpServletRequest request) {
        User user = sessionService.getUser(request);

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        return listingRepository.findByUser(user);
    }

    @Transactional
    public String handleUpdateListing(Long id, UpdateListingRequest request, List<MultipartFile> newImages, List<String> deleteImages, HttpServletRequest httpRequest) {
        Listing listing = verifyListingOwnership(id, httpRequest);

        updateListingAttributes(listing, request);

        if (deleteImages != null && !deleteImages.isEmpty()) {
            deleteImagesByPublicId(listing, deleteImages);
        }

        if (newImages != null && !newImages.isEmpty()) {
            List<ImageRecord> imageRecords = uploadImages(newImages);
            addImagesToListing(listing, imageRecords);
        }

        listingRepository.save(listing);

        return "Successfully updated listing";
    }

    public Listing verifyListingOwnership(Long id, HttpServletRequest httpRequest) {
        User sessionUser = sessionService.getUser(httpRequest);

        if (sessionUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getUser().getId().equals(sessionUser.getId())) {
            throw new RuntimeException("Unauthorized request");
        }

        return listing;
    }

    public void updateListingAttributes(Listing listing, UpdateListingRequest request) {
        if (request.getTitle() != null) listing.setTitle(request.getTitle());
        if (request.getDescription() != null) listing.setDescription(request.getDescription());
        if (request.getPrice() != null) listing.setPrice(request.getPrice());
        if (request.getBrand() != null) listing.setBrand(request.getBrand());
        if (request.getSize() != null) listing.setSize(request.getSize());
        if (request.getItemCondition() != null) listing.setItemCondition(request.getItemCondition());
        if (request.getCategory() != null) listing.setCategory(request.getCategory());
    }

    public void deleteImagesByPublicId(Listing listing, List<String> publicIds) {
        for (String publicId : publicIds) {
            ListingImage listingImage = listingImageRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new RuntimeException("Image not found: " + publicId));

            if (!listingImage.getListing().getId().equals(listing.getId())) {
                throw new RuntimeException("Image does not belong to this listing");
            }

            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image from Cloudinary", e);
            }

            listingImageRepository.delete(listingImage);
        }
    }

    @Transactional
    public String deleteListing(Long id, HttpServletRequest httpRequest) {
        Listing listing = verifyListingOwnership(id, httpRequest);

        for (ListingImage image : listing.getImages()) {
            try {
                cloudinary.uploader().destroy(image.getPublicId(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image: ", e);
            }
        }

        listingRepository.delete(listing);

        return "Successfully deleted listing";
    }
}
