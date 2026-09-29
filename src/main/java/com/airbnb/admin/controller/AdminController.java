package com.airbnb.admin.controller;

import com.airbnb.admin.service.AdminService;
import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.shared.dto.ApiResponse;
import com.airbnb.shared.exceptions.ForbiddenException;
import com.airbnb.shared.exceptions.UnauthorizedException;
import com.airbnb.user.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    private void requireAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new ForbiddenException("Admin access required");
        }
    }

    /**
     * GET /api/admin/users
     * List all registered users on the platform.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        requireAdmin();
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Users fetched successfully", users));
    }

    /**
     * PUT /api/admin/users/{id}/status?enabled=false
     * Enable or disable (suspend/ban) a user account.
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<Void>> setUserStatus(@PathVariable UUID id,
                                               @RequestParam boolean enabled) {
        requireAdmin();
        adminService.setUserStatus(id, enabled);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "User status updated successfully", null));
    }

    /**
     * GET /api/admin/bookings
     * View all bookings across the entire platform.
     */
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings() {
        requireAdmin();
        List<BookingDTO> bookings = adminService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Bookings fetched successfully", bookings));
    }

    /**
     * PUT /api/admin/bookings/{id}/cancel
     * Force-cancel a booking (for dispute resolution).
     */
    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> forceCancelBooking(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        requireAdmin();
        BookingDTO result = adminService.forceCancelBooking(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Booking force-cancelled successfully", result));
    }

    /**
     * GET /api/admin/analytics
     * Platform-level analytics dashboard.
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        requireAdmin();
        Map<String, Object> analytics = adminService.getPlatformAnalytics();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Analytics fetched successfully", analytics));
    }

    /**
     * PUT /api/admin/commission?rate=12.5
     * Update the global platform commission rate (percentage).
     */
    @PutMapping("/commission")
    public ResponseEntity<ApiResponse<Void>> updateCommission(@RequestParam BigDecimal rate) {
        requireAdmin();
        adminService.updateCommission(rate);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Commission rate updated successfully", null));
    }
}
