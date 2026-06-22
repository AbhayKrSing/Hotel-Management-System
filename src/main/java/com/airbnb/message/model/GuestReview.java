package com.airbnb.message.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.airbnb.hotel.model.Hotel;
import com.airbnb.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_guest_review")
public class GuestReview {
  private UUID id;
  private String reviewMessage;
  private Hotel hotel;
  private User user;  //Only guest can write review
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String updatedBy;
  
  @Id
  @Column(name = "C_Guest_Review_Id")
  public UUID getId() {
	return id;
  }
  public void setId(UUID id) {
	this.id = id;
  }
  
  @Column(name = "C_Review_Message")
  public String getReviewMessage() {
	return reviewMessage;
  }
  public void setReviewMessage(String reviewMessage) {
	this.reviewMessage = reviewMessage;
  }
  @ManyToOne
  @JoinColumn(name = "C_Hotel_Id")
  @JsonBackReference
  public Hotel getHotel() {
	return hotel;
  }
  public void setHotel(Hotel hotel) {
	this.hotel = hotel;
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
  
  @Column(name ="Dt_Created_Date")
  public LocalDateTime getCreatedAt() {
	return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
  }
  @Column(name ="Dt_Updated_Date")
  public LocalDateTime getUpdatedAt() {
	return updatedAt;
  }
  public void setUpdatedAt(LocalDateTime updatedAt) {
	this.updatedAt = updatedAt;
  }
  @Column(name ="C_Created_By")
  public String getCreatedBy() {
	return createdBy;
  }
  public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
  }
  @Column(name ="C_Updated_By")
  public String getUpdatedBy() {
	return updatedBy;
  }
  public void setUpdatedBy(String updatedBy) {
	this.updatedBy = updatedBy;
  }
  
}
