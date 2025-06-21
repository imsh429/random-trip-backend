package com.randomtrip.backend.repository;

import com.randomtrip.backend.entity.TripRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRouteRepository extends JpaRepository<TripRoute, Long> {
}
