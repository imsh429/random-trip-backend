package com.randomtrip.backend.repository;

import com.randomtrip.backend.entity.ConfirmedPlace;
import com.randomtrip.backend.entity.Trip;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmedPlaceRepository extends JpaRepository<ConfirmedPlace, Long> {

    @Query("SELECT cp FROM ConfirmedPlace cp WHERE cp.trip.userId = :userId ORDER BY cp.trip.createdAt DESC")
    List<ConfirmedPlace> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

}
