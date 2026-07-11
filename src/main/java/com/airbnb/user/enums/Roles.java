package com.airbnb.user.enums;


public enum Roles {
   ADMIN("ADMIN"),
   HOST("HOST"),
   GUEST("GUEST");
   
   private String displayName;
   Roles(String name) {
	   this.displayName=name;
   }
   public String getDisplayName(){
	   return this.displayName;
   }
   
}
