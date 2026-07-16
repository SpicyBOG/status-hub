package com.statushub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.statushub.model.OverallStatus;
import com.statushub.model.ServiceStatus;
import com.statushub.model.StatusChange;
import com.statushub.model.TechnologyService;

class TechnologyServiceManagerTest {

	private TechnologyServiceManager manager;

	@BeforeEach
	void setUp() {
		manager = new TechnologyServiceManager();
	}

	@Test
	void initialOverallStatusShouldBePartialOutageWhenExactlyOneServiceIsDown() {
		OverallStatus overallStatus = manager.calculateOverallStatus();

		assertEquals(OverallStatus.PARTIAL_OUTAGE, overallStatus);
	}

	@Test
	void calculateOverallStatusShouldReturnOperationalWhenAllServicesAreOperational() {
		manager.updateServiceStatus(3L, ServiceStatus.OPERATIONAL);
		manager.updateServiceStatus(4L, ServiceStatus.OPERATIONAL);

		OverallStatus overallStatus = manager.calculateOverallStatus();

		assertEquals(OverallStatus.OPERATIONAL, overallStatus);
	}

	@Test
	void calculateOverallStatusShouldReturnDegradedWhenNoServiceIsDownButOneIsDegraded() {
		manager.updateServiceStatus(4L, ServiceStatus.OPERATIONAL);

		OverallStatus overallStatus = manager.calculateOverallStatus();

		assertEquals(OverallStatus.DEGRADED, overallStatus);
	}

	@Test
	void calculateOverallStatusShouldReturnPartialOutageWhenExactlyOneServiceIsDown() {
		manager.updateServiceStatus(4L, ServiceStatus.OPERATIONAL);
		manager.updateServiceStatus(1L, ServiceStatus.DOWN);

		OverallStatus overallStatus = manager.calculateOverallStatus();

		assertEquals(OverallStatus.PARTIAL_OUTAGE, overallStatus);
	}

	@Test
	void calculateOverallStatusShouldReturnCriticalOutageWhenTwoOrMoreServicesAreDown() {
		manager.updateServiceStatus(1L, ServiceStatus.DOWN);

		OverallStatus overallStatus = manager.calculateOverallStatus();

		assertEquals(OverallStatus.CRITICAL_OUTAGE, overallStatus);
	}

	@Test
	void updateServiceStatusShouldThrowIllegalArgumentExceptionWhenServiceIdDoesNotExist() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> manager.updateServiceStatus(99L, ServiceStatus.OPERATIONAL));

		assertTrue(exception.getMessage().contains("No existe un servicio"));
	}

	@Test
	void updateServiceStatusShouldChangeStatusAndCreateHistoryEntryWithExpectedDetails() {
		String message = "mantenimiento programado";

		TechnologyService updatedService = manager.updateServiceStatus(
				4L,
				ServiceStatus.OPERATIONAL,
				message);

		List<StatusChange> history = manager.getHistory();

		assertEquals(ServiceStatus.OPERATIONAL, updatedService.getStatus());
		assertEquals(1, history.size());

		StatusChange change = history.get(0);
		assertEquals(4L, change.getServiceId());
		assertEquals("Notificaciones", change.getServiceName());
		assertEquals(ServiceStatus.DOWN, change.getPreviousStatus());
		assertEquals(ServiceStatus.OPERATIONAL, change.getNewStatus());
		assertEquals(message, change.getMessage());
	}

	@Test
	void updateServiceStatusShouldNotCreateHistoryOrChangeUpdatedAtWhenStatusRemainsTheSame() {
		TechnologyService service = manager.findById(1L).orElseThrow();
		LocalDateTime originalUpdatedAt = service.getUpdatedAt();
		int historySizeBefore = manager.getHistory().size();

		TechnologyService result = manager.updateServiceStatus(
				1L,
				ServiceStatus.OPERATIONAL,
				"sin cambio real");

		assertSame(service, result);
		assertEquals(ServiceStatus.OPERATIONAL, result.getStatus());
		assertEquals(originalUpdatedAt, result.getUpdatedAt());
		assertEquals(historySizeBefore, manager.getHistory().size());
	}

	@Test
	void getAllServicesShouldReturnFourServicesInitially() {
		List<TechnologyService> services = manager.getAllServices();

		assertEquals(4, services.size());
	}
}
