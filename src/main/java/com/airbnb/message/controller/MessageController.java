package com.airbnb.message.controller;

import com.airbnb.message.dto.ReviewDTO;
import com.airbnb.message.service.ReviewService;
import com.airbnb.shared.dto.ApiResponse;
import com.airbnb.shared.exceptions.UnauthorizedException;

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
@RequestMapping("/api/${api.version}")
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
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@RequestBody ReviewDTO reviewDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        ReviewDTO saved = reviewService.createReview(reviewDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Review submitted successfully", saved));
    }

    /**
     * GET /api/hotels/{id}/reviews
     * Public endpoint — view all reviews for a hotel.
     */
    @GetMapping("/hotels/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getHotelReviews(@PathVariable UUID id) {
        List<ReviewDTO> reviews = reviewService.getHotelReviews(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reviews fetched successfully", reviews));
    }

    /**
     * POST /api/reviews/{id}/response
     * Host replies to a guest review.
     * Body: { "response": "Thank you for staying with us!" }
     */
    @PostMapping("/reviews/{id}/response")
    public ResponseEntity<ApiResponse<ReviewDTO>> addHostResponse(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        String response = body.get("response");
        if (response == null || response.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response text is required");
        }
        ReviewDTO updated = reviewService.addHostResponse(id, response, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Host response added successfully", updated));
    }
}
