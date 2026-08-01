package com.airbnb.message.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDTO {
    private UUID id;
    private UUID bookingId;
    private UUID hotelId;
    private UUID userId;
    private String reviewMessage;
    private String hostResponse;
    private Integer rating;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public UUID getHotelId() { return hotelId; }
    public void setHotelId(UUID hotelId) { this.hotelId = hotelId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getReviewMessage() { return reviewMessage; }
    public void setReviewMessage(String reviewMessage) { this.reviewMessage = reviewMessage; }

    public String getHostResponse() { return hostResponse; }
    public void setHostResponse(String hostResponse) { this.hostResponse = hostResponse; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
