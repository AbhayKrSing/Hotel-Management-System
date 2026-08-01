package com.airbnb.booking.enums;

public enum BookingStatus {
   CONFIRMED("CONFIRMED"),
   PENDING("PENDING"),
   CHECKIN("CHECKIN"),
   CHECKOUT("CHECKOUT"),
   CANCELLED("CANCELLED");
   
   private String displayName;
   
   private BookingStatus(String displayName) {
	  this.displayName=displayName;
   }
   
   public String getDisplayName() {
	   return this.displayName;
   }
   
}
