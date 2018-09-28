package com.multicert.project.v2x.pkimanager.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.multicert.project.v2x.pkimanager.model.CreditorConf;
import com.multicert.project.v2x.pkimanager.model.User;
import com.multicert.project.v2x.pkimanager.service.CreditorManagementService;
import com.multicert.project.v2x.pkimanager.service.UserService;


@Controller
public class CreditorManagementController {

	@Autowired
	private UserService userService;

	@Autowired
	private CreditorManagementService creditorManagementService;



	@RequestMapping(value="/admin/creditor", method = RequestMethod.GET)
	public ModelAndView adminHome(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		List<CreditorConf> creditors = creditorManagementService.getAllCreditorsConf();

		User user = userService.findUserByEmail(auth.getName());
		//modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
		modelAndView.addObject("creditors",creditors);

		modelAndView.addObject("userRole", user.getRoles().iterator().next().getRole());
		modelAndView.addObject("userName", user.getName());
		modelAndView.addObject("userLastName", user.getLastName());
		modelAndView.addObject("email", user.getEmail());
		modelAndView.addObject("adminPassword", user.getPassword());

		modelAndView.setViewName("admin/creditor");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/editcreditor", method = RequestMethod.POST)
	public String editCreditor(@Valid CreditorConf creditor, final RedirectAttributes ra) {
		//ModelAndView modelAndView = new ModelAndView();

		CreditorConf currentCreditor = creditorManagementService.getCreditorConfByCreditorId(creditor.getCreditorId());

		currentCreditor.setCreditorAddress(creditor.getCreditorAddress());
		currentCreditor.setCreditorBankBic(creditor.getCreditorBankBic());
		currentCreditor.setCreditorCountry(creditor.getCreditorCountry());
		currentCreditor.setCreditorName(creditor.getCreditorName());

		try {
			creditorManagementService.saveOrUpdateCreditorConfData(currentCreditor);

			ra.addFlashAttribute("message", "Creditor data deleted");
			ra.addFlashAttribute("type", "success");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		modelAndView.addObject("successMessage", "Creditor data has been updated");
		//		modelAndView.addObject("user", new User());
		//		modelAndView.setViewName("admin/creditor");


		return "redirect:/admin/creditor";
	}

	@RequestMapping(value = "/admin/addcreditor", method = RequestMethod.POST)
	public String addCreditor(@Valid CreditorConf creditor, BindingResult bindingResult, final RedirectAttributes ra) {

		CreditorConf currentCreditor = creditorManagementService.getCreditorConfByCreditorId(creditor.getCreditorId());


		if (currentCreditor != null) {
			ra.addFlashAttribute("message", "CreditorId already defined");
			ra.addFlashAttribute("type", "danger");
		} else {


			if(bindingResult.hasErrors()){
				StringBuilder msgBuildr = new StringBuilder();

				for(ObjectError e : bindingResult.getAllErrors()){
					msgBuildr.append(e.toString()).append("\n "); 
				}

				ra.addFlashAttribute("message", msgBuildr.toString());
				ra.addFlashAttribute("type", "danger");

			}else {
				//			.rejectValue("email", "error.user",
				//					"There is already a user registered with the email provided");

				creditorManagementService.saveOrUpdateCreditorConfData(creditor);

				ra.addFlashAttribute("message", "Creditor successfully added");
				ra.addFlashAttribute("type", "success");
			}
		}


		return "redirect:/admin/creditor";
	}

	@RequestMapping(value = "/admin/deletecreditor", method = RequestMethod.POST)
	public String deleteCreditor(@RequestParam("creditorId") String id, final RedirectAttributes ra) {


		CreditorConf currentCreditor = creditorManagementService.getCreditorConfByCreditorId(id);

		if (currentCreditor != null) {
			creditorManagementService.deleteCreditor(id);
			ra.addFlashAttribute("message", "Creditor data deleted");
			ra.addFlashAttribute("type", "success");
		}else {
			ra.addFlashAttribute("message", "Creditor already deleted");
			ra.addFlashAttribute("type", "danger");
		}


		return "redirect:/admin/creditor";
	}

	@RequestMapping(value = "/admin/updateusers", method = RequestMethod.POST)
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

		return "redirect:/admin/creditor";
	}









}
