package io.group32.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConversationDTO {
    private Long userId;
    private String username;
    private List<ConversationListingDTO> listings;
}
