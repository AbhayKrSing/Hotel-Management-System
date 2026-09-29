package com.airbnb.hotel.repository;

import com.airbnb.hotel.model.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, UUID> {
    Optional<Amenities> findByAmenity(String amenity);
}
