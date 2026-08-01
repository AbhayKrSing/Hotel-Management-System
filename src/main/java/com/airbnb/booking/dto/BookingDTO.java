package com.airbnb.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.airbnb.booking.enums.BookingStatus;

public class BookingDTO {
    private UUID id;
    private UUID userId;
    private UUID hotelId;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private BigDecimal totalPrice;
    private Integer guestCount;
    private BookingStatus bookingStatus;
    private List<BookingRoomDTO> bookingRooms;
    private List<BookingOccupantDTO> bookingOccupants;

    public static class BookingRoomDTO {
        private UUID roomId;
        private Integer unitsBooked;
        private BigDecimal pricePerNight;

        public UUID getRoomId() { return roomId; }
        public void setRoomId(UUID roomId) { this.roomId = roomId; }
        public Integer getUnitsBooked() { return unitsBooked; }
        public void setUnitsBooked(Integer unitsBooked) { this.unitsBooked = unitsBooked; }
        public BigDecimal getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    }

    public static class BookingOccupantDTO {
        private String fullName;
        private Boolean isPrimary;
        private Integer age;
        private String idProofNumber;
        private String idProofType;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public Boolean getIsPrimary() { return isPrimary; }
        public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getIdProofNumber() { return idProofNumber; }
        public void setIdProofNumber(String idProofNumber) { this.idProofNumber = idProofNumber; }
        public String getIdProofType() { return idProofType; }
        public void setIdProofType(String idProofType) { this.idProofType = idProofType; }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getHotelId() { return hotelId; }
    public void setHotelId(UUID hotelId) { this.hotelId = hotelId; }
    public LocalDateTime getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDateTime checkInDate) { this.checkInDate = checkInDate; }
    public LocalDateTime getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDateTime checkOutDate) { this.checkOutDate = checkOutDate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    public List<BookingRoomDTO> getBookingRooms() { return bookingRooms; }
    public void setBookingRooms(List<BookingRoomDTO> bookingRooms) { this.bookingRooms = bookingRooms; }
    public List<BookingOccupantDTO> getBookingOccupants() { return bookingOccupants; }
    public void setBookingOccupants(List<BookingOccupantDTO> bookingOccupants) { this.bookingOccupants = bookingOccupants; }
}
