package com.airbnb.common;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class AuditableEntry {

	private LocalDateTime createdAt;
	private String createdBy;

	@Column(name = "Dt_Created_At")
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "C_Created_By")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	@PrePersist
	public void beforeInsert() {
		this.createdAt=LocalDateTime.now();
	}
}
