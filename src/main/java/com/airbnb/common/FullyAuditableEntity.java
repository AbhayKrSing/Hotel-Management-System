package com.airbnb.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class FullyAuditableEntity extends AuditableEntry {
	private LocalDateTime updatedAt;
	private String updatedBy;


	@Column(name = "Dt_Updated_At")
	@LastModifiedDate
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	 
	 @Column(name ="C_Updated_By")
	 @LastModifiedBy
	 public String getUpdatedBy() {
		return updatedBy;
	 }
	 public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	 }
	 

	 
}
