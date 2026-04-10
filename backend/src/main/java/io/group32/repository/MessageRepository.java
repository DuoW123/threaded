package io.group32.repository;

import io.group32.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByListingIdAndSenderIdAndReceiverIdOrderByTimestampAsc(Long listingId, Long senderId, Long receiverId);
    List<Message> findByListingIdAndReceiverIdAndSenderIdOrderByTimestampAsc(Long listingId, Long receiverId, Long senderId);
    List<Message> findBySenderIdOrReceiverIdOrderByTimestampDesc(Long senderId, Long receiverId);
}
