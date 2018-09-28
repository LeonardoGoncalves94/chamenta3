package com.multicert.project.v2x.pkimanager.controller;

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
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.repository.RegionRepository;
import com.multicert.project.v2x.pkimanager.service.CaService;
import com.multicert.project.v2x.pkimanager.service.CertManagementService;
import com.multicert.project.v2x.pkimanager.service.V2XService;

import javax.validation.Valid;


@Controller
public class CertManagementController {

	@Autowired
	private CertManagementService certManagementService;
	@Autowired
	private CaService caManagementService;
	@Autowired
	private V2XService v2xService;
	@Autowired
	private RegionRepository regionRepository;
	
	@RequestMapping(value="/admin/certificate", method = RequestMethod.GET)
	public ModelAndView showCerts(){
		ModelAndView modelAndView = new ModelAndView();
		
		List<Certificate> certs = certManagementService.getAllCertificates();
		List <CA> rootSubjects = caManagementService.getValidSubjects("RootCa");
		List <CA> subSubjects = caManagementService.getValidSubjects("SubCa");
		List <CA> issuers = caManagementService.getValidIssuers();
		List <Region> countries = regionRepository.findAll();
		Certificate certificateObj = new Certificate();
		
		modelAndView.addObject("certificateObj",certificateObj);
		modelAndView.addObject("rootSubjects",rootSubjects);
		modelAndView.addObject("subSubjects",subSubjects);
		modelAndView.addObject("issuers",issuers);
		modelAndView.addObject("certs", certs);
		modelAndView.addObject("countries", countries);
		modelAndView.setViewName("admin/certificate");
		return modelAndView;
		
		
	}


	@RequestMapping(value = "/admin/addrootcert", method = RequestMethod.POST)
	public String addRootCert(@Valid Certificate certificate, BindingResult bindingResult, final RedirectAttributes ra) throws Exception {
		
		Certificate currentCert = certManagementService.getCertById(certificate.getCertId());
		
		if(currentCert != null){
			ra.addFlashAttribute("message", "Certificate already defined");
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
				
				certManagementService.saveRootCertificate(certificate);
				
				ra.addFlashAttribute("message", "Certificate successfully added");
				ra.addFlashAttribute("type", "success");
			}
		
		}

		return "redirect:/admin/certificate";
		
	}
	
	@RequestMapping(value = "/admin/addsubcert", method = RequestMethod.POST)
	public String addSubCert(@Valid Certificate certificate, BindingResult bindingResult, final RedirectAttributes ra) throws Exception {
		
	Certificate currentCert = certManagementService.getCertById(certificate.getCertId());
	CA issuer = caManagementService.getCaById(certificate.getIssuer().getCaId());
	CA subject = caManagementService.getCaById(certificate.getSubject().getCaId());
		
		if(currentCert != null)
		{
			ra.addFlashAttribute("message", "Certificate already defined");
			ra.addFlashAttribute("type", "danger");
		}
		else if (issuer == null)
		{
			ra.addFlashAttribute("message", "issuer Ca not found");
			ra.addFlashAttribute("type", "danger");
		} 
		else if(subject == null) 
		{
			ra.addFlashAttribute("message", "subject Ca not found");
			ra.addFlashAttribute("type", "danger");
		} 
		else if(bindingResult.hasErrors()) 
		{
			
			StringBuilder msgBuildr = new StringBuilder();
			
			for(ObjectError e : bindingResult.getAllErrors()){
				msgBuildr.append(e.toString()).append("\n "); 
			}
				ra.addFlashAttribute("message", msgBuildr.toString());
				ra.addFlashAttribute("type", "danger");
				
		} else {
				certManagementService.saveSubCertificate(certificate);

				ra.addFlashAttribute("message", "Certificate successfully added");
				ra.addFlashAttribute("type", "success");
		}
		
		return "redirect:/admin/certificate";
	}
		

}
