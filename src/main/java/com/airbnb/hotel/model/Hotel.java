package com.airbnb.hotel.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.airbnb.booking.model.Booking;
import com.airbnb.hotel.enums.Status;
import com.airbnb.message.model.GuestReview;
import com.airbnb.photo.model.Photo;
import com.airbnb.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name ="fr_hotel_master")
public class Hotel {

	private UUID id;
	private String name;
	private String description;
	private String address;
	private List<Photo> photo;
	private List<String> rules;
	private Status status;
	private User user;
	private List<Room> rooms;
	private List<Hotel_Amenities> hotelAmenities;
	private List<Booking> bookings;
	private List<GuestReview> guestReviews;
	private String createdBy;
	private String updatedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	
	@Id
	@Column(name ="C_Hotel_Id",nullable = false)
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	@Column(name ="C_Hotel_Name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name ="C_Hotel_Description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Column(name ="C_Address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@OneToMany(mappedBy = "hotelId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<Photo> getPhoto() {
		return photo;
	}
	public void setPhoto(List<Photo> photoUrl) {
		this.photo = photoUrl;
	}
	
	@ElementCollection
	@CollectionTable(name = "fr_hotel_rules", joinColumns = @JoinColumn(name = "C_Hotel_Id"))
	@Column(name = "C_Rule")
	public List<String> getRules() {
		return rules;
	}
	public void setRules(List<String> rules) {
		this.rules = rules;
	}
	@Enumerated(EnumType.STRING)
	@Column(name ="C_Status")
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@ManyToOne
	@JoinColumn(name = "C_User_Id")
	@JsonBackReference
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	@OneToMany(mappedBy = "hotelId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<Hotel_Amenities> getHotelAmenities() {
		return hotelAmenities;
	}
	public void setHotelAmenities(List<Hotel_Amenities> hotelAmenities) {
		this.hotelAmenities = hotelAmenities;
	}
	
	@OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<Booking> getBookings() {
		return bookings;
	}
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	
	@OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<GuestReview> getGuestReviews() {
		return guestReviews;
	}
	public void setGuestReviews(List<GuestReview> guestReviews) {
		this.guestReviews = guestReviews;
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
	@Column(name = "Dt_Created_At")
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	 @Column(name ="Dt_Updated_At")
	public LocalDateTime getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}
}
