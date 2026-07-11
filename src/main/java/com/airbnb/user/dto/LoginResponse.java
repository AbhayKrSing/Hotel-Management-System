package com.airbnb.user.dto;

import java.util.Set;

import com.airbnb.user.enums.Roles;

public class LoginResponse {
    private String token;
    private String email;
    private Set<Roles> role;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<Roles> getRole() {
		return role;
	}
	public void setRole(Set<Roles> role) {
		this.role = role;
	}
    
}
