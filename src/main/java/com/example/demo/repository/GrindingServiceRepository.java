package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.GrindingService;

import java.util.List;

public interface GrindingServiceRepository extends JpaRepository<GrindingService, Integer> {

    List<GrindingService> findByFactory_FactoryId(int factoryId);

    List<GrindingService> findByMaterialName(String materialName);

    List<GrindingService> findByMachineName(String machineName);
}