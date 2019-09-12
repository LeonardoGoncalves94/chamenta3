package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.Region;


@Repository("regionRepository")
public interface RegionRepository extends JpaRepository<Region, Long>{
	
	public Region findByregionId(Long regionId);
	
	public Region findByregionNumber(Long regionNumber);
	
	public Region findByregionName(String regionName);
	
}
