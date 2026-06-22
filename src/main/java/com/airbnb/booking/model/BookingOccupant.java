package com.airbnb.booking.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_booking_occupants")
public class BookingOccupant {
   private UUID id;
   private Booking booking;
   private String fullName;
   private Boolean isPrimary;
   private Integer age;
   private String idProofNumber;
   private String idProofType;
   private LocalDateTime createdAt;
   private String createdBy;
   
   @Id
   @Column(name ="C_Booking_Occupant_Id",nullable = false)
   public UUID getId() {
	return id;
   }
   public void setId(UUID id) {
	this.id = id;
   }
   @ManyToOne
   @JoinColumn(name ="C_Booking_Id")
   @JsonBackReference
   public Booking getBooking() {
	return booking;
   }
   public void setBooking(Booking booking) {
	this.booking = booking;
   }
   
   @Column(name ="C_Full_Name")
   public String getFullName() {
	return fullName;
   }
   public void setFullName(String fullName) {
	this.fullName = fullName;
   }
   
   @Column(name ="N_Is_Primary")
   public Boolean getIsPrimary() {
	return isPrimary;
   }
   public void setIsPrimary(Boolean isPrimary) {
	this.isPrimary = isPrimary;
   }
   
   @Column(name ="N_Age")
   public Integer getAge() {
	return age;
   }
   public void setAge(Integer age) {
	this.age = age;
   }
   
   @Column(name ="C_Id_Proof_Number")
   public String getIdProofNumber() {
	return idProofNumber;
   }
   public void setIdProofNumber(String idProofNumber) {
	this.idProofNumber = idProofNumber;
   }
   @Column(name ="C_Id_Proof_Type")
   public String getIdProofType() {
	return idProofType;
   }
   public void setIdProofType(String idProofType) {
	this.idProofType = idProofType;
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
