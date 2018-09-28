package com.multicert.project.v2x.client;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.SecretKey;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.DecryptionException;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.datastructures.base.GeographicRegion;
import com.multicert.v2x.datastructures.base.HashedId3;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.base.SequenceOfHashedId3;
import com.multicert.v2x.datastructures.base.ValidityPeriod;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.SignerIdentifier;

/**
 * Interface with the v2x package
 *
 */
public interface V2X {
	/**
	 * Method that generates an elliptic curve key pair
	 * @param alg the algorithm of the key, the possible types are present in the Signature.SignatureTypes Enum
	 * @return
	 * @throws Exception
	 */
	public KeyPair genKeyPair(AlgorithmType alg) throws Exception;
	/**
	 * Method that generates a symmetric key to encrypt the EcRequest and be shares with the enrollment authority
	 * @param alg the algorithm of the key, the possible types are present in the SymmAlgorithm Enum
	 * @return
	 * @throws Exception
	 */
	public SecretKey genSecretKey (AlgorithmType alg) throws Exception;
	
	/**
	 * Method that wraps an elliptic curve generated key into a PublicVerificationKey structure (contains the curve point and algorithm)
	 * @param publicKey the public key to wrap
	 * @param alg the algorithm of the key to wrap
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public PublicVerificationKey buildVerificationKey(PublicKey publicKey, AlgorithmType alg) throws IllegalArgumentException, InvalidKeySpecException, IOException;
	
	/**
	 * Method that generates a truststore
	 * @param certificates the trsuted certificates
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IllegalArgumentException 
	 */
	Map<HashedId8, EtsiTs103097Certificate> genTrustStore(EtsiTs103097Certificate[] certificates) throws IllegalArgumentException, NoSuchAlgorithmException, IOException;
	/**
	 * Method that generates a new request for a single enrollment credential
	 * @param itsID
	 * @param canonicalKeys
	 * @param verificationKeys
	 * @param vehicleAlgorithm
	 * @param EnrollmentAuthorityCertificate
	 * @param sharedKey
	 * @return
	 * @throws Exception
	 * @throws GeneralSecurityException
	 */
	EtsiTs103097Data genEcRequest(String itsID, KeyPair canonicalKeys, KeyPair verificationKeys,
			AlgorithmType vehicleAlgorithm, EtsiTs103097Certificate EnrollmentAuthorityCertificate, SecretKey sharedKey)
			throws Exception, GeneralSecurityException;
	
	/**
	 * Method that processes an enrollment response that came from an enrollment authority
	 * This method first decrypts the response using the public encryption key of the EA,
	 * Then it verifies the EA's signature using the public verification key of the EA.
	 * 
	 * @param encryptedResponse the encoded encrypted enrollment response
	 * @param EAcertificate the trusted EA certificate
	 * @param sharedKey the symmetric key that belongs to the vehicle (previously shared with the EA on the enrollment request)
	 * @return the inner enrollment response (contains the response code and if applicable the enrollment certificate)
	 * @throws IOException
	 */
	InnerEcResponse processEcResponse(byte[] encryptedResponse, EtsiTs103097Certificate EAcertificate, SecretKey sharedKey) throws IOException;
	
	/**
	 *  Method that generates a new request for a single authorization ticket
	 * @param enrollmentCredential
	 * @param enrollmentSigningKey
	 * @param vehicleAlgorithm
	 * @param verificationKeys
	 * @param AaCertificate
	 * @param EaCertificate
	 * @param AASharedKey
	 * @param EASharedKey
	 * @return
	 * @throws Exception
	 * @throws GeneralSecurityException
	 */
	EtsiTs103097Data genAtRequest(EtsiTs103097Certificate enrollmentCredential, PrivateKey enrollmentSigningKey,
			AlgorithmType vehicleAlgorithm, KeyPair verificationKeys, EtsiTs103097Certificate AaCertificate,
			EtsiTs103097Certificate EaCertificate, SecretKey AASharedKey, SecretKey EASharedKey) throws Exception, GeneralSecurityException;
	
	
	/**
	 * Method that processes an authorization response that came from an authorization authority
	 * This method first decrypts the response using the public encryption key of the AA,
	 * Then it verifies the AA's signature using the public verification key of the AA.
	 * 
	 * @param encryptedResponse the encoded encrypted authorization response
	 * @param aaCertificate the trusted AA certificate
	 * @param sharedKey the symmetric key that belongs to the vehicle (previously shared with the AA on the authorization request)
	 * @throws IOException
	 */
	InnerAtResponse processAtResponse(byte[] encryptedResponse, EtsiTs103097Certificate aaCertificate,
			SecretKey sharedKey) throws IOException;
	
	/**
	 * method to generate an certificate hashed id with the SHA-256 algorithm
	 */
	byte[] genCertificateHashedId(EtsiTs103097Certificate certificate) throws NoSuchAlgorithmException, IOException;
	
	 /**
     * Method to generate a Cooperative Awareness Message (CAM)
     * @param authorizationTicket the authorization ticket used to certificate this message. Required
     * @param signingkeyPair the keypair to that will be used to sign the message (should be related to the authorization ticket). Required
     * @param signatureAlgorithm the algorithm of the signature. Required
     * @param payload the payload to be signed
     * @param inlineP2pcdRequest used to broadcast a request gor missing authorization tickets (authorization tickets are needed to CAM verification). Optional
     * @param requestedCertificate used to broadcast the authorization ticket requested by the inlineP2pcdRequest. Optional
     * @param includeCert, true if the signerId should be the full certificate, false to used a hashedId. Required
     * @return a signed message
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws SignatureException 
     */
	EtsiTs103097Data genCAM(EtsiTs103097Certificate authorizationTicket, KeyPair signingkeyPair, AlgorithmType signatureAlgorithm,
			String payload, HashedId3[] inlineP2pcdRequest, EtsiTs103097Certificate requestedCertificate,
			boolean includeCert) throws SignatureException, NoSuchAlgorithmException, IOException;
	/**
	 * Method to extract the signer id information froma  sign message (Certificate, certificate hash or self)
	 * @param signData e.g. CAM 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws BadContentTypeException 
	 */
	SignerIdentifier getSignerId(EtsiTs103097Data signData) throws NoSuchAlgorithmException, IOException, BadContentTypeException;
	/**
	 * Method that processes a received CAM
	 * This method checks the signature against the provided certificate
	 * @param cam
	 * @param authTicket
	 * @return 
	 */
	boolean processCAM(EtsiTs103097Data cam, EtsiTs103097Certificate authTicket);
	Boolean verifyCertificate(EtsiTs103097Certificate certificate, EtsiTs103097Certificate signerCertificate);
}
