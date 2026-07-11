package com.airbnb.user.dto;

import java.util.Set;
import java.util.UUID;

import com.airbnb.user.enums.Roles;

public class UserDTO {
	private UUID id;
	private String email;
	private String fullName;
	private Set<Roles> role;
	private boolean active;

	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Set<Roles> getRole() {
		return role;
	}
	public void setRole(Set<Roles> role) {
		this.role = role;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
