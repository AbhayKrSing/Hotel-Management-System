package com.airbnb.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.payment.model.Payment;
import com.airbnb.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking extends FullyAuditableEntity {
  private UUID id;
  private UUID userId; // Must be Guest Id
  private UUID hotelId;
  private LocalDateTime checkInDate;
  private LocalDateTime checkOutDate;
  private BigDecimal totalPrice;
  private Integer guestCount;
  private BookingStatus bookingStatus;
  private List<BookingRoom> bookingRooms;
  private List<BookingOccupant> bookingOccupants;
  private List<Payment> payments;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "C_Booking_Id", nullable = false, columnDefinition = "VARCHAR(36)")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  @JoinColumn(name = "C_Hotel_Id")
  public UUID getHotelId() {
    return hotelId;
  }

  public void setHotelId(UUID hotelId) {
    this.hotelId = hotelId;
  }

  @Column(name = "Dt_Check_In_Date")
  public LocalDateTime getCheckInDate() {
    return checkInDate;
  }

  public void setCheckInDate(LocalDateTime checkInDate) {
    this.checkInDate = checkInDate;
  }

  @Column(name = "Dt_Check_Out_Date")
  public LocalDateTime getCheckOutDate() {
    return checkOutDate;
  }

  public void setCheckOutDate(LocalDateTime checkOutDate) {
    this.checkOutDate = checkOutDate;
  }

  @Column(name = "N_Total_Price")
  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  @Column(name = "N_Guest_Count")
  public Integer getGuestCount() {
    return guestCount;
  }

  public void setGuestCount(Integer guestCount) {
    this.guestCount = guestCount;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "C_Booking_Status")
  public BookingStatus getBookingStatus() {
    return bookingStatus;
  }

  public void setBookingStatus(BookingStatus bookingStatus) {
    this.bookingStatus = bookingStatus;
  }

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<BookingRoom> getBookingRooms() {
    return bookingRooms;
  }

  public void setBookingRooms(List<BookingRoom> bookingRooms) {
    this.bookingRooms = bookingRooms;
  }

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<BookingOccupant> getBookingOccupants() {
    return bookingOccupants;
  }

  public void setBookingOccupants(List<BookingOccupant> bookingOccupants) {
    this.bookingOccupants = bookingOccupants;
  }

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<Payment> getPayments() {
    return payments;
  }

  public void setPayments(List<Payment> payments) {
    this.payments = payments;
  }

}
