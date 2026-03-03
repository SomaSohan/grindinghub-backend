package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Factory;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FactoryRepository extends JpaRepository<Factory, Integer> {

    List<Factory> findByUserId(int userId);

    List<Factory> findByCity(String city);

    List<Factory> findByState(String state);

    @Transactional
    @Modifying
    @Query("UPDATE Factory f SET f.searchAppearances = f.searchAppearances + 1 WHERE f.factoryId IN :factoryIds")
    void incrementSearchAppearances(@Param("factoryIds") List<Integer> factoryIds);

    @Transactional
    @Modifying
    @Query("UPDATE Factory f SET f.contactClicks = f.contactClicks + 1 WHERE f.userId = :userId")
    void incrementContactClicksByUserId(@Param("userId") int userId);
}