package io.group32.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private Long listingId;

    private String content;

    private LocalDateTime timestamp;

    @Transient
    private String senderUsername;

    @Transient
    private String receiverUsername;

    @Transient
    private boolean mine;
}
