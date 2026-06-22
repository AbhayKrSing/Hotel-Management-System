package com.airbnb.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.airbnb.booking.enums.BookingStatus;
import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.payment.model.Payment;
import com.airbnb.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_booking")
public class Booking extends FullyAuditableEntity {
  private UUID id;
  private User user; //Must be Guest Id
  private Hotel hotel;
  private LocalDateTime checkInDate;
  private LocalDateTime checkOutDate;
  private BigDecimal totalPrice;
  private Integer guestCount;
  private BookingStatus bookingStatus;
  private List<BookingRoom> bookingRooms;
  private List<BookingOccupant> bookingOccupants;
  private List<Payment> payments;

  
  @Id
  @Column(name ="C_Booking_Id",nullable = false)
  public UUID getId() {
	return id;
  }
  public void setId(UUID id) {
	this.id = id;
  }
  
  @ManyToOne
  @JoinColumn(name ="C_User_Id")
  @JsonBackReference
  public User getUser() {
	return user;
  }
  public void setUser(User user) {
	this.user = user;
  }
  
  @ManyToOne
  @JoinColumn(name ="C_Hotel_Id")
  @JsonBackReference
  public Hotel getHotel() {
	return hotel;
  }
  public void setHotel(Hotel hotel) {
	this.hotel = hotel;
  }
  @Column(name ="Dt_Check_In_Date")
  public LocalDateTime getCheckInDate() {
	return checkInDate;
  }
  public void setCheckInDate(LocalDateTime checkInDate) {
	this.checkInDate = checkInDate;
  }
  
  @Column(name ="Dt_Check_Out_Date")
  public LocalDateTime getCheckOutDate() {
	return checkOutDate;
  }
  public void setCheckOutDate(LocalDateTime checkOutDate) {
	this.checkOutDate = checkOutDate;
  }
  
  @Column(name ="N_Total_Price")
  public BigDecimal getTotalPrice() {
	return totalPrice;
  }
  public void setTotalPrice(BigDecimal totalPrice) {
	this.totalPrice = totalPrice;
  }
  
  @Column(name ="C_Guest_Count")
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
  
  @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<BookingOccupant> getBookingOccupants() {
	return bookingOccupants;
}
  public void setBookingOccupants(List<BookingOccupant> bookingOccupants) {
	this.bookingOccupants = bookingOccupants;
  }
  
  @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<Payment> getPayments() {
	return payments;
  }
  public void setPayments(List<Payment> payments) {
	this.payments = payments;
  }
  
}
