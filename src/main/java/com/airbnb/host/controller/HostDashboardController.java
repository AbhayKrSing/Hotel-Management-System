package com.airbnb.host.controller;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.service.BookingService;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.booking.model.Booking;
import com.airbnb.shared.exceptions.ResourceNotFoundException;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/host")
public class HostDashboardController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public HostDashboardController(BookingService bookingService,
                                   BookingRepository bookingRepository,
                                   UserRepository userRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    private Authentication requireHost() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        boolean isHostOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST") || a.getAuthority().equals("ROLE_ADMIN"));
        if (!isHostOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only hosts can access this dashboard");
        }
        return auth;
    }

    /**
     * GET /api/host/bookings
     * Returns all bookings for all hotels owned by the authenticated host.
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getHostBookings() {
        Authentication auth = requireHost();
        List<BookingDTO> bookings = bookingService.getHostBookings(auth.getName());
        return ResponseEntity.ok(bookings);
    }

    /**
     * PUT /api/host/bookings/{id}/status
     * Host accepts (CONFIRMED) or rejects (CANCELLED) a booking.
     * Body: { "status": "CONFIRMED" }
     */
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable UUID id,
            @RequestParam BookingStatus status) {
        Authentication auth = requireHost();
        BookingDTO updated = bookingService.updateBookingStatus(id, status, auth.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/host/earnings
     * Returns total earnings summary for all the host's confirmed bookings.
     */
    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> getHostEarnings() {
        Authentication auth = requireHost();
        User host = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Host not found"));

        List<Booking> bookings = bookingRepository.findBookingsByHostId(host.getId());

        BigDecimal totalEarnings = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalConfirmed = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        long totalPending = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING)
                .count();

        long totalCancelled = bookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CANCELLED)
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEarnings", totalEarnings);
        summary.put("totalConfirmedBookings", totalConfirmed);
        summary.put("totalPendingBookings", totalPending);
        summary.put("totalCancelledBookings", totalCancelled);
        summary.put("totalBookings", bookings.size());

        return ResponseEntity.ok(summary);
    }
}
