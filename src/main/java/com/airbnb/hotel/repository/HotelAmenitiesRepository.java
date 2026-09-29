package com.airbnb.hotel.repository;

import com.airbnb.hotel.model.HotelAmenities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelAmenitiesRepository extends JpaRepository<HotelAmenities, UUID> {
    void deleteByHotelId_Id(UUID hotelId);
}
