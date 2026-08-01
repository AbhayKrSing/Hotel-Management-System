package com.airbnb.user.model;


import java.util.Set;
import java.util.UUID;

import com.airbnb.common.FullyAuditableEntity;
import com.airbnb.user.enums.Roles;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import jakarta.persistence.Table;

@Entity
@Table(name ="user_master")
public class User extends FullyAuditableEntity {
	
 private UUID id;
 private Set<Roles> roles;
 private String email;
 private String name;
 private String password;
 private Boolean isEnabled = true;  // Admin can disable/suspend user
 
 @Id
 @GeneratedValue(strategy = GenerationType.UUID)
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="C_User_Id",nullable = false,columnDefinition = "VARCHAR(36)")
 public UUID getId() {
	return id;
 }
 public void setId(UUID id) {
	this.id = id;
 }
 
 @ElementCollection(fetch = FetchType.EAGER) //This is the collection of different values not entity
 @Enumerated(EnumType.STRING) //Storing ENUM type in string
 @CollectionTable(name ="user_roles",joinColumns = @JoinColumn(name ="C_User_Id")) //Define seperate table name and foreign key column
 @Column(name ="C_Roles")
 public Set<Roles> getRoles() {
	return roles;
 }
 public void setRoles(Set<Roles> roles) {
	this.roles = roles;
 }
 
 @Column(name ="C_Email",unique = true,nullable = false)
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

 @Column(name = "B_Is_Enabled")
 public Boolean getIsEnabled() {
	return isEnabled;
 }
 public void setIsEnabled(Boolean isEnabled) {
	this.isEnabled = isEnabled;
 }


 
}
