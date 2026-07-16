package com.statushub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.statushub.model.ServiceStatus;
import com.statushub.model.StatusChange;
import com.statushub.model.TechnologyService;
import com.statushub.service.TechnologyServiceManager;

@Controller
public class HomeController {

	private final TechnologyServiceManager technologyServiceManager;

	public HomeController(TechnologyServiceManager technologyServiceManager) {
		this.technologyServiceManager = technologyServiceManager;
	}

	@GetMapping("/")
	public String home(Model model) {
		List<TechnologyService> services = technologyServiceManager.getAllServices();

		long operationalCount = services.stream()
				.filter(service -> service.getStatus() == ServiceStatus.OPERATIONAL)
				.count();
		long degradedCount = services.stream()
				.filter(service -> service.getStatus() == ServiceStatus.DEGRADED)
				.count();
		long downCount = services.stream()
				.filter(service -> service.getStatus() == ServiceStatus.DOWN)
				.count();

		List<StatusChange> recentHistory = technologyServiceManager.getHistory().stream()
				.limit(5)
				.toList();

		model.addAttribute("services", services);
		model.addAttribute("overallStatus", technologyServiceManager.calculateOverallStatus());
		model.addAttribute("totalServices", services.size());
		model.addAttribute("operationalCount", operationalCount);
		model.addAttribute("degradedCount", degradedCount);
		model.addAttribute("downCount", downCount);
		model.addAttribute("recentHistory", recentHistory);
		model.addAttribute("generatedAt", LocalDateTime.now());

		return "index";
	}
}
