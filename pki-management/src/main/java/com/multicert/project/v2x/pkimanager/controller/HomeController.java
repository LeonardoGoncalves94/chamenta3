package com.multicert.project.v2x.pkimanager.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.multicert.project.v2x.pkimanager.model.User;
import com.multicert.project.v2x.pkimanager.service.UserService;


@Controller
public class HomeController {

	@Autowired
	private UserService userService;



	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView adminHome(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	
		User user = userService.findUserByEmail(auth.getName());
		//modelAndView.addObject("userName", user.getName() + " " + user.getLastName());

		modelAndView.addObject("userRole", user.getRoles().iterator().next().getRole());
		modelAndView.addObject("userName", user.getName());
		modelAndView.addObject("userLastName", user.getLastName());
		modelAndView.addObject("email", user.getEmail());
		modelAndView.addObject("adminPassword", user.getPassword());

		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/updateuser", method = RequestMethod.POST)
	public String updateUser(@RequestParam("name") String name, @RequestParam("lastName") String lastName,  @RequestParam("email") String email, @RequestParam("adminPassword") String adminPassword, final RedirectAttributes ra) {


		User user = userService.findUserByEmail(email);

		user.setPassword(null);
		
		if(name != null && !name.isEmpty()){
			user.setName(name);
		}
		if(lastName != null && !lastName.isEmpty()){
			user.setLastName(lastName);
		}
		if(email != null && !email.isEmpty()){
			user.setEmail(email);
		}
		if(adminPassword != null && !adminPassword.isEmpty()){
			user.setPassword(adminPassword);
		}

		try {
			userService.updateUser(user);
			ra.addFlashAttribute("message", "Creditor data deleted");
			ra.addFlashAttribute("type", "success");
		} catch (Exception e1) {
			ra.addFlashAttribute("message", e1.getMessage());
			ra.addFlashAttribute("type", "danger");
		}

		return "redirect:/admin/home";
	}









}
