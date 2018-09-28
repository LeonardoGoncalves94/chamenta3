package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.EnrollmentCredential;
import com.multicert.project.v2x.pkimanager.model.Key;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;
import com.multicert.project.v2x.pkimanager.repository.CaRepository;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.DecryptionException;
import com.multicert.v2x.cryptography.ImcompleteRequestException;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.cryptography.InvalidSignatureException;
import com.multicert.v2x.cryptography.UnknownItsException;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationResponseCode;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationValidationRequest;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.EnrollmentResonseCode;
import com.multicert.v2x.datastructures.message.encrypteddata.RecipientInfo;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.generators.message.SecuredDataGenerator;

@Service("caManagementService")
public class CaServiceImpl implements CaService {

	@Autowired
	private CaRepository caRepository;
	@Autowired
	private V2XService v2xService;
	@Autowired
	private EnrollCredentialService enrollCredentialService;
	@Autowired
	private CertManagementService CAcertService;
	
	public void saveOrUpdateCaData(CA ca){

		CA storedCa = this.getCaById(ca.getCaId());

		if(storedCa != null){
			storedCa.setCaName(ca.getCaName());
			storedCa.setCaCountry(ca.getCaCountry());
			storedCa.setCaType(ca.getCaType());
			storedCa.setKeys(ca.getKeys());
		}else {
			storedCa = ca;
		}

		caRepository.save(storedCa);
	}

	public CA getCaById(Long caId){ 
		return caRepository.findBycaId(caId);
	}
	
	public CA getCaByName(String caName){ 
		return caRepository.findBycaName(caName);
	}

	@Override
	public List<CA> getAllCas() {
		return caRepository.findAll();
	}

	@Override
	public void deleteCa(Long caId) {
		caRepository.delete(caId);	
	}
	
	
	@Override
	public List<CA> getValidSubCas(String caType) 
	{
		List<CA> subCAs = caRepository.findBycaType(caType);
		List<CA> validSubCAs = new ArrayList<CA>();
		
		for(CA ca : subCAs) 
		{
			if(isReady(ca.getCaName())) 
			{
				validSubCAs.add(ca);
			}
		}
		return validSubCAs;
	}
	
	@Override
	public CA getRoot() {
		List<CA> root = caRepository.findBycaType("Root");
		if((root.size() > 0 ))
		{
			CA rootCA = root.get(0);
			if(isReady(rootCA.getCaName()))
			{
				return rootCA;
			}
		}
		return null;
	}
	
	@Override
	public Boolean rootExists() {
		List<CA> root = caRepository.findBycaType("Root");
		if(root.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	
	@Override
	public EtsiTs103097Data validateEcRequest(byte[] encryptedEcRequest, String profile, PublicKey canonicalKey, String caName)throws Exception{
			
		CA destCa = getCaByName(caName);
		Certificate destinationCertificate = destCa.getCertificate();
		Key encKeyPair = destCa.getEncryptionKey();
		Key sigKeyPair = destCa.getSignatureKey();
		
		SecuredDataGenerator dataGenerator = v2xService.initDataGenerator();
		
		EtsiTs103097Data signedRequest = v2xService.decryptRequest(dataGenerator, encryptedEcRequest, destinationCertificate, encKeyPair);	
		
		EnrollmentResonseCode responseCode = v2xService.verifyEcRequest(dataGenerator, signedRequest, canonicalKey);
		
		return v2xService.genEcResponse(dataGenerator, signedRequest, profile, responseCode, destinationCertificate, sigKeyPair, destCa);		
	}
	
	@Override
	public EtsiTs103097Data validateAtRequest(byte[] encodedAtRequest, String profile, String caName,
			boolean requestVerification) throws Exception {
		
		CA destCa = getCaByName(caName);
		Certificate destinationCertificate = destCa.getCertificate();
		Key encKeyPair = destCa.getEncryptionKey();
		Key sigKeyPair = destCa.getSignatureKey();
		
		SecuredDataGenerator dataGenerator = v2xService.initDataGenerator();
		
		EtsiTs103097Data signedRequest = v2xService.decryptRequest(dataGenerator, encodedAtRequest, destinationCertificate, encKeyPair);
		
		AuthorizationResponseCode responseCode = v2xService.verifyAtRequest(dataGenerator, signedRequest);
		
		if(responseCode == AuthorizationResponseCode.OK) 
		{
			AuthorizationValidationRequest authorizationValidation = new AuthorizationValidationRequest(dataGenerator.getSharedAtRequest(),dataGenerator.getEcSignature());
		
			if(requestVerification) // if this AT requests need enrollment verification
			{
				byte[] eaHashedId = dataGenerator.getSharedAtRequest().getEaId(); //get the hashed id of the EA that will validate vehicle enrollment from the AtRequest (sharedAtRequest)
					
				String EAname = CAcertService.getCertByhashedId(eaHashedId).getSubject().getCaName();	
				
				int validationResult = validateEnrollment(authorizationValidation,EAname); // send an enrollment validation request to the EA
				
				if(validationResult != 0)
				{
					responseCode = AuthorizationResponseCode.ENROLLMENT_VERIFICATION_FAILED;
				}
				
			}	
		}
		
		return v2xService.genAtResponse(dataGenerator, signedRequest, profile, responseCode, destinationCertificate, sigKeyPair, destCa);	
		
	}
	/**
	 * Method that validates the enrollment of a vehicle
	 * @param authorizationValidation the authorization validation request
	 * @param EAname the name of the Ea that will perform the enrollment verification
	 * @return 1 as a negative response, 0 as a positive response
	 * @throws BadContentTypeException if the request is badly formed
	 * @throws InvalidSignatureException if the signature does not verify
	 */
	private int validateEnrollment(AuthorizationValidationRequest authorizationValidation, String EAname)
	{
		SecuredDataGenerator dataGenerator = v2xService.initDataGenerator();
		
		try {
			byte[] ecId = v2xService.getEnrollmentCertId(dataGenerator, authorizationValidation); //gets the signerID of the enrollment credential that signed the request
			EnrollmentCredential enrollCredential = enrollCredentialService.getCertById(ecId);
			EtsiTs103097Certificate etsiCredential;
			EtsiTs103097Certificate eaCertificate;
			if(enrollCredential != null) //If the enrollment credential is in the database
			{
				etsiCredential = new EtsiTs103097Certificate(enrollCredential.getEncoded());
				eaCertificate =  new EtsiTs103097Certificate(caRepository.findBycaName(EAname).getCertificate().getEncoded());
		
				if(!enrollCredential.getrevoked()) // if the credential is not on the blacklist
				{
					if(v2xService.verifyCertificate(etsiCredential, eaCertificate))// if the credential was issued by the EA that the original AtRequest references
					{
						v2xService.verifyEnrollmentSignature(dataGenerator, etsiCredential); //if the signature does not very, throws InvalidSignatureException
						return 0; //positive response
					}			
				}
			}
		
			return 1;  
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}
		
	/**
	 * Help method that verifies if a CA exists, has keys and a certificate associated
	 * @param caName
	 * @return true if the CA is ready, false if not
	 */
	public boolean isReady(String caName) 
	{
		CA ca = getCaByName(caName);
		if(ca == null) 
		{
			return false;
		}
		
		if(ca.getEncryptionKey() != null && ca.getCertificate() != null && ca.getSignatureKey() != null)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * This method filters the list of possible subject CAs (CAs with encryption keys associated) and returns only the ones which don't have already a certificate associated
	 * A CA is considered  a valid subject subject if it does have an encryption key and not a subject of any certificate. 
	 */
	@Override
	public List<CA> getValidSubjects(String ca_group) {
		List <CA> subjects = caRepository.findSubjects(ca_group);
		List <CA> validSubjects = new ArrayList<CA>();
		
		for(CA ca : subjects) {
			if(ca.getCertificate() == null) {
				if(!validSubjects.contains(ca)) //avoid duplicates
				validSubjects.add(ca);
			}
		}
		
		return validSubjects;
		
	}
	
	@Override
	public List<CA> getValidIssuers() {
		return caRepository.findIssuers();
	}

	
}
