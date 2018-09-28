package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.SecretKey;

import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.Key;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.v2x.IdentifiedRegions.Countries.CountryTypes;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.ImcompleteRequestException;
import com.multicert.v2x.cryptography.InvalidSignatureException;
import com.multicert.v2x.datastructures.base.CountryOnly;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationResponseCode;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationValidationRequest;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.EnrollmentResonseCode;
import com.multicert.v2x.datastructures.message.encrypteddata.RecipientInfo;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.generators.message.SecuredDataGenerator;

/**
 * Interface that contains all the methods of the v2x package that will be used by this webapp
 *
 */
public interface V2XService {

	/**
	 *Method that generates an elliptic curve key pair (can be used for signature and encryption)
	 * @param alias the desired alias
	 * @param algorithm the algorithm of the key pair, the possible types are present in the Signature.SignatureTypes Enum
	 * @return 
	 */
	KeyPair genKeyPair(String algorithm) throws Exception;
	
	
	/**
	 * Method that generates a certificate for a Root CA
	 * @return 
	 * @throws IOException 
	 * @throws Exception 
	 */
	byte[] genRootCertificate(Certificate rootCertificate) throws IOException, Exception;
	
	/**
	 * Method that generates a certificate for a Sub CA
	 * @return 
	 * @throws Exception 
	 */
	byte[] genSubCertificate(Certificate subCertificate) throws Exception;
	
	/**
	 * method that initializes a data generator to be used in the enrollment process of a vehicle
	 * The enrollment process starts with the decryption of the enrollment request, then verification of the signature and ends with the generation of a response
	 */
	SecuredDataGenerator initDataGenerator();
	
	/**
	 * Method that decrypts the enrollment request
	 * @param securedDataGenerator, the data generator associated with this vehicle's enrollment
	 * @param encryptedRequest, the encrypted and encoded enrollment credential request
	 * @param destinationCertificate, the certificate of the destination enrollment authority
	 * @param decriptionPair, the decryption keypair that belongs to the destination CA
	 * @returns the decrypted request (a.k.a signed request)
	 * @throws Exception if can't decrypt 
	 */
	EtsiTs103097Data decryptRequest(SecuredDataGenerator securedDataGenerator, byte[] encryptedRequest,
			Certificate destinationCertificate, Key decriptionKeys) throws Exception;
	
	/**
	 * Method that verifies the signature on a enrollment request
	 * @param securedDataGenerator, the data generator associated with this vehicle's enrollment
	 * @param decryptedRequest, the decrypted request to verify the signature
	 * @param canonicalKey, the canonical public key that belongs to the vehicle (to verify the signature with)
	 * @return A response code as a result of the signature verification
	 */
	EnrollmentResonseCode verifyEcRequest(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data decryptedRequest,
			PublicKey canonicalKey) throws IOException;
	/**
	 * Method that generates an enrollment response to the vehicle 
	 * @param securedDataGenerator, the data generator associated with this vehicle's enrollment
	 * @param signedRequest, the decrypted request
	 * @param profile, a reference to the vehicle's profile which contains subject attributes to go in the enrollment credential
	 * @param responseCode, the response code 
	 * @param EAcertificate, the certificate of the EA that is responding
	 * @param signatureKeys, the signing keys of the EA, to sign the response
	 * @param signingAlgorithm, the algorithm to be used in the signature
	 * @param destCa,the enrollment authority database info
	 * @return a positive response with the enrollment credential or negative response without it
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	EtsiTs103097Data genEcResponse(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data signedRequest, String profile,
			EnrollmentResonseCode responseCode, Certificate EAcertificate, Key signatureKeys, CA EA)
			throws IOException, GeneralSecurityException, Exception;

	/**
	 * Method to extract the PublicKey from the PublicVerificationKey structure
	 * @throws InvalidKeySpecException 
	 */
	PublicKey extractPublicKey(PublicVerificationKey verificationKey) throws InvalidKeySpecException;

	/**
	 * method to generate an certificate hashed id with the SHA-256 algorithm
	 */
	byte[] genCertificateHashedId(EtsiTs103097Certificate certificate) throws NoSuchAlgorithmException, IOException;

	/**
	 * Method used by the target AA to verify the proof of possession (POP) signature of an authorization request
	 *
	 * @param securedDataGenerator
	 * @param decryptedRequest
	 * @return
	 * @throws IOException
	 * @throws InvalidSignatureException 
	 * @throws ImcompleteRequestException 
	 * @throws BadContentTypeException 
	 * @throws InvalidKeySpecException 
	 * @throws SignatureException 
	 */
	AuthorizationResponseCode verifyAtRequest(SecuredDataGenerator securedDataGenerator,
			EtsiTs103097Data decryptedRequest) throws IOException, SignatureException, InvalidKeySpecException, BadContentTypeException, ImcompleteRequestException, InvalidSignatureException;

	/**
	 * Method that gets the hashedId of the enrollment credential from AuthorizationValidationRequest
	 */
	 public byte[] getEnrollmentCertId(SecuredDataGenerator securedDataGenerator, AuthorizationValidationRequest authorizationValidationRequest) throws BadContentTypeException;
	 
	/**
	 * Method used by the EA to verify a signature done with the vehicle's enrollment credential signing key
	 * @throws BadContentTypeException, if the enrollment verification request is badly formed
	 * @throws InvalidSignatureException, if the signature does not verify
	*/
	void verifyEnrollmentSignature(SecuredDataGenerator securedDataGenerator,EtsiTs103097Certificate enrollmentCredential) throws InvalidSignatureException, BadContentTypeException;

	 /** Method that verifies the signature of a certificate using the public key of the signing certificate
	  */
	Boolean verifyCertificate(EtsiTs103097Certificate certificate, EtsiTs103097Certificate signerCertificate);


	EtsiTs103097Data genAtResponse(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data signedRequest,
			String profile, AuthorizationResponseCode responseCode, Certificate AAcertificate, Key signatureKeys, CA AA)
			throws Exception;



}
