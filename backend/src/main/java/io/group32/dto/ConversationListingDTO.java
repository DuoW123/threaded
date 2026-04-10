package io.group32.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ConversationListingDTO {
    private Long listingId;
    private String listingTitle;
    private String thumbnailUrl;
    private String lastMessage;
    private LocalDateTime timestamp;
}
