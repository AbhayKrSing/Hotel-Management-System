package com.airbnb.hotel.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.airbnb.common.FullyAuditableEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "room_inventory", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"C_Room_Id", "Dt_Date"})
})
public class Inventory extends FullyAuditableEntity {
   private UUID id;
   private Date date;
   private Integer availableUnits;
   private UUID roomId;
   private BigDecimal pricePerNight;
   
   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @JdbcTypeCode(SqlTypes.VARCHAR)
   @Column(name = "C_Inventory_Id",columnDefinition = "VARCHAR(36)")
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
   

   @JoinColumn(name ="C_Room_Id")
   public UUID getRoomId() {
	return roomId;
   }
   public void setRoomId(UUID roomId) {
	this.roomId = roomId;
   }
   
   @Column(name ="N_Price_Per_Night")
   public BigDecimal getPricePerNight() {
	return pricePerNight;
   }
   public void setPricePerNight(BigDecimal pricePerNight) {
	this.pricePerNight = pricePerNight;
   }
   
}
