package io.group32.controller;

import io.group32.model.Message;
import io.group32.model.User;
import io.group32.model.Listing;
import io.group32.model.ListingImage;
import io.group32.repository.MessageRepository;
import io.group32.repository.UserRepository;
import io.group32.repository.ListingRepository;
import io.group32.repository.ListingImageRepository;
import io.group32.dto.ConversationDTO;
import io.group32.dto.ConversationListingDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ListingImageRepository listingImageRepository;

    private User getCurrentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) return null;
        return userRepository.findById((Long) id).orElse(null);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody Message incoming, HttpSession session) {
        User sender = getCurrentUser(session);
        if (sender == null) return;

        Message m = new Message();
        m.setSenderId(sender.getId());
        m.setReceiverId(incoming.getReceiverId());
        m.setListingId(incoming.getListingId());
        m.setContent(incoming.getContent());
        m.setTimestamp(LocalDateTime.now());
        messageRepository.save(m);
    }

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long seller, @RequestParam Long listing, HttpSession session) {
        User current = getCurrentUser(session);
        if (current == null) return new ArrayList<>();
        Long currentId = current.getId();

        List<Message> a = messageRepository.findByListingIdAndSenderIdAndReceiverIdOrderByTimestampAsc(listing, currentId, seller);
        List<Message> b = messageRepository.findByListingIdAndReceiverIdAndSenderIdOrderByTimestampAsc(listing, currentId, seller);

        List<Message> all = new ArrayList<>();
        all.addAll(a);
        all.addAll(b);
        all.sort((x, y) -> x.getTimestamp().compareTo(y.getTimestamp()));

        for (Message m : all) {
            User sender = userRepository.findById(m.getSenderId()).orElse(null);
            User receiver = userRepository.findById(m.getReceiverId()).orElse(null);
            if (sender != null) m.setSenderUsername(sender.getUsername());
            if (receiver != null) m.setReceiverUsername(receiver.getUsername());
            m.setMine(m.getSenderId().equals(currentId));
        }

        return all;
    }

    @GetMapping("/conversations")
    public List<ConversationDTO> getConversations(HttpSession session) {
        User current = getCurrentUser(session);
        if (current == null) return new ArrayList<>();
        Long currentId = current.getId();

        List<Message> messages = messageRepository.findBySenderIdOrReceiverIdOrderByTimestampDesc(currentId, currentId);

        Map<Long, ConversationDTO> byUser = new LinkedHashMap<>();

        for (Message m : messages) {
            Long otherUserId = m.getSenderId().equals(currentId) ? m.getReceiverId() : m.getSenderId();
            ConversationDTO convo = byUser.get(otherUserId);
            if (convo == null) {
                User other = userRepository.findById(otherUserId).orElse(null);
                if (other == null) continue;
                convo = new ConversationDTO();
                convo.setUserId(otherUserId);
                convo.setUsername(other.getUsername());
                convo.setListings(new ArrayList<>());
                byUser.put(otherUserId, convo);
            }

            List<ConversationListingDTO> listings = convo.getListings();
            ConversationListingDTO listingDto = null;
            for (ConversationListingDTO l : listings) {
                if (l.getListingId().equals(m.getListingId())) {
                    listingDto = l;
                    break;
                }
            }

            if (listingDto == null) {
                listingDto = new ConversationListingDTO();
                listingDto.setListingId(m.getListingId());

                Listing listing = listingRepository.findById(m.getListingId()).orElse(null);
                if (listing != null) {
                    listingDto.setListingTitle(listing.getTitle());
                    List<ListingImage> images = listingImageRepository.findByListing(listing);
                    if (!images.isEmpty()) {
                        listingDto.setThumbnailUrl(images.get(0).getImageUrl());
                    }
                }

                listingDto.setLastMessage(m.getContent());
                listingDto.setTimestamp(m.getTimestamp());
                listings.add(listingDto);
            }
        }

        return new ArrayList<>(byUser.values());
    }

    @DeleteMapping("/conversation")
    public void deleteConversation(@RequestParam Long seller, @RequestParam Long listing, HttpSession session) {
        User current = getCurrentUser(session);
        if (current == null) return;
        Long currentId = current.getId();

        List<Message> a = messageRepository.findByListingIdAndSenderIdAndReceiverIdOrderByTimestampAsc(listing, currentId, seller);
        List<Message> b = messageRepository.findByListingIdAndReceiverIdAndSenderIdOrderByTimestampAsc(listing, currentId, seller);

        List<Message> all = new ArrayList<>();
        all.addAll(a);
        all.addAll(b);

        if (!all.isEmpty()) {
            messageRepository.deleteAll(all);
        }
    }
}
