package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.entity.Factory;
import com.example.demo.service.FactoryService;

import com.example.demo.repository.FactoryRepository;
import com.example.demo.repository.FavoriteRepository;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/factories")
public class FactoryController {

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private FactoryRepository factoryRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @PreAuthorize("hasRole('FACTORY')")
    @PostMapping
    public ResponseEntity<?> createFactory(@RequestBody Factory factory) {
        Factory savedFactory = factoryService.createFactory(factory);
        return new ResponseEntity<>(savedFactory, HttpStatus.CREATED);
    }

    // 👀 ADMIN, FACTORY & CLIENT can view all
    @PreAuthorize("hasAnyRole('ADMIN','FACTORY','CLIENT')")
    @GetMapping
    public ResponseEntity<List<Factory>> getAllFactories() {

        List<Factory> factories = factoryService.getAllFactories();

        return ResponseEntity.ok(factories);
    }

    // 👀 View by user
    @PreAuthorize("hasAnyRole('ADMIN','FACTORY','CLIENT')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Factory>> getByUser(@PathVariable int userId) {

        List<Factory> factories = factoryService.getFactoriesByUserId(userId);

        return ResponseEntity.ok(factories);
    }

    // 🔎 Search by city
    @PreAuthorize("hasAnyRole('ADMIN','FACTORY','CLIENT')")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Factory>> getByCity(@PathVariable String city) {

        List<Factory> factories = factoryService.getFactoriesByCity(city);

        return ResponseEntity.ok(factories);
    }

    // 🔎 Search by state
    @PreAuthorize("hasAnyRole('ADMIN','FACTORY','CLIENT')")
    @GetMapping("/state/{state}")
    public ResponseEntity<List<Factory>> getByState(@PathVariable String state) {

        List<Factory> factories = factoryService.getFactoriesByState(state);

        return ResponseEntity.ok(factories);
    }

    // 📊 Analytics Endpoint
    @PreAuthorize("hasRole('FACTORY')")
    @GetMapping("/{factoryId}/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(@PathVariable int factoryId) {
        Optional<Factory> factoryOpt = factoryRepository.findById(factoryId);

        if (factoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Factory factory = factoryOpt.get();
        int favoritesCount = favoriteRepository.countByFactoryId(factoryId);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("searchAppearances", factory.getSearchAppearances());
        analytics.put("contactClicks", factory.getContactClicks());
        analytics.put("favoritesCount", favoritesCount);

        return ResponseEntity.ok(analytics);
    }
}