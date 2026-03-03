package com.example.demo.repository;

import com.example.demo.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserId(int userId);

    Optional<Favorite> findByUserIdAndFactoryId(int userId, int factoryId);

    boolean existsByUserIdAndFactoryId(int userId, int factoryId);

    void deleteByUserIdAndFactoryId(int userId, int factoryId);

    int countByFactoryId(int factoryId);
}
