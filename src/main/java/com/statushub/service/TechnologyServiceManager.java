package com.statushub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.statushub.model.OverallStatus;
import com.statushub.model.ServiceStatus;
import com.statushub.model.StatusChange;
import com.statushub.model.TechnologyService;

@Service
public class TechnologyServiceManager {

	private final List<TechnologyService> services = new ArrayList<>();
	private final List<StatusChange> history = new ArrayList<>();
	private long nextHistoryId = 1L;

	public TechnologyServiceManager() {
		LocalDateTime now = LocalDateTime.now();

		services.add(new TechnologyService(
				1L,
				"API principal",
				"Gestiona las solicitudes principales de la plataforma.",
				ServiceStatus.OPERATIONAL,
				now));

		services.add(new TechnologyService(
				2L,
				"Base de datos",
				"Almacena la información operativa de StatusHub.",
				ServiceStatus.OPERATIONAL,
				now));

		services.add(new TechnologyService(
				3L,
				"Servicio de pagos",
				"Procesa las transacciones y validaciones de pago.",
				ServiceStatus.DEGRADED,
				now));

		services.add(new TechnologyService(
				4L,
				"Notificaciones",
				"Envía alertas y comunicaciones del sistema.",
				ServiceStatus.DOWN,
				now));
	}

	public List<TechnologyService> getAllServices() {
		return List.copyOf(services);
	}

	public Optional<TechnologyService> findById(Long id) {
		return services.stream()
				.filter(service -> service.getId().equals(id))
				.findFirst();
	}

	public TechnologyService updateServiceStatus(Long id, ServiceStatus newStatus) {
		return updateServiceStatus(id, newStatus, null);
	}

	public TechnologyService updateServiceStatus(Long id, ServiceStatus newStatus, String message) {
		TechnologyService service = findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No existe un servicio con ID: " + id));

		ServiceStatus previousStatus = service.getStatus();

		if (previousStatus == newStatus) {
			return service;
		}

		service.updateStatus(newStatus);

		StatusChange change = new StatusChange(
				nextHistoryId++,
				service.getId(),
				service.getName(),
				previousStatus,
				newStatus,
				message,
				LocalDateTime.now());

		history.add(0, change);
		return service;
	}

	public List<StatusChange> getHistory() {
		return List.copyOf(history);
	}

	public OverallStatus calculateOverallStatus() {
		long downCount = services.stream()
				.filter(service -> service.getStatus() == ServiceStatus.DOWN)
				.count();

		if (downCount >= 2) {
			return OverallStatus.CRITICAL_OUTAGE;
		}

		if (downCount == 1) {
			return OverallStatus.PARTIAL_OUTAGE;
		}

		boolean hasDegraded = services.stream()
				.anyMatch(service -> service.getStatus() == ServiceStatus.DEGRADED);

		if (hasDegraded) {
			return OverallStatus.DEGRADED;
		}

		return OverallStatus.OPERATIONAL;
	}
}
