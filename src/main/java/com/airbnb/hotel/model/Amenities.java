package com.airbnb.hotel.model;

import java.util.List;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name ="amenities_master")
public class Amenities {
  private UUID id;
  private String amenity;
  private String icon;
  private List<HotelAmenities> hotelAmenties;
  
  
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name ="C_Amenity_Id",columnDefinition = "VARCHAR(36)")
  public UUID getId() {
	return id;
  }
  public void setId(UUID id) {
	this.id = id;
  }
  
  @Column(name ="C_Amenity_Name")
  public String getAmenity() {
	return amenity;
  }
  public void setAmenity(String amenity) {
	this.amenity = amenity;
  }
  
  @Column(name = "C_Amenity_Icon")
  public String getIcon() {
	return icon;
  }
  public void setIcon(String icon) {
	this.icon = icon;
  }
  @OneToMany(mappedBy = "amenityId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
  @JsonManagedReference
  public List<HotelAmenities> getHotelAmenties() {
	return hotelAmenties;
  }
  public void setHotelAmenties(List<HotelAmenities> hotelAmenties) {
	this.hotelAmenties = hotelAmenties;
  }
  
  
}
