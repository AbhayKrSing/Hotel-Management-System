package com.airbnb.booking.service;

import java.util.List;
import java.util.UUID;
import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;

public interface BookingService {
    BookingDTO createBooking(BookingDTO dto, String guestEmail);
    List<BookingDTO> getMyBookings(String guestEmail);
    BookingDTO cancelBooking(UUID bookingId, String userEmail);
    BookingDTO updateBookingStatus(UUID bookingId, BookingStatus status, String hostOrAdminEmail);
    List<BookingDTO> getHostBookings(String hostEmail);
    List<BookingDTO> getAllBookings();
}
