package com.multicert.project.v2x.pkimanager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.Request;


@Repository("requestRepository")
public interface RequestRepository extends JpaRepository<Request, Long>{
	
	public Request findByrequestId(Long requestId);
	
}
