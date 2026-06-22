package com.airbnb.booking.model;

import java.util.UUID;

import com.airbnb.common.AuditableEntry;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_booking_occupants")
public class BookingOccupant extends AuditableEntry {
   private UUID id;
   private Booking booking;
   private String fullName;
   private Boolean isPrimary;
   private Integer age;
   private String idProofNumber;
   private String idProofType;
   
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
   
}
