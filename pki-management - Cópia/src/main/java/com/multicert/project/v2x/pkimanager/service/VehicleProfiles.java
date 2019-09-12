package com.multicert.project.v2x.pkimanager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.repository.RegionRepository;
import com.multicert.v2x.datastructures.base.PsidSsp;


/**
 * This class lists in a hash map the available vehicle's profile
 * A profile is built for a specific type of vehicle (e.g. and ambulance, truck etc..) and contains its enrollment time and region profile
 */
@Service("VehicleProfileService")
public class VehicleProfiles {
	
	@Autowired
	private RegionRepository regionRepository; //the country table to select the region validity of the profiles
	
	public Map<String, Profile> vehicleProfiles = new HashMap<String, Profile>();
		
	/**
	 * method to initialize all the available profiles
	 * @return 
	 */
	public void init()
	{
		Profile profile1 = new Profile(3, regionRepository.findAll(),2,2); //profile with  region validity set to all of the available countries
		vehicleProfiles.put("profile1", profile1);
	}
	
	public Profile getProfile(String name)
	{
		return vehicleProfiles.get(name);
	}
	
	public class Profile
	{	
		
		private int enrollmentPeriod;
		private List <Region> countries; 
		private int assuranceLevel;
		private int confidenceLevel;
		
		/**
		 * Main constructor to build a vehicle profile
		 * @param enrollmentPeriod, the enrollment period of this vehicle profile
		 * @param countries, the region validity of this vehicle profile
		 */
		public Profile(int enrollmentPeriod, List <Region> countries,int assuranceLevel, int confidenceLevel)
		{
			this.enrollmentPeriod = enrollmentPeriod;
			this.countries = countries;
			this.assuranceLevel = assuranceLevel;
			this.confidenceLevel = confidenceLevel;
			
			
		}
		
		/**
		 * Main constructor to build a vehicle profile with one country validity
		 * @param enrollmentPeriod, the enrollment period of this vehicle profile
		 * @param countries, the region validity of this vehicle profile
		 */
		public Profile(int enrollmentPeriod, Region country)
		{
			this.enrollmentPeriod = enrollmentPeriod;
			countries.add(country);
			
		}
		
		public int getEnrollmentPeriod()
		{
			return this.enrollmentPeriod;
		}
		
		public List <Region> getCountries()
		{
			return this.countries;
		}
		
	}
}
