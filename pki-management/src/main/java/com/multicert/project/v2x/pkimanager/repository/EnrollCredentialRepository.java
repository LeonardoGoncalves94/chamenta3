package com.multicert.project.v2x.pkimanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.EnrollmentCredential;


@Repository("credentialRepository")
public interface EnrollCredentialRepository extends JpaRepository<EnrollmentCredential, byte[]>{
	
	public EnrollmentCredential findByhashedId(byte[] hashedId);
	
}
