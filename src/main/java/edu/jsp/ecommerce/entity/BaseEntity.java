package edu.jsp.ecommerce.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public  abstract class BaseEntity {
	@Column(name="Create_At")
	private LocalDateTime createAt;
	@Column(name="Upadate_At")
	private LocalDateTime udateAt;
	
	@PrePersist
	protected void onCreate() {
		createAt=LocalDateTime.now();
		udateAt =LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		udateAt=LocalDateTime.now();
		
	}
}
