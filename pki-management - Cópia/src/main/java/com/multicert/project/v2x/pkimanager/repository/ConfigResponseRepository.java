package com.multicert.project.v2x.pkimanager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.ConfigResponse;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;


@Repository("configResponseRepository")
public interface ConfigResponseRepository extends JpaRepository<ConfigResponse, Long>{
	
	public ConfigResponse findByresponseId(Long responseId);
	
}
