package com.statushub.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.statushub.dto.UpdateServiceStatusRequest;
import com.statushub.model.TechnologyService;
import com.statushub.service.TechnologyServiceManager;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TechnologyServiceController {

	private final TechnologyServiceManager technologyServiceManager;

	public TechnologyServiceController(TechnologyServiceManager technologyServiceManager) {
		this.technologyServiceManager = technologyServiceManager;
	}

	@GetMapping("/services")
	public List<TechnologyService> getAllServices() {
		return technologyServiceManager.getAllServices();
	}

	@GetMapping("/status")
	public Map<String, Object> getOverallStatus() {
		return Map.of("overallStatus", technologyServiceManager.calculateOverallStatus());
	}

	@PutMapping("/services/{id}/status")
	public TechnologyService updateServiceStatus(
			@PathVariable Long id,
			@Valid @RequestBody UpdateServiceStatusRequest request) {
		try {
			return technologyServiceManager.updateServiceStatus(id, request.status());
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}
}
