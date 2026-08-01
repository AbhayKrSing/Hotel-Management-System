package com.airbnb.photo.model;

import java.util.UUID;

import com.airbnb.common.AuditableEntry;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.model.Room;
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

@Entity
@Table(name ="store_photo")
public class Photo extends AuditableEntry {
  private UUID id;
  private UUID roomId;
  private UUID hotelId;
  private String photoUrl;
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "C_Photo_Id",nullable = false,columnDefinition = "VARCHAR(36)")
  public UUID getId() {
	return id;
  }
  public void setId(UUID id) {
	this.id = id;
  }
  
  @JoinColumn(name = "C_Room_Id")
  public UUID getRoomId() {
	return roomId;
  }
  public void setRoomId(UUID roomId) {
	this.roomId = roomId;
  }
  
  @JoinColumn(name = "C_Hotel_Id")
  public UUID getHotelId() {
	return hotelId;
  }
  public void setHotelId(UUID hotelId) {
	this.hotelId = hotelId;
  }
  
  @Column(name ="C_Photo_Url")
  public String getPhotoUrl() {
	return photoUrl;
  }
  public void setPhotoUrl(String photoUrl) {
	this.photoUrl = photoUrl;
  }
  
}
