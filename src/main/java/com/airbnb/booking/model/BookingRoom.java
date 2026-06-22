package com.airbnb.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.airbnb.hotel.model.Room;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_booking_room")
public class BookingRoom {
 private UUID id;
 private Room room;
 private Booking  booking;
 private BigDecimal pricePerNight;
 private Integer unitsBooked;
 private LocalDateTime createdAt;
 private String createdBy;
 
 @Id
 @Column(name ="C_Booking_Room_Id",nullable = false)
 public UUID getId() {
	return id;
 }
 public void setId(UUID id) {
	this.id = id;
 }
 
 @ManyToOne
 @JoinColumn(name = "C_Room_Id")
 @JsonBackReference
 public Room getRoom() {
	return room;
 }
 public void setRoom(Room room) {
	this.room = room;
 }
 
 @ManyToOne
 @JoinColumn(name = "C_Booking_Id")
 @JsonBackReference
 public Booking getBooking() {
	return booking;
 }
 public void setBooking(Booking booking) {
	this.booking = booking;
 }
 
 @Column(name ="N_Price_Per_Night")
 public BigDecimal getPricePerNight() {
	return pricePerNight;
 }
 public void setPricePerNight(BigDecimal pricePerNight) {
	this.pricePerNight = pricePerNight;
 }
 
 @Column(name ="N_Units_Booked")
 public Integer getUnitsBooked() {
	return unitsBooked;
 }
 public void setUnitsBooked(Integer unitsBooked) {
	this.unitsBooked = unitsBooked;
 }
 
 @Column(name ="Dt_Created_At")
 public LocalDateTime getCreatedAt() {
	return createdAt;
 }
 public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
 }
 @Column(name ="C_Created_By")
 public String getCreatedBy() {
	return createdBy;
 }
 public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
 }
 
}
