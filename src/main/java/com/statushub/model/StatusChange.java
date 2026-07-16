package com.statushub.model;

import java.time.LocalDateTime;

public class StatusChange {

	private Long id;
	private Long serviceId;
	private String serviceName;
	private ServiceStatus previousStatus;
	private ServiceStatus newStatus;
	private String message;
	private LocalDateTime changedAt;

	public StatusChange() {
	}

	public StatusChange(
			Long id,
			Long serviceId,
			String serviceName,
			ServiceStatus previousStatus,
			ServiceStatus newStatus,
			String message,
			LocalDateTime changedAt) {
		this.id = id;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.previousStatus = previousStatus;
		this.newStatus = newStatus;
		this.message = message;
		this.changedAt = changedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public ServiceStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(ServiceStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	public ServiceStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ServiceStatus newStatus) {
		this.newStatus = newStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}
}
