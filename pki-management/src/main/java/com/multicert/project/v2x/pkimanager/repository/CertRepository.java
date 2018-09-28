package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;


@Repository("CertRepository")
public interface CertRepository extends JpaRepository<Certificate, Long>{
	
	public Certificate findBycertId(Long certId);
	
	public Certificate findByhashedId(byte[] hashedId);
	
}
