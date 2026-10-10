package com.airbnb.booking.scheduler;

import com.airbnb.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingExpirySchedulerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingExpiryScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void expireUnpaidBookings_delegatesToBookingService() {
        when(bookingService.expireUnpaidBookings()).thenReturn(2);

        scheduler.expireUnpaidBookings();

        verify(bookingService).expireUnpaidBookings();
    }
}
