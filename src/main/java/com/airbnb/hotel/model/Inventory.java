package com.airbnb.hotel.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fr_room_inventory")
public class Inventory {
   private UUID id;
   private Date date;
   private Integer availableUnits;
   private Room room;
   private BigDecimal pricePerNight;
   
   @Id
   @Column(name = "C_Inventory_Id")
   public UUID getId() {
	return id;
   }
   public void setId(UUID id) {
	this.id = id;
   }
   @Column(name = "Dt_Date")
   public Date getDate() {
	return date;
   }
   public void setDate(Date date) {
	this.date = date;
   }
   
   @Column(name ="N_Available_Units")
   public Integer getAvailableUnits() {
	return availableUnits;
   }
   public void setAvailableUnits(Integer availableUnits) {
	this.availableUnits = availableUnits;
   }
   
   @OneToOne
   @JsonBackReference
   @JoinColumn(name ="C_Room_Id")
   public Room getRoom() {
	return room;
   }
   public void setRoom(Room room) {
	this.room = room;
   }
   
   @Column(name ="N_Price_Per_Night")
   public BigDecimal getPricePerNight() {
	return pricePerNight;
   }
   public void setPricePerNight(BigDecimal pricePerNight) {
	this.pricePerNight = pricePerNight;
   }
   
}
