package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Factory;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.FactoryRepository;
import com.example.demo.repository.WorkOrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private FactoryRepository factoryRepository;

    @PreAuthorize("hasAnyRole('FACTORY', 'CLIENT')")
    @GetMapping("/{userId}/{role}")
    public ResponseEntity<Map<String, Long>> getUnreadCounts(@PathVariable int userId, @PathVariable String role) {
        long unreadChats = chatMessageRepository.countByReceiverIdAndIsReadFalse(userId);
        long unreadOrders = 0;

        if ("CLIENT".equalsIgnoreCase(role)) {
            // Clients get notified if order status changes (if we want to build that later)
            // For now, let's just count pending changes or we can just leave it 0
        } else if ("FACTORY".equalsIgnoreCase(role)) {
            // Factory gets notified of new WorkOrders
            List<Factory> factories = factoryRepository.findByUserId(userId);
            if (!factories.isEmpty()) {
                unreadOrders = workOrderRepository
                        .countByFactory_FactoryIdAndIsReadFalse(factories.get(0).getFactoryId());
            }
        }

        Map<String, Long> response = new HashMap<>();
        response.put("chats", unreadChats);
        response.put("orders", unreadOrders);
        response.put("total", unreadChats + unreadOrders);

        return ResponseEntity.ok(response);
    }
}
