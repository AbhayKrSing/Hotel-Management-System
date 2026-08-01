package com.airbnb.hotel.enums;

public enum HotelStatus {

	ACTIVE("ACTIVE"),
	INACTIVE("INACTIVE"),
	PENDING_APPROVAL("PENDING_APPROVAL"),
	SUSPENDED("SUSPENDED");

	private String displayName;
	HotelStatus(String status){
		this.displayName=status;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
}
