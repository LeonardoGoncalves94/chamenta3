package com.multicert.project.v2x.pkimanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.repository.RegionRepository;

/**
 *Simple converter class to convert a string containing the id of a Region into the Region object present on the database
 *This class is used by spring. For example, in certificate.html to populate the Region field of the Certificate object 
 */
@Component
public class RegionConverter implements Converter <String, Region>
{

	@Autowired
	private RegionRepository regionRepository;

	@Override
	public Region convert(String regionId) {
		
		return regionRepository.findByregionId(Long.parseLong(regionId));
	}


}
