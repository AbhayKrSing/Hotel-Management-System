package com.airbnb.booking.enums;

public enum BookingStatus {
   CONFIRM("Confirm"),
   PENDING("Pending"),
   CHECKIN("CheckIn"),
   CHECKOUT("CheckOut"),
   CANCEL("Cancel");
   
   private String displayName;
   
   private BookingStatus(String displayName) {
	  this.displayName=displayName;
   }
   
   public String getDisplayName() {
	   return this.displayName;
   }
   
}
