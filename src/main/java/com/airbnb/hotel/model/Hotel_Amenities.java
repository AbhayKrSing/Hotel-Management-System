package com.airbnb.hotel.model;

import java.util.UUID;

import com.airbnb.common.FullyAuditableEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_hotel_amenities")
public class Hotel_Amenities extends FullyAuditableEntity {

	private UUID id;
	private Hotel hotelId;
	private Amenities amenityId;
	
	@Id
	@Column(name ="C_Hotel_Amenties_Id")
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name ="C_Hotel_Id")
	public Hotel getHotelId() {
		return hotelId;
	}
	public void setHotelId(Hotel hotelId) {
		this.hotelId = hotelId;
	}
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name ="C_Amenity_Id")
	public Amenities getAmenityId() {
		return amenityId;
	}
	public void setAmenityId(Amenities amenityId) {
		this.amenityId = amenityId;
	}
	
}
