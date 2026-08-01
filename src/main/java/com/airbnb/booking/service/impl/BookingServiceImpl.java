package com.airbnb.booking.service.impl;

import com.airbnb.booking.dto.BookingDTO;
import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.booking.model.Booking;
import com.airbnb.booking.model.BookingRoom;
import com.airbnb.booking.model.BookingOccupant;
import com.airbnb.booking.repository.BookingRepository;
import com.airbnb.booking.service.BookingService;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.model.Room;
import com.airbnb.hotel.model.Inventory;
import com.airbnb.hotel.repository.HotelRepository;
import com.airbnb.hotel.repository.RoomRepository;
import com.airbnb.inventory.repository.InventoryRepository;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import com.airbnb.shared.exceptions.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              HotelRepository hotelRepository,
                              RoomRepository roomRepository,
                              InventoryRepository inventoryRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDTO createBooking(BookingDTO dto, String guestEmail) {
        User guest = userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + dto.getHotelId()));

        List<Date> bookingDates = getDatesBetween(dto.getCheckInDate(), dto.getCheckOutDate());
        if (bookingDates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid booking dates");
        }

        Booking booking = new Booking();
        booking.setUserId(guest.getId());
        booking.setHotelId(hotel.getId());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setGuestCount(dto.getGuestCount());
        booking.setBookingStatus(BookingStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<BookingRoom> bookingRooms = new ArrayList<>();
        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (BookingDTO.BookingRoomDTO roomDto : dto.getBookingRooms()) {
            Room room = roomRepository.findById(roomDto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomDto.getRoomId()));

            // Check availability for each date in range
            for (Date date : bookingDates) {
                Inventory inventory = inventoryRepository.findByRoomIdAndDate(room.getId(), date)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "No inventory record found for room on date: " + date));

                if (inventory.getAvailableUnits() < roomDto.getUnitsBooked()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            "Insufficient availability for room " + room.getRoomTypes() + " on " + date);
                }

                // Decrement inventory
                inventory.setAvailableUnits(inventory.getAvailableUnits() - roomDto.getUnitsBooked());
                inventoriesToSave.add(inventory);
                
                // Add to total price
                BigDecimal dayCost = inventory.getPricePerNight().multiply(BigDecimal.valueOf(roomDto.getUnitsBooked()));
                totalPrice = totalPrice.add(dayCost);
            }

            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setBooking(booking);
            bookingRoom.setRoomId(room.getId());
            bookingRoom.setUnitsBooked(roomDto.getUnitsBooked());
            bookingRoom.setPricePerNight(room.getPricePerNight()); // Standard base price per night
            bookingRooms.add(bookingRoom);
        }

        booking.setTotalPrice(totalPrice);
        booking.setBookingRooms(bookingRooms);

        // Save Occupants
        List<BookingOccupant> occupants = new ArrayList<>();
        if (dto.getBookingOccupants() != null) {
            for (BookingDTO.BookingOccupantDTO occDto : dto.getBookingOccupants()) {
                BookingOccupant occupant = new BookingOccupant();
                occupant.setBooking(booking);
                occupant.setFullName(occDto.getFullName());
                occupant.setIsPrimary(occDto.getIsPrimary());
                occupant.setAge(occDto.getAge());
                occupant.setIdProofNumber(occDto.getIdProofNumber());
                occupant.setIdProofType(occDto.getIdProofType());
                occupants.add(occupant);
            }
        }
        booking.setBookingOccupants(occupants);

        // Save everything
        inventoryRepository.saveAll(inventoriesToSave);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Override
    public List<BookingDTO> getMyBookings(String guestEmail) {
        User guest = userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found"));

        return bookingRepository.findByUserId(guest.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO cancelBooking(UUID bookingId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Validate authority: guest who booked, or hotel owner, or admin
        boolean isGuest = booking.getUserId().equals(user.getId());
        //boolean isOwner = booking.getHotel().getHost().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().contains(com.airbnb.user.enums.Roles.ADMIN);

//        if (!isGuest && !isOwner && !isAdmin) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to cancel this booking");
//        }

      if (!isGuest && !isAdmin) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to cancel this booking");
      }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            return convertToDTO(booking);
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        // Release inventory
        List<Date> bookingDates = getDatesBetween(booking.getCheckInDate(), booking.getCheckOutDate());
        List<Inventory> inventoriesToSave = new ArrayList<>();

        for (BookingRoom br : booking.getBookingRooms()) {
            for (Date date : bookingDates) {
                Optional<Inventory> invOpt = inventoryRepository.findByRoomIdAndDate(br.getRoomId(), date);
                if (invOpt.isPresent()) {
                    Inventory inv = invOpt.get();
                    inv.setAvailableUnits(inv.getAvailableUnits() + br.getUnitsBooked());
                    inventoriesToSave.add(inv);
                }
            }
        }

        inventoryRepository.saveAll(inventoriesToSave);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Override
    public BookingDTO updateBookingStatus(UUID bookingId, BookingStatus status, String hostOrAdminEmail) {
        User user = userRepository.findByEmail(hostOrAdminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
//To check this logic
//        boolean isOwner = booking.getHotel().getHost().getId().equals(user.getId());
//        boolean isAdmin = user.getRoles().contains(com.airbnb.user.enums.Roles.ADMIN);
//
//        if (!isOwner && !isAdmin) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to update booking status");
//        }

        if (status == BookingStatus.CANCELLED && booking.getBookingStatus() != BookingStatus.CANCELLED) {
            return cancelBooking(bookingId, hostOrAdminEmail);
        }

        booking.setBookingStatus(status);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Override
    public List<BookingDTO> getHostBookings(String hostEmail) {
        User host = userRepository.findByEmail(hostEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Host user not found"));

        return bookingRepository.findBookingsByHostId(host.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private List<Date> getDatesBetween(LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Date> dates = new ArrayList<>();
        if (checkIn == null || checkOut == null || checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(localDateTimeToDate(checkIn));
        Date end = localDateTimeToDate(checkOut);
        while (cal.getTime().before(end)) {
            dates.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }

    private Date localDateTimeToDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, ldt.getYear());
        cal.set(Calendar.MONTH, ldt.getMonthValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, ldt.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private BookingDTO convertToDTO(Booking booking) {
        if (booking == null) return null;
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setHotelId(booking.getHotelId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setGuestCount(booking.getGuestCount());
        dto.setBookingStatus(booking.getBookingStatus());

        if (booking.getBookingRooms() != null) {
            dto.setBookingRooms(booking.getBookingRooms().stream().map(br -> {
                BookingDTO.BookingRoomDTO brDto = new BookingDTO.BookingRoomDTO();
                brDto.setRoomId(br.getRoomId());
                brDto.setUnitsBooked(br.getUnitsBooked());
                brDto.setPricePerNight(br.getPricePerNight());
                return brDto;
            }).collect(Collectors.toList()));
        }

        if (booking.getBookingOccupants() != null) {
            dto.setBookingOccupants(booking.getBookingOccupants().stream().map(bo -> {
                BookingDTO.BookingOccupantDTO boDto = new BookingDTO.BookingOccupantDTO();
                boDto.setFullName(bo.getFullName());
                boDto.setIsPrimary(bo.getIsPrimary());
                boDto.setAge(bo.getAge());
                boDto.setIdProofNumber(bo.getIdProofNumber());
                boDto.setIdProofType(bo.getIdProofType());
                return boDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
