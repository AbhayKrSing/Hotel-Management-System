package com.airbnb.message.model;

import java.util.UUID;

import com.airbnb.booking.model.Booking;
import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.hotel.model.Hotel;
import com.airbnb.user.model.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name ="guest_review")
public class GuestReview extends FullyAuditableEntity {
  private UUID id;
  private String reviewMessage;
  private String hostResponse;  // Host's reply to the review
  private UUID hotelId;
  private UUID userId;  //Only guest can write review
  private UUID bookingId;
  private Integer rating;
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "C_Guest_Review_Id",columnDefinition = "VARCHAR(36)")
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

  @Column(name = "C_Host_Response")
  public String getHostResponse() {
	return hostResponse;
  }
  public void setHostResponse(String hostResponse) {
	this.hostResponse = hostResponse;
  }

  @JoinColumn(name = "C_Hotel_Id")
  public UUID getHotelId() {
	return hotelId;
  }
  public void setHotelId(UUID hotelId) {
	this.hotelId = hotelId;
  }

  @JoinColumn(name ="C_User_Id")
  public UUID getUserId() {
	return userId;
  }
  public void setUserId(UUID userId) {
	this.userId = userId;
  }
  
  @JoinColumn(name = "C_Booking_Id", unique = true)
  public UUID getBookingId() {
	return bookingId;
  }
  public void setBookingId(UUID bookingId) {
	this.bookingId = bookingId;
  }

  @Column(name = "N_Rating")
  @Min(1)
  @Max(5)
  public Integer getRating() {
	return rating;
  }
  public void setRating(Integer rating) {
	this.rating = rating;
  }
  
}
