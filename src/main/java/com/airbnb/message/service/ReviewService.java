package com.airbnb.message.service;

import com.airbnb.message.dto.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO, String guestEmail);
    List<ReviewDTO> getHotelReviews(UUID hotelId);
    ReviewDTO addHostResponse(UUID reviewId, String response, String hostEmail);
}
