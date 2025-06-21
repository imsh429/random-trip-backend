package com.randomtrip.backend.repository;

import com.randomtrip.backend.entity.ConfirmedPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmedPlaceRepository extends JpaRepository<ConfirmedPlace, Long> {
}
