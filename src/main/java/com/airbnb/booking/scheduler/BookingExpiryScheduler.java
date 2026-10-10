package com.airbnb.booking.scheduler;

import com.airbnb.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingExpiryScheduler.class);

    private final BookingService bookingService;

    public BookingExpiryScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Expires unpaid PENDING bookings past their payment window and releases inventory.
     * Runs every 2 minutes.
     */
    @Scheduled(fixedDelayString = "${booking.expiry.fixed-delay-ms:120000}")
    public void expireUnpaidBookings() {
        int expiredCount = bookingService.expireUnpaidBookings();
        if (expiredCount > 0) {
            log.info("Expired {} unpaid booking(s) and released inventory", expiredCount);
        }
    }
}
