package com.airbnb.common;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
public abstract class FullyAuditableEntity extends AuditableEntry {
	private LocalDateTime updatedAt;
	private String updatedBy;


	@Column(name = "Dt_Updated_At")
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	 
	 @Column(name ="C_Updated_By")
	 public String getUpdatedBy() {
		return updatedBy;
	 }
	 public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	 }
	 
	 @PreUpdate
	 public void beforeUpdate() {
		 this.updatedAt=LocalDateTime.now();
	 }
	 
}
