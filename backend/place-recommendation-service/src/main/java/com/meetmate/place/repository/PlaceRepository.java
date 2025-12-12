package com.meetmate.place.repository;

import com.meetmate.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {

    @Query("""
        SELECT p FROM Place p
        WHERE p.isActive = true
          AND (:category IS NULL OR p.category = :category)
          AND (:area IS NULL OR p.area = :area)
          AND (:costLevel IS NULL OR p.costLevel = :costLevel)
        """)
    List<Place> searchActive(@Param("category") String category,
                             @Param("area") String area,
                             @Param("costLevel") String costLevel);
}

