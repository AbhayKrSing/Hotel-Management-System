package com.airbnb.booking.controller;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.service.BookingService;
import com.airbnb.shared.dto.ApiResponse;
import com.airbnb.shared.exceptions.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@RequestBody BookingDTO bookingDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        BookingDTO created = bookingService.createBooking(bookingDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Booking created successfully", created));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getMyBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        List<BookingDTO> bookings = bookingService.getMyBookings(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Bookings fetched successfully", bookings));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        BookingDTO cancelled = bookingService.cancelBooking(id, auth.getName());
        return ResponseEntity
                .ok(ApiResponse.success(HttpStatus.OK.value(), "Booking cancelled successfully", cancelled));
    }
}
