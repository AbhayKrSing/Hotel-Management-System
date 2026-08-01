package com.airbnb.admin.service;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    List<User> getAllUsers();
    void setUserStatus(UUID userId, boolean enabled);
    List<BookingDTO> getAllBookings();
    BookingDTO forceCancelBooking(UUID bookingId, String adminEmail);
    Map<String, Object> getPlatformAnalytics();
    void updateCommission(BigDecimal commissionRate);
}
