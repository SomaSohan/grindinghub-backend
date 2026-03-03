package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.GrindingService;
import com.example.demo.entity.Factory;
import com.example.demo.repository.GrindingServiceRepository;
import com.example.demo.repository.FactoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GrindingServiceService {

    @Autowired
    private GrindingServiceRepository grindingServiceRepository;

    @Autowired
    private FactoryRepository factoryRepository;

    // Create Grinding Service
    public GrindingService createGrindingService(int factoryId, GrindingService grindingService) {

        Optional<Factory> optionalFactory = factoryRepository.findById(factoryId);

        if (optionalFactory.isEmpty()) {
            throw new RuntimeException("Factory not found");
        }

        grindingService.setFactory(optionalFactory.get());

        return grindingServiceRepository.save(grindingService);
    }

    public List<GrindingService> getAllServices() {
        return grindingServiceRepository.findAll();
    }

    public List<GrindingService> getServicesByFactory(int factoryId) {
        return grindingServiceRepository.findByFactory_FactoryId(factoryId);
    }

    public List<GrindingService> getByMaterial(String materialName) {
        return grindingServiceRepository.findByMaterialName(materialName);
    }

    public List<GrindingService> getByMachine(String machineName) {
        return grindingServiceRepository.findByMachineName(machineName);
    }

    public void deleteService(int serviceId) {
        grindingServiceRepository.deleteById(serviceId);
    }
}