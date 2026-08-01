package com.airbnb.booking.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.airbnb.booking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);
    
    @Query("SELECT b FROM Booking b WHERE b.hotelId IN (SELECT h.id FROM Hotel h WHERE h.hostId = :hostId)")
    List<Booking> findBookingsByHostId(@Param("hostId") UUID hostId);
}
