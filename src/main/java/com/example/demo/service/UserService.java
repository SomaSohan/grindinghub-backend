package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // OTP storage and expiry tracking
    private Map<String, String> otpStorage = new HashMap<>();
    private Map<String, Long> otpExpiryStorage = new HashMap<>();

    // Register User
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        // FACTORY → send OTP to email
        if (user.getRole().name().equals("FACTORY")) {

            String otp = String.valueOf(new Random().nextInt(900000) + 100000);

            otpStorage.put(user.getEmail(), otp);

            // 5 minutes expiry
            otpExpiryStorage.put(user.getEmail(), System.currentTimeMillis() + (5 * 60 * 1000));

            // Send email OTP
            emailService.sendOtpEmail(user.getEmail(), otp);

            throw new RuntimeException("Factory registered. OTP sent to email.");
        }

        return savedUser;
    }

    // Login User
    public User loginUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        if (user.getRole().name().equals("FACTORY")
                && (user.getLicenseNumber() == null || user.getLicenseNumber().trim().isEmpty())) {
            throw new RuntimeException("Factory not verified. Please verify OTP first.");
        }

        return user;
    }

    // Verify Factory OTP + License
    public String verifyFactory(String email, String otp, String licenseNumber) {

        String storedOtp = otpStorage.get(email);
        Long expiryTime = otpExpiryStorage.get(email);

        if (storedOtp == null) {
            throw new RuntimeException("No OTP found. Please register first.");
        }

        // Check expiry
        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.remove(email);
            otpExpiryStorage.remove(email);
            throw new RuntimeException("OTP expired. Please register again.");
        }

        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        String gstRegex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}Z[0-9A-Z]{1}$";
        if (!licenseNumber.matches(gstRegex)) {
            throw new RuntimeException("Invalid License Format");
        }

        if (userRepository.existsByLicenseNumber(licenseNumber)) {
            throw new RuntimeException("This License Number is already registered by another factory.");
        }

        // Save license number permanently to the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        user.setLicenseNumber(licenseNumber);
        userRepository.save(user);

        otpStorage.remove(email);
        otpExpiryStorage.remove(email);

        return "Factory verified successfully. You can login now.";
    }

    // CRUD methods
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> updateUser(int id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            return Optional.empty();

        User existingUser = optionalUser.get();
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setRole(updatedUser.getRole());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        userRepository.save(existingUser);
        return Optional.of(existingUser);
    }

    public boolean deleteUser(int id) {
        if (!userRepository.existsById(id))
            return false;
        userRepository.deleteById(id);
        return true;
    }
}