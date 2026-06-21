package com.airbnb.user.enums;


public enum Roles {
   ADMIN("Admin"),
   HOST("Host"),
   GUEST("Guest");
   
   private String displayName;
   Roles(String name) {
	   this.displayName=name;
   }
   public String getDisplayName(){
	   return this.displayName;
   }
   
}
