package com.airbnb.hotel.enums;

public enum Status {

	Active("Active"),
	InActive("InActive");
	
	private String displayName;
	Status(String status){
		this.displayName=status;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
}
