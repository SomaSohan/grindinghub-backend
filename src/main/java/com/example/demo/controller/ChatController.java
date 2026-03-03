package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyRole('FACTORY', 'CLIENT')")
    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        ChatMessage savedMsg = chatMessageRepository.save(message);
        return ResponseEntity.ok(savedMsg);
    }

    @PreAuthorize("hasAnyRole('FACTORY', 'CLIENT')")
    @GetMapping("/conversation/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable int user1,
            @PathVariable int user2) {
        List<ChatMessage> conversation = chatMessageRepository.findConversation(user1, user2);
        return ResponseEntity.ok(conversation);
    }

    @PreAuthorize("hasAnyRole('FACTORY', 'CLIENT')")
    @GetMapping("/contacts/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getChatContacts(@PathVariable int userId) {
        List<Integer> contactIds = chatMessageRepository.findChattedUserIds(userId);

        List<Map<String, Object>> contacts = new ArrayList<>();
        for (Integer contactId : contactIds) {
            Optional<User> uOpt = userRepository.findById(contactId);
            if (uOpt.isPresent()) {
                User u = uOpt.get();
                Map<String, Object> map = new HashMap<>();
                map.put("userId", u.getUserId());
                map.put("name", u.getName());
                map.put("role", u.getRole().name());
                contacts.add(map);
            }
        }
        return ResponseEntity.ok(contacts);
    }
}
