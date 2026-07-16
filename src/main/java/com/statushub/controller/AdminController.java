package com.statushub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.statushub.model.ServiceStatus;
import com.statushub.model.TechnologyService;
import com.statushub.service.TechnologyServiceManager;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final TechnologyServiceManager technologyServiceManager;

	public AdminController(TechnologyServiceManager technologyServiceManager) {
		this.technologyServiceManager = technologyServiceManager;
	}

	@GetMapping
	public String admin(Model model) {
		model.addAttribute("services", technologyServiceManager.getAllServices());
		model.addAttribute("statuses", ServiceStatus.values());
		model.addAttribute("overallStatus", technologyServiceManager.calculateOverallStatus());
		return "admin";
	}

	@PostMapping("/services/{id}/status")
	public String updateServiceStatus(
			@PathVariable Long id,
			@RequestParam ServiceStatus status,
			@RequestParam(required = false) String message,
			RedirectAttributes redirectAttributes) {
		try {
			TechnologyService updatedService = technologyServiceManager.updateServiceStatus(id, status, message);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Estado de \"" + updatedService.getName() + "\" actualizado correctamente.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin";
	}
}
