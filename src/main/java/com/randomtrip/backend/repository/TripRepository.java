package com.randomtrip.backend.repository;

import com.randomtrip.backend.entity.Trip;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
