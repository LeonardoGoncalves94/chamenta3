package com.multicert.project.v2x.pkimanager.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.multicert.project.v2x.pkimanager.model.CA;
/**
 * Simple converter class to convert a string containing the id of a CA into the CA object present on the database
 * This class is used by spring. For example, in certificate.html to populate issuer and subject fields of the Certificate object 
 *
 */
@Component
public class CaConverter implements Converter <String, CA>
{

	
	@Autowired
	private CaService caManagementService;
	/**
	 * This method gets a CA from the database providing its id 
	 */
	@Override
	public CA convert(String caId) {
	
        return caManagementService.getCaById(Long.parseLong(caId));


	}


}
