package com.airbnb.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.airbnb.booking.model.BookingRoom;
import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.hotel.enums.Status;
import com.airbnb.photo.model.Photo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_room_master")
public class Room extends FullyAuditableEntity {

	private UUID id;
	private Hotel hotel;
	private String description;
	private String  roomTypes;
	private Integer guestCapacity;
	private List<Photo> photo;
	private String bedType;
	private Status status;
	private Integer noOfUnits;
	private BigDecimal pricePerNight;
	private Inventory inventory;
	private List<BookingRoom> bookingRooms;
	
	@Id
	@Column(name= "C_Room_Id")
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "C_Hotel_Id")
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	@Column(name="C_Room_Description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Column(name="C_Room_Type")
	public String getRoomTypes() {
		return roomTypes;
	}
	public void setRoomTypes(String roomTypes) {
		this.roomTypes = roomTypes;
	}
	
	@Column(name ="N_Guest_Capacity")
	public Integer getGuestCapacity() {
		return guestCapacity;
	}
	public void setGuestCapacity(Integer guestCapacity) {
		this.guestCapacity = guestCapacity;
	}

    @OneToMany(mappedBy = "roomId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
	public List<Photo> getPhoto() {
		return photo;
	}
	public void setPhoto(List<Photo> photo) {
		this.photo = photo;
	}
	
	@Column(name ="C_Bed_Type")
	public String getBedType() {
		return bedType;
	}
	public void setBedType(String bedType) {
		this.bedType = bedType;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name= "C_Status")
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Column(name ="N_No_of_Units")
	public Integer getNoOfUnits() {
		return noOfUnits;
	}
	public void setNoOfUnits(Integer noOfUnits) {
		this.noOfUnits = noOfUnits;
	}
	
	@Column(name ="N_Price_Per_Night")
	public BigDecimal getPricePerNight() {
		return pricePerNight;
	}
	public void setPricePerNight(BigDecimal pricePerNight) {
		this.pricePerNight = pricePerNight;
	}
    @OneToOne(mappedBy = "room",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	@OneToMany(mappedBy = "room",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<BookingRoom> getBookingRooms() {
		return bookingRooms;
	}
	public void setBookingRooms(List<BookingRoom> bookingRooms) {
		this.bookingRooms = bookingRooms;
	}
	
 }
