package com.airbnb.admin.controller;

import com.airbnb.admin.service.AdminService;
import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.user.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private void requireAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    /**
     * GET /api/admin/users
     * List all registered users on the platform.
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        requireAdmin();
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * PUT /api/admin/users/{id}/status?enabled=false
     * Enable or disable (suspend/ban) a user account.
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Void> setUserStatus(@PathVariable UUID id,
                                               @RequestParam boolean enabled) {
        requireAdmin();
        adminService.setUserStatus(id, enabled);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/admin/bookings
     * View all bookings across the entire platform.
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        requireAdmin();
        return ResponseEntity.ok(adminService.getAllBookings());
    }

    /**
     * PUT /api/admin/bookings/{id}/cancel
     * Force-cancel a booking (for dispute resolution).
     */
    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<BookingDTO> forceCancelBooking(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        requireAdmin();
        BookingDTO result = adminService.forceCancelBooking(id, auth.getName());
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/analytics
     * Platform-level analytics dashboard.
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        requireAdmin();
        return ResponseEntity.ok(adminService.getPlatformAnalytics());
    }

    /**
     * PUT /api/admin/commission?rate=12.5
     * Update the global platform commission rate (percentage).
     */
    @PutMapping("/commission")
    public ResponseEntity<Void> updateCommission(@RequestParam BigDecimal rate) {
        requireAdmin();
        adminService.updateCommission(rate);
        return ResponseEntity.ok().build();
    }
}
