package com.airbnb.booking.service.impl;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingRoom;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.hotel.model.Room;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.hotel.repository.RoomRepository;
import com.airbnb.inventory.repository.InventoryRepository;
import com.airbnb.user.enums.Roles;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User guest;
    private Hotel hotel;
    private Room room;
    private Inventory inventory;
    private UUID roomId;
    private UUID hotelId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        guest = new User();
        guest.setId(UUID.randomUUID());
        guest.setEmail("guest@example.com");
        guest.setRoles(Set.of(Roles.GUEST));

        hotelId = UUID.randomUUID();
        hotel = new Hotel();
        hotel.setId(hotelId);

        roomId = UUID.randomUUID();
        room = new Room();
        room.setId(roomId);
        room.setRoomTypes("Deluxe");
        room.setPricePerNight(new BigDecimal("1000"));

        inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setRoomId(roomId);
        inventory.setAvailableUnits(5);
        inventory.setPricePerNight(new BigDecimal("1000"));
    }

    @Test
    void createBooking_setsPendingExpiresAtAndDecrementsInventory() {
        BookingDTO dto = buildCreateDto();

        when(userRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(guest));
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(inventoryRepository.findByRoomIdAndDate(eq(roomId), any(Date.class))).thenReturn(Optional.of(inventory));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            if (booking.getId() == null) {
                booking.setId(UUID.randomUUID());
            }
            return booking;
        });

        LocalDateTime before = LocalDateTime.now();
        BookingDTO result = bookingService.createBooking(dto, "guest@example.com");
        LocalDateTime after = LocalDateTime.now();

        assertEquals(BookingStatus.PENDING, result.getBookingStatus());
        assertNotNull(result.getExpiresAt());
        assertFalse(result.getExpiresAt().isBefore(before.plusMinutes(14)));
        assertFalse(result.getExpiresAt().isAfter(after.plusMinutes(16)));

        assertEquals(4, inventory.getAvailableUnits());
        verify(inventoryRepository).saveAll(any());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void expireUnpaidBookings_cancelsAndReleasesInventory() {
        Booking expired = pendingBookingWithHold(2);
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        // Simulate inventory already held at booking create time
        inventory.setAvailableUnits(3);

        when(bookingRepository.findByBookingStatusAndExpiresAtBefore(eq(BookingStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expired));
        when(bookingRepository.findById(expired.getId())).thenReturn(Optional.of(expired));
        when(inventoryRepository.findByRoomIdAndDate(eq(roomId), any(Date.class))).thenReturn(Optional.of(inventory));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int count = bookingService.expireUnpaidBookings();

        assertEquals(1, count);
        assertEquals(BookingStatus.CANCELLED, expired.getBookingStatus());
        assertEquals(5, inventory.getAvailableUnits()); // 2 held units restored
        verify(inventoryRepository).saveAll(any());
    }

    @Test
    void expireUnpaidBookings_skipsAlreadyConfirmedBooking() {
        Booking expired = pendingBookingWithHold(1);
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        Booking confirmed = pendingBookingWithHold(1);
        confirmed.setId(expired.getId());
        confirmed.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByBookingStatusAndExpiresAtBefore(eq(BookingStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expired));
        when(bookingRepository.findById(expired.getId())).thenReturn(Optional.of(confirmed));

        int count = bookingService.expireUnpaidBookings();

        assertEquals(0, count);
        verify(inventoryRepository, never()).saveAll(any());
    }

    @Test
    void cancelAndReleaseInventory_isIdempotentWhenAlreadyCancelled() {
        Booking booking = pendingBookingWithHold(1);
        booking.setBookingStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.cancelAndReleaseInventory(booking.getId());

        assertEquals(BookingStatus.CANCELLED, result.getBookingStatus());
        verify(inventoryRepository, never()).saveAll(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_rejectsConfirmed() {
        Booking booking = pendingBookingWithHold(1);
        when(userRepository.findByEmail("host@example.com")).thenReturn(Optional.of(guest));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.updateBookingStatus(booking.getId(), BookingStatus.CONFIRMED, "host@example.com"));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("confirmed after successful payment"));
    }

    private BookingDTO buildCreateDto() {
        BookingDTO dto = new BookingDTO();
        dto.setHotelId(hotelId);
        dto.setCheckInDate(LocalDateTime.of(2030, 1, 10, 14, 0));
        dto.setCheckOutDate(LocalDateTime.of(2030, 1, 11, 11, 0));
        dto.setGuestCount(2);

        BookingDTO.BookingRoomDTO roomDto = new BookingDTO.BookingRoomDTO();
        roomDto.setRoomId(roomId);
        roomDto.setUnitsBooked(1);
        dto.setBookingRooms(List.of(roomDto));
        return dto;
    }

    private Booking pendingBookingWithHold(int unitsBooked) {
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setUserId(guest.getId());
        booking.setHotelId(hotelId);
        booking.setCheckInDate(LocalDateTime.of(2030, 1, 10, 14, 0));
        booking.setCheckOutDate(LocalDateTime.of(2030, 1, 11, 11, 0));
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        booking.setTotalPrice(new BigDecimal("1000"));

        BookingRoom bookingRoom = new BookingRoom();
        bookingRoom.setBooking(booking);
        bookingRoom.setRoomId(roomId);
        bookingRoom.setUnitsBooked(unitsBooked);
        bookingRoom.setPricePerNight(new BigDecimal("1000"));
        booking.setBookingRooms(List.of(bookingRoom));
        return booking;
    }
}
