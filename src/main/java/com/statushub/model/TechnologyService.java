package com.statushub.model;

import java.time.LocalDateTime;

public class TechnologyService {

	private Long id;
	private String name;
	private String description;
	private ServiceStatus status;
	private LocalDateTime updatedAt;

	public TechnologyService() {
	}

	public TechnologyService(Long id, String name, String description, ServiceStatus status, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = status;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void updateStatus(ServiceStatus status) {
		this.status = status;
		this.updatedAt = LocalDateTime.now();
	}
}
