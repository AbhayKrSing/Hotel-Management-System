package com.airbnb.user.dto;

import java.util.Set;

import com.airbnb.user.enums.Roles;

public class RegisterRequest {
	private String email;
	private String password;
	private String fullName;
	private Set<Roles> roles; // "GUEST", "HOST", "ADMIN"
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Set<Roles> getRoles() {
		return roles;
	}
	public void setRoles(Set<Roles> roles) {
		this.roles = roles;
	}
}
