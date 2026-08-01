package com.airbnb.message.controller;

import com.airbnb.message.dto.ReviewDTO;
import com.airbnb.message.service.ReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final ReviewService reviewService;

    public MessageController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * POST /api/reviews
     * Guest submits a review for a completed booking.
     * Body: { "bookingId": "...", "reviewMessage": "...", "rating": 4 }
     */
    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        ReviewDTO saved = reviewService.createReview(reviewDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * GET /api/hotels/{id}/reviews
     * Public endpoint — view all reviews for a hotel.
     */
    @GetMapping("/hotels/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getHotelReviews(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getHotelReviews(id));
    }

    /**
     * POST /api/reviews/{id}/response
     * Host replies to a guest review.
     * Body: { "response": "Thank you for staying with us!" }
     */
    @PostMapping("/reviews/{id}/response")
    public ResponseEntity<ReviewDTO> addHostResponse(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String response = body.get("response");
        if (response == null || response.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response text is required");
        }
        ReviewDTO updated = reviewService.addHostResponse(id, response, auth.getName());
        return ResponseEntity.ok(updated);
    }
}
