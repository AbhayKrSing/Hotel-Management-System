package com.airbnb.hotel.model;

import java.util.List;
import java.util.UUID;

import com.airbnb.booking.model.Booking;
import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.hotel.enums.HotelStatus;
import com.airbnb.message.model.GuestReview;
import com.airbnb.photo.model.Photo;
import com.airbnb.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name ="hotel_master")
public class Hotel extends FullyAuditableEntity {

	private UUID id;
	private String name;
	private String description;
	private String address;
	private String city;
	private String country;
	private String zipCode;
	private String phoneNumber;
	private String cancellationPolicy;
	private String checkInTime;
	private String checkOutTime;
	private Integer starRating;
	private Boolean isVerified;
	private Double latitude;
	private Double longitude;
	private List<String> rules;
	private HotelStatus status;
	private UUID hostId;
	private List<Room> rooms;
	private List<HotelAmenities> hotelAmenities;
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name ="C_Hotel_Id",nullable = false,columnDefinition = "VARCHAR(36)")
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
	
	@Column(name ="C_City")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	@Column(name ="C_Country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name ="C_Zip_Code")
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Column(name ="C_Phone_Number")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name ="C_Cancellation_Policy")
	public String getCancellationPolicy() {
		return cancellationPolicy;
	}
	public void setCancellationPolicy(String cancellationPolicy) {
		this.cancellationPolicy = cancellationPolicy;
	}

	@Column(name ="C_Check_In_Time")
	public String getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	@Column(name ="C_Check_Out_Time")
	public String getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	@Column(name ="N_Star_Rating")
	public Integer getStarRating() {
		return starRating;
	}
	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

	@Column(name ="B_Is_Verified")
	public Boolean getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	@Column(name ="N_Latitude")
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name ="N_Longitude")
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@ElementCollection
	@CollectionTable(name = "hotel_rules", joinColumns = @JoinColumn(name = "C_Hotel_Id"))
	@Column(name = "C_Rule")
	public List<String> getRules() {
		return rules;
	}
	public void setRules(List<String> rules) {
		this.rules = rules;
	}
	@Enumerated(EnumType.STRING)
	@Column(name ="C_Status")
	public HotelStatus getStatus() {
		return status;
	}
	public void setStatus(HotelStatus status) {
		this.status = status;
	}
	

	@JoinColumn(name = "C_Host_Id")
	public UUID getHostId() {
		return hostId;
	}
	public void setHostId(UUID hostId) {
		this.hostId = hostId;
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
	public List<HotelAmenities> getHotelAmenities() {
		return hotelAmenities;
	}
	public void setHotelAmenities(List<HotelAmenities> hotelAmenities) {
		this.hotelAmenities = hotelAmenities;
	}
	
}
