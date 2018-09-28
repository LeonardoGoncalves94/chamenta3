package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.util.List;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.Region;

public interface CertManagementService {

	public Certificate getCertById(Long certId);
	
	public List<Certificate> getAllCertificates();
	
	public void deleteCertificate(Long certID);
	
	/**
	 * Method that generates a valid Root CA certificate, and stores its infomation on the database
	 * @throws IOException 
	 * @throws Exception 
	 */
	public void saveRootCertificate(Certificate rootCertificate) throws IOException, Exception;
	/**
	 * Method that creates a valid Sub CA certificate, and stores its infomation on the database
	 * @throws Exception 
	 */
	public void saveSubCertificate(Certificate subCertificate) throws Exception;
	
	/**
	 * Method to find a certificate by its hashId (calculated over the encoded EtsiTs103097 certificate)
	 * @param hashedId
	 * @return
	 */
	Certificate getCertByhashedId(byte[] hashedId);

	
}
