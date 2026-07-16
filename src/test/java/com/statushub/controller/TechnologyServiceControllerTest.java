package com.statushub.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.statushub.model.OverallStatus;
import com.statushub.model.ServiceStatus;
import com.statushub.model.TechnologyService;
import com.statushub.service.TechnologyServiceManager;

@WebMvcTest({ HealthController.class, TechnologyServiceController.class })
class TechnologyServiceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TechnologyServiceManager technologyServiceManager;

	@Test
	void getHealthShouldReturnUpStatusAndApplicationName() throws Exception {
		mockMvc.perform(get("/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"))
				.andExpect(jsonPath("$.application").value("StatusHub"));
	}

	@Test
	void getServicesShouldReturnFourServicesAsJson() throws Exception {
		LocalDateTime now = LocalDateTime.of(2026, 7, 16, 13, 0);
		List<TechnologyService> services = List.of(
				new TechnologyService(1L, "API principal", "Desc 1", ServiceStatus.OPERATIONAL, now),
				new TechnologyService(2L, "Base de datos", "Desc 2", ServiceStatus.OPERATIONAL, now),
				new TechnologyService(3L, "Servicio de pagos", "Desc 3", ServiceStatus.DEGRADED, now),
				new TechnologyService(4L, "Notificaciones", "Desc 4", ServiceStatus.DOWN, now));

		when(technologyServiceManager.getAllServices()).thenReturn(services);

		mockMvc.perform(get("/api/services"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("API principal"))
				.andExpect(jsonPath("$[0].status").value("OPERATIONAL"));
	}

	@Test
	void getStatusShouldReturnPartialOutage() throws Exception {
		when(technologyServiceManager.calculateOverallStatus()).thenReturn(OverallStatus.PARTIAL_OUTAGE);

		mockMvc.perform(get("/api/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.overallStatus").value("PARTIAL_OUTAGE"));
	}

	@Test
	void putServiceStatusShouldUpdateServiceAndReturnUpdatedResource() throws Exception {
		LocalDateTime now = LocalDateTime.of(2026, 7, 16, 13, 0);
		TechnologyService updatedService = new TechnologyService(
				4L,
				"Notificaciones",
				"Envía alertas y comunicaciones del sistema.",
				ServiceStatus.OPERATIONAL,
				now);

		when(technologyServiceManager.updateServiceStatus(4L, ServiceStatus.OPERATIONAL))
				.thenReturn(updatedService);

		mockMvc.perform(put("/api/services/4/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "status": "OPERATIONAL"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(4))
				.andExpect(jsonPath("$.status").value("OPERATIONAL"));

		verify(technologyServiceManager).updateServiceStatus(eq(4L), eq(ServiceStatus.OPERATIONAL));
	}

	@Test
	void putServiceStatusShouldReturnNotFoundWhenServiceDoesNotExist() throws Exception {
		when(technologyServiceManager.updateServiceStatus(99L, ServiceStatus.OPERATIONAL))
				.thenThrow(new IllegalArgumentException("No existe un servicio con ID: 99"));

		mockMvc.perform(put("/api/services/99/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "status": "OPERATIONAL"
								}
								"""))
				.andExpect(status().isNotFound());
	}

	@Test
	void putServiceStatusShouldReturnBadRequestWhenStatusIsNull() throws Exception {
		mockMvc.perform(put("/api/services/4/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(technologyServiceManager);
	}

	@Test
	void putServiceStatusShouldReturnBadRequestWhenStatusIsInvalid() throws Exception {
		mockMvc.perform(put("/api/services/4/status")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "status": "INVENTADO"
								}
								"""))
				.andExpect(status().isBadRequest());

		verify(technologyServiceManager, never()).updateServiceStatus(eq(4L), eq(ServiceStatus.OPERATIONAL));
		verifyNoInteractions(technologyServiceManager);
	}
}
