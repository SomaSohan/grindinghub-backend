package com.example.demo.controller;

import com.example.demo.entity.Favorite;
import com.example.demo.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        if (favoriteRepository.existsByUserIdAndFactoryId(favorite.getUserId(), favorite.getFactoryId())) {
            return ResponseEntity.badRequest().body("Already favorited");
        }
        Favorite saved = favoriteRepository.save(favorite);
        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable int userId) {
        return ResponseEntity.ok(favoriteRepository.findByUserId(userId));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Transactional
    @DeleteMapping("/user/{userId}/factory/{factoryId}")
    public ResponseEntity<?> removeFavorite(@PathVariable int userId, @PathVariable int factoryId) {
        if (favoriteRepository.existsByUserIdAndFactoryId(userId, factoryId)) {
            favoriteRepository.deleteByUserIdAndFactoryId(userId, factoryId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
