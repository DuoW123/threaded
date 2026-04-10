package io.group32.dto;

import java.util.List;

public class ConversationDTO {

    private Long userId;
    private String username;
    private List<ConversationListingDTO> listings;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ConversationListingDTO> getListings() {
        return listings;
    }

    public void setListings(List<ConversationListingDTO> listings) {
        this.listings = listings;
    }
}
