package com.airbnb.payment.enums;

public enum PaymentStatus {
  SUCCESS("Success"),
  FAILED("Failed"),
  PENDING("Pending");
	
  private String displayName;  
  PaymentStatus(String displayName) {
	this.displayName=displayName;
  }
  
  public String getDisplayName() {
	  return this.displayName;
  }
}
