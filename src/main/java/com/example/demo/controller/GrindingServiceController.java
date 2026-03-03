package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.entity.GrindingService;
import com.example.demo.service.GrindingServiceService;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.service.FactoryService;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/grinding-services")
public class GrindingServiceController {

    @Autowired
    private GrindingServiceService grindingServiceService;

    @Autowired
    private FactoryService factoryService;

    // 🔐 Only FACTORY can create
    @PreAuthorize("hasRole('FACTORY')")
    @PostMapping("/{factoryId}")
    public ResponseEntity<?> createService(
            @PathVariable int factoryId,
            @RequestBody GrindingService grindingService) {

        GrindingService savedService = grindingServiceService.createGrindingService(factoryId, grindingService);

        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    // 👀 FACTORY & CLIENT can view all
    @PreAuthorize("hasAnyRole('FACTORY','CLIENT')")
    @GetMapping
    public ResponseEntity<List<GrindingService>> getAllServices() {

        List<GrindingService> services = grindingServiceService.getAllServices();

        List<Integer> factoryIds = services.stream()
                .map(s -> s.getFactory().getFactoryId())
                .distinct()
                .collect(Collectors.toList());
        factoryService.incrementSearchAppearances(factoryIds);

        return ResponseEntity.ok(services);
    }

    // 👀 View by Factory
    @PreAuthorize("hasAnyRole('FACTORY','CLIENT')")
    @GetMapping("/factory/{factoryId}")
    public ResponseEntity<List<GrindingService>> getByFactory(@PathVariable int factoryId) {

        List<GrindingService> services = grindingServiceService.getServicesByFactory(factoryId);

        return ResponseEntity.ok(services);
    }

    // 🔎 Search by Material
    @PreAuthorize("hasAnyRole('FACTORY','CLIENT')")
    @GetMapping("/material/{materialName}")
    public ResponseEntity<List<GrindingService>> getByMaterial(@PathVariable String materialName) {

        List<GrindingService> services = grindingServiceService.getByMaterial(materialName);

        List<Integer> factoryIds = services.stream()
                .map(s -> s.getFactory().getFactoryId())
                .distinct()
                .collect(Collectors.toList());
        factoryService.incrementSearchAppearances(factoryIds);

        return ResponseEntity.ok(services);
    }

    // 🔎 Search by Machine
    @PreAuthorize("hasAnyRole('FACTORY','CLIENT')")
    @GetMapping("/machine/{machineName}")
    public ResponseEntity<List<GrindingService>> getByMachine(@PathVariable String machineName) {

        List<GrindingService> services = grindingServiceService.getByMachine(machineName);

        List<Integer> factoryIds = services.stream()
                .map(s -> s.getFactory().getFactoryId())
                .distinct()
                .collect(Collectors.toList());
        factoryService.incrementSearchAppearances(factoryIds);

        return ResponseEntity.ok(services);
    }

    // ❌ Delete Service
    @PreAuthorize("hasRole('FACTORY')")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable int serviceId) {

        grindingServiceService.deleteService(serviceId);

        return ResponseEntity.ok("Service deleted successfully");
    }
}