package com.multicert.project.v2x.pkimanager.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;


@Repository("responseRepository")
public interface ResponseRepository extends JpaRepository<Response, Long>{
	
	public Response findByresponseId(Long responseId);
	
}
