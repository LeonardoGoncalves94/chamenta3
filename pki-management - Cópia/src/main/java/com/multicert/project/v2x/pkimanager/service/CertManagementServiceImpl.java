package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.repository.CertRepository;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;

@Service("CertManagementService")
public class CertManagementServiceImpl implements CertManagementService {

	@Autowired
	CertRepository certRepository;
	@Autowired
	V2XService v2xService;

	private void saveCertificateData(Certificate cert) {
		Certificate currentCert = this.getCertById(cert.getCertId());
		currentCert = cert;
		certRepository.save(currentCert);
	}

	@Override
	public Certificate getCertById(Long certId) {
		return certRepository.findBycertId(certId);
	}
	
	@Override
	public Certificate getCertByhashedId(byte[] hashedId) {
		return certRepository.findByhashedId(hashedId);
	}
	
	@Override
	public List<Certificate> getAllCertificates() {
		return certRepository.findAll();
	}

	@Override
	public void deleteCertificate(Long certID) {
		certRepository.delete(certID);	
	}	
	
	/**
	 * Method that generates a Root CA certificate (using v2xervice) and stores its information on the database
	 * @throws Exception 
	 */
	@Override
	public void saveRootCertificate(Certificate rootCertificate) throws Exception {
		
		// Generate a root certificate
		byte [] encoded = v2xService.genRootCertificate(rootCertificate);
		
		// set the certificate encoded value
		rootCertificate.setEncoded(encoded);
		rootCertificate.setHashedId(v2xService.genCertificateHashedId(decodeCertificate(encoded)));
		
		//Save the certificate in the database		
		saveCertificateData(rootCertificate);
		test(rootCertificate.getCertId());
		
	}
	
	public EtsiTs103097Certificate decodeCertificate(byte[] encodedCertificate) throws IOException
	{
		return new EtsiTs103097Certificate(encodedCertificate);
		
	}
	
	public void test(Long id) throws Exception {
		
		Certificate cert = getCertById(id);
		EtsiTs103097Certificate certificateBase = new EtsiTs103097Certificate(cert.getEncoded()); //decode the certificate
		System.out.println(certificateBase.toString());
		
	}
	
	/**
	 * Method that generates a Sub CA certificate (using v2xervice) and stores its information on the database
	 * @throws Exception 
	 */
	@Override
	public void saveSubCertificate(Certificate subCertificate)throws Exception {
		
		// Generate a root certificate
				byte [] encoded = v2xService.genSubCertificate(subCertificate);
				
				// set the certificate encoded value
				subCertificate.setEncoded(encoded);
				subCertificate.setHashedId(v2xService.genCertificateHashedId(decodeCertificate(encoded)));
				
				//Save the certificate in the database		
				saveCertificateData(subCertificate);
				test(subCertificate.getCertId());
		
	}
	
	

}
