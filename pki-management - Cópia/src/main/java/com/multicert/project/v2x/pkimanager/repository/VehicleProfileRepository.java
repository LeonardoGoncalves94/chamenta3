package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.Profile;
import com.multicert.project.v2x.pkimanager.model.Region;


@Repository("vehicleProfileRepository")
public interface VehicleProfileRepository extends JpaRepository<Profile, String>{
	
	public Profile findByVehicleType(int vehicleType);
	
	public Profile findByProfileName(String profileName);
}
