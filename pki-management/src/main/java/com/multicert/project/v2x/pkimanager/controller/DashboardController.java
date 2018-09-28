package com.multicert.project.v2x.pkimanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.service.CaService;

@Controller
public class DashboardController {
	@Autowired
	private CaService caManagementService;
	
	@RequestMapping(value="/admin/dashboard", method = RequestMethod.GET)
	public ModelAndView showDashboard(){
		ModelAndView modelAndView = new ModelAndView();
	
		List <CA> cas = caManagementService.getAllCas();
	
		modelAndView.addObject("cas",cas);
		modelAndView.setViewName("admin/dashboard");
		return modelAndView;		
	}

}
