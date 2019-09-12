package com.multicert.project.v2x.pkimanager.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Key;
import com.multicert.project.v2x.pkimanager.service.CaService;
import com.multicert.project.v2x.pkimanager.service.KeyManagementService;


@Controller
public class KeyManagementController {

	@Autowired
	private KeyManagementService keyManagementService;
	@Autowired
	private CaService caManagementService;



	@RequestMapping(value="/admin/key", method = RequestMethod.GET)
	public ModelAndView showCAs(){
		ModelAndView modelAndView = new ModelAndView();

		List<Key> keys = keyManagementService.getAllKeys();
		List<CA> cas = caManagementService.getAllCas();
		Key keyObj = new Key();
		
		modelAndView.addObject("keyObj", keyObj);
		modelAndView.addObject("cas", cas);
		modelAndView.addObject("keys",keys);
		modelAndView.addObject("allAlgorithms", getAlgorithms());
		modelAndView.setViewName("admin/key");
		
		return modelAndView;
	}
	
	//TODO falta meter a trcar no alias da chave no keystore
	@RequestMapping(value = "/admin/editkey", method = RequestMethod.POST)
	public String editKey(@RequestParam("keyId") Long keyId, @RequestParam("alias") String alias, final RedirectAttributes ra) {
		
	
		Key currentKey = keyManagementService.getKeyById(keyId);
		
		if(currentKey == null) {
			ra.addFlashAttribute("message", "Specified key does not exist");
			ra.addFlashAttribute("type", "danger");
			
			return "redirect:/admin/key";
		}
		
		currentKey.setAlias(alias);
		
		try {
			keyManagementService.changeKey(currentKey); // TODO implement here

			ra.addFlashAttribute("message", "CA data updated");
			ra.addFlashAttribute("type", "success");

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	
		return "redirect:/admin/key";
	}

	@RequestMapping(value = "/admin/addkey", method = RequestMethod.POST)
	public String addKey(@Valid Key key, BindingResult bindingResult, final RedirectAttributes ra) throws Exception {
		
		Key currentKey = keyManagementService.getKeyById(key.getKeyId());
		if(currentKey != null) {
			ra.addFlashAttribute("message", "Key already defined");
			ra.addFlashAttribute("type", "danger");
		} else {
			
			if(bindingResult.hasErrors()){
				StringBuilder msgBuildr = new StringBuilder();

				for(ObjectError e : bindingResult.getAllErrors()){
					msgBuildr.append(e.toString()).append("\n "); 
				}

				ra.addFlashAttribute("message", msgBuildr.toString());
				ra.addFlashAttribute("type", "danger");
			} else {
				keyManagementService.saveKey(key);
				ra.addFlashAttribute("message", "Key successfully added");
				ra.addFlashAttribute("type", "success");
			} 
		}
		
		return "redirect:/admin/key";
	}
	
	
	/**
	 * Help method to display the possible algorithms for the keys
	 */
	private List<String> getAlgorithms(){
		List<String> algorithms = new ArrayList<>();
		algorithms.add("ECIES-Nist");
		algorithms.add("ECIES-Brainpool");
		algorithms.add("ECDSA-Nist");
		algorithms.add("ECDSA-Brainpool");
		return algorithms;
	}
	
	

}
