package com.airbnb.user.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.airbnb.hotel.model.Hotel;
import com.airbnb.user.enums.Roles;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name ="fr_user_master")
public class User {
	
 private UUID id;
 private Set<Roles> roles;
 private String email;
 private String name;
 private String password;
 private String createdBy;
 private String updatedBy;
 private List<Hotel> hotels;
 private LocalDateTime createdAt;
 private LocalDateTime updateAt;
 
 @Id
 @Column(name="C_User_Id",nullable = false)
 public UUID getId() {
	return id;
 }
 public void setId(UUID id) {
	this.id = id;
 }
 
 @ElementCollection //This is the collection of different values not entity
 @Enumerated(EnumType.STRING) //Storing ENUM type in string
 @CollectionTable(name ="fr_user_roles",joinColumns = @JoinColumn(name ="C_User_Id")) //Define seperate table name and foreign key column
 @Column(name ="C_Roles")
 public Set<Roles> getRoles() {
	return roles;
 }
 public void setRoles(Set<Roles> roles) {
	this.roles = roles;
 }
 
 @Column(name ="C_Email",unique = true)
 public String getEmail() {
	return email;
 }
 public void setEmail(String email) {
	this.email = email;
 }
 @Column(name ="C_Name")
 public String getName() {
	return name;
 }
 public void setName(String name) {
	this.name = name;
 }
 
 @Column(name ="C_Password")
 public String getPassword() {
	return password;
 }
 public void setPassword(String password) {
	this.password = password;
 }
 
 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 @JsonManagedReference 
 public List<Hotel> getHotels() {
	return hotels;
}
 public void setHotels(List<Hotel> hotels) {
	this.hotels = hotels;
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
