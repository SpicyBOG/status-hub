package com.statushub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.statushub.service.TechnologyServiceManager;

@Controller
public class HistoryController {

	private final TechnologyServiceManager technologyServiceManager;

	public HistoryController(TechnologyServiceManager technologyServiceManager) {
		this.technologyServiceManager = technologyServiceManager;
	}

	@GetMapping("/history")
	public String history(Model model) {
		model.addAttribute("history", technologyServiceManager.getHistory());
		return "history";
	}
}
