package com.airbnb.message.repository;

import com.airbnb.message.model.GuestReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestReviewRepository extends JpaRepository<GuestReview, UUID> {
    List<GuestReview> findByHotelId(UUID hotelId);
    List<GuestReview> findByUserId(UUID userId);
    Optional<GuestReview> findByBookingId(UUID bookingId);
}
