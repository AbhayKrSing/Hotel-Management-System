package com.airbnb.admin.service.impl;

import com.airbnb.admin.model.SystemConfig;
import com.airbnb.admin.repository.SystemConfigRepository;
import com.airbnb.admin.service.AdminService;
import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.booking.service.BookingService;
import com.airbnb.shared.exceptions.ResourceNotFoundException;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private static final String COMMISSION_KEY = "platform_commission_rate";

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final SystemConfigRepository systemConfigRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            BookingRepository bookingRepository,
                            BookingService bookingService,
                            SystemConfigRepository systemConfigRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.systemConfigRepository = systemConfigRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void setUserStatus(UUID userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setIsEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @Override
    @Transactional
    public BookingDTO forceCancelBooking(UUID bookingId, String adminEmail) {
        return bookingService.cancelBooking(bookingId, adminEmail);
    }

    @Override
    public Map<String, Object> getPlatformAnalytics() {
        List<Booking> allBookings = bookingRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long totalHosts = allUsers.stream()
                .filter(u -> u.getRoles().contains(com.airbnb.user.enums.Roles.HOST))
                .count();
        long totalGuests = allUsers.stream()
                .filter(u -> u.getRoles().contains(com.airbnb.user.enums.Roles.GUEST))
                .count();

        long confirmedBookings = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED).count();
        long cancelledBookings = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CANCELLED).count();
        long pendingBookings = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING).count();

        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String commissionRate = systemConfigRepository.findByConfigKey(COMMISSION_KEY)
                .map(SystemConfig::getConfigValue)
                .orElse("10"); // default 10%

        BigDecimal commission = totalRevenue
                .multiply(new BigDecimal(commissionRate))
                .divide(BigDecimal.valueOf(100));

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", totalUsers);
        analytics.put("totalHosts", totalHosts);
        analytics.put("totalGuests", totalGuests);
        analytics.put("totalBookings", allBookings.size());
        analytics.put("confirmedBookings", confirmedBookings);
        analytics.put("cancelledBookings", cancelledBookings);
        analytics.put("pendingBookings", pendingBookings);
        analytics.put("totalRevenue", totalRevenue);
        analytics.put("platformCommissionRate", commissionRate + "%");
        analytics.put("platformCommissionEarned", commission);

        return analytics;
    }

    @Override
    @Transactional
    public void updateCommission(BigDecimal commissionRate) {
        SystemConfig config = systemConfigRepository.findByConfigKey(COMMISSION_KEY)
                .orElseGet(() -> {
                    SystemConfig c = new SystemConfig();
                    c.setConfigKey(COMMISSION_KEY);
                    c.setDescription("Platform commission percentage deducted from each booking");
                    return c;
                });
        config.setConfigValue(commissionRate.toPlainString());
        systemConfigRepository.save(config);
    }
}
