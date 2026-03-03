package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Factory;
import com.example.demo.entity.User;
import com.example.demo.repository.FactoryRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class FactoryService {

    @Autowired
    private FactoryRepository factoryRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔐 Create Factory with Auto UserId
    public Factory createFactory(Factory factory) {

        // Get logged-in user email from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Fetch user using Optional
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // Auto-assign userId
        factory.setUserId(user.getUserId());

        return factoryRepository.save(factory);
    }

    public List<Factory> getAllFactories() {
        return factoryRepository.findAll();
    }

    public List<Factory> getFactoriesByUserId(int userId) {
        return factoryRepository.findByUserId(userId);
    }

    public List<Factory> getFactoriesByCity(String city) {
        return factoryRepository.findByCity(city);
    }

    public List<Factory> getFactoriesByState(String state) {
        return factoryRepository.findByState(state);
    }

    public void incrementSearchAppearances(List<Integer> factoryIds) {
        if (factoryIds != null && !factoryIds.isEmpty()) {
            factoryRepository.incrementSearchAppearances(factoryIds);
        }
    }

    public void incrementContactClicksByUserId(int userId) {
        factoryRepository.incrementContactClicksByUserId(userId);
    }
}