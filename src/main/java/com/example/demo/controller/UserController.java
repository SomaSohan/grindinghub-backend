package com.example.demo.controller;

import com.example.demo.config.JwtUtil;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.service.FactoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        try {
            User savedUser = userService.registerUser(user);

            // For clients, return user normally
            return ResponseEntity.ok(savedUser);

        } catch (RuntimeException e) {

            // Factory case: check message contains "OTP sent"
            if (e.getMessage().contains("OTP sent")) {
                // Send a proper response instead of throwing
                Map<String, String> resp = new HashMap<>();
                resp.put("message", e.getMessage());
                resp.put("next", "verify"); // frontend knows to go to verify.html
                return ResponseEntity.ok(resp);
            }

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        try {

            User user = userService.loginUser(
                    request.getEmail(),
                    request.getPassword());

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name());

            return ResponseEntity.ok(
                    new LoginResponse(token, user.getRole().name(), user.getUserId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {

        Optional<User> user = userService.getUserById(id);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACTORY', 'CLIENT')")
    @GetMapping("/contact/{id}")
    public ResponseEntity<Map<String, String>> getUserContact(@PathVariable int id) {

        factoryService.incrementContactClicksByUserId(id);

        return userService.getUserById(id).map(user -> {
            Map<String, String> contact = new HashMap<>();
            contact.put("name", user.getName());
            contact.put("email", user.getEmail());
            contact.put("phone", user.getPhone());
            return ResponseEntity.ok(contact);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id,
            @RequestBody User updatedUser) {

        Optional<User> user = userService.updateUser(id, updatedUser);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {

        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyFactory(@RequestBody Map<String, String> request) {

        try {
            String email = request.get("email");
            String otp = request.get("otp");
            String licenseNumber = request.get("licenseNumber");

            String response = userService.verifyFactory(email, otp, licenseNumber);

            return ResponseEntity.ok(Map.of(
                    "message", response,
                    "next", "login"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage()));
        }
    }

}