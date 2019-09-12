package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.util.List;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.EnrollmentCredential;
import com.multicert.project.v2x.pkimanager.model.Region;

public interface EnrollCredentialService {

	public EnrollmentCredential getCertById(byte[] hashedId);
	
	public List<EnrollmentCredential> getAllCertificates();
	
	public void deleteCertificate(byte[] hashedId);
	
	public void saveEnrollmentCredential(EnrollmentCredential certificate);

	
}
