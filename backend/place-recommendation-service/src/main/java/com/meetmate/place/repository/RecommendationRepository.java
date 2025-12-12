package com.meetmate.place.repository;

import com.meetmate.place.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
}

