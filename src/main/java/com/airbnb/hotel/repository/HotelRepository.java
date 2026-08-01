package com.airbnb.hotel.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airbnb.hotel.enums.HotelStatus;
import com.airbnb.hotel.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    
    Optional<Hotel> findById(UUID id);
    // Find hotels by host ID
    List<Hotel> findByHostId(UUID hostId);

    // Find hotels by host ID and status
    List<Hotel> findByHostIdAndStatus(UUID hostId, HotelStatus status);

    // Find hotels by city (for guest search)
    List<Hotel> findByCityContainingIgnoreCase(String city);

    // Find hotels by country
    List<Hotel> findByCountryContainingIgnoreCase(String country);

    // Find active hotels by city
    List<Hotel> findByCityAndStatus(String city, HotelStatus status);

    // Check if hotel exists and belongs to host
    boolean existsByIdAndHostId(UUID id, UUID hostId);

    // Find hotel with validation
    Optional<Hotel> findByIdAndHostId(UUID id, UUID hostId);

    // Search hotels by name or city
    @Query("SELECT h FROM Hotel h WHERE " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(h.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Hotel> searchHotels(@Param("keyword") String keyword);
}