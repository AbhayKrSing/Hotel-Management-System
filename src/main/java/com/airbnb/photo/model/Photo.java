package com.airbnb.photo.model;

import java.util.UUID;

import com.airbnb.common.AuditableEntry;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.hotel.model.Room;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_store_photo")
public class Photo extends AuditableEntry {
  private UUID id;
  private Room roomId;
  private Hotel hotelId;
  private String photoUrl;
  
  @Id
  @Column(name = "C_Photo_Id",nullable = false)
  public UUID getId() {
	return id;
  }
  public void setId(UUID id) {
	this.id = id;
  }
  
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "C_Room_Id")
  public Room getRoomId() {
	return roomId;
  }
  public void setRoomId(Room roomId) {
	this.roomId = roomId;
  }
  
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "C_Hotel_Id")
  public Hotel getHotelId() {
	return hotelId;
  }
  public void setHotelId(Hotel hotelId) {
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
