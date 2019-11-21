package com.multicert.project.v2x.pkimanager.service;

import java.security.PublicKey;
import java.util.List;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.CAresponse;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.model.Request;
import com.multicert.project.v2x.pkimanager.model.Response;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

public interface CaService {

	public void saveOrUpdateCaData(CA ca);
	
	public CA getCaById(Long caId);
	
	public CA getCaByName(String caName);
		
	public List<CA> getAllCas();
	
	/**
	 * Method that gets the  valid RootCA (has keys and certificate associated)
	 * @return RootCA, if the CA exists and is ready, null otherwise
	 */
	public CA getRoot();
	
	/**
	 * Method that returns true if a root CA exists even if not yet valid
	 * @return
	 */
	public Boolean rootExists();
	
	/**
	 * Method that returns the valid subCAs 
	 * @param caType, the type of subCa we are looking "Enrollment" or "Authorization"
	 * @return
	 */
	List<CA> getValidSubCas(String caType);
	
	/**
	 * This method filters the list of possible subject CAs (CAs with encryption keys associated) and returns only the ones which don't have already a certificate associated
	 * A CA is considered  a valid subject subject if it does have an encryption key and not a subject of any certificate. 
	 */
	public List<CA> getValidSubjects(String ca_group);

	public List<CA> getValidIssuers();
	
	/**
	 * Help method that verifies if a CA exists, has signing keys and a certificate associated
	 * @param caName
	 * @return true if the CA is ready, false if not
	 */
	public boolean isReady(String caName);
	
	public void deletePKI();
	
	/**
	 * Method that validates an enrollment request.
	 *
	 * @param encodedEcRequest the request to validate
	 * @param profile, vehicle profile information 
	 * @param canonicalKey the vehicle's public canonical key
	 * @param caName the name of the destination CA
	 * @return 
	 * @throws IncorrectRecipientException 
	 * @throws Exception 
	 */
	CAresponse validateEcRequest(byte[] encodedEcRequest, String profile, PublicKey canonicalKey, String caName) throws IncorrectRecipientException, Exception;
	
	/**
	 * Method that validates an authorization request.
	 *
	 * @param encodedAtRequest the request to validate
	 * @param profile, vehicle profile information 
	 * @param caName the name of the destination CA
	 * @return 
	 * @throws IncorrectRecipientException 
	 * @throws Exception 
	 */
	CAresponse validateAtRequest(byte[] encodedAtRequest, String profile, String caName, boolean requestVerification) throws IncorrectRecipientException, Exception;




	

	

			
		

}
