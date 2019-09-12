package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.EnrollmentCredential;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.repository.CertRepository;
import com.multicert.project.v2x.pkimanager.repository.EnrollCredentialRepository;

@Service("enrollcredentialService")
public class EnrollCredentialServiceImpl implements EnrollCredentialService {
	
	@Autowired
	EnrollCredentialRepository credentialRepository;

	public EnrollmentCredential getCertById(byte[] hashedId) 
	{
		return credentialRepository.findByhashedId(hashedId);
	}
	
	public List<EnrollmentCredential> getAllCertificates()
	{
		return credentialRepository.findAll();
	}
	
	public void deleteCertificate(byte[] hashedId)
	{
		
	}
	
	/**
	 * Method that creates a valid Sub CA certificate, and stores its information on the database
	 * @throws Exception 
	 */
	public void saveEnrollmentCredential(EnrollmentCredential credential)
	{
		credentialRepository.save(credential);
	}

	
}
