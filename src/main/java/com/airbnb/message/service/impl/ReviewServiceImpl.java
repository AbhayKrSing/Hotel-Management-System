package com.airbnb.message.service.impl;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.message.dto.ReviewDTO;
import com.airbnb.message.model.GuestReview;
import com.airbnb.message.repository.GuestReviewRepository;
import com.airbnb.message.service.ReviewService;
import com.airbnb.shared.exceptions.ResourceNotFoundException;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final GuestReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(GuestReviewRepository reviewRepository,
                             BookingRepository bookingRepository,
                             HotelRepository hotelRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO dto, String guestEmail) {
        User guest = userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + dto.getBookingId()));

        // Only the guest who booked can write the review
        if (!booking.getUserId().equals(guest.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only review your own bookings");
        }

        // Can only review CONFIRMED (completed) bookings
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can only review completed (confirmed) bookings");
        }

        // One review per booking
        reviewRepository.findByBookingId(booking.getId()).ifPresent(r -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A review already exists for this booking");
        });

        UUID hotelId = booking.getHotelId();

       GuestReview review = new GuestReview();
//        review.setBooking(booking);
//        review.setHotel(hotel);
//        review.setUser(guest);
//        review.setReviewMessage(dto.getReviewMessage());
//        review.setRating(dto.getRating());

        GuestReview saved = reviewRepository.save(review);
        return convertToDTO(saved);
    }

    @Override
    public List<ReviewDTO> getHotelReviews(UUID hotelId) {
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
        return reviewRepository.findByHotelId(hotelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO addHostResponse(UUID reviewId, String response, String hostEmail) {
        User host = userRepository.findByEmail(hostEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Host not found"));

        GuestReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));

        // Verify the responder is the hotel owner
//        if (!review.getHotel().getHost().getId().equals(host.getId())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
//                    "Only the hotel owner can respond to this review");
//        }

        review.setHostResponse(response);
        GuestReview saved = reviewRepository.save(review);
        return convertToDTO(saved);
    }

    private ReviewDTO convertToDTO(GuestReview review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setHotelId(review.getHotelId());
        dto.setUserId(review.getUserId());
        if (review.getBookingId() != null) {
            dto.setBookingId(review.getBookingId());
        }
        dto.setReviewMessage(review.getReviewMessage());
        dto.setHostResponse(review.getHostResponse());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
