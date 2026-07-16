package com.statushub.dto;

import jakarta.validation.constraints.NotNull;

import com.statushub.model.ServiceStatus;

public record UpdateServiceStatusRequest(
		@NotNull ServiceStatus status
) {
}
