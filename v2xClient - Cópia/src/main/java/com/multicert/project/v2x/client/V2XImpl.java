package com.multicert.project.v2x.client;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.cryptography.DecryptionException;
import com.multicert.v2x.cryptography.ImcompleteRequestException;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.cryptography.InvalidSignatureException;
import com.multicert.v2x.datastructures.base.Duration;
import com.multicert.v2x.datastructures.base.EccP256CurvePoint;
import com.multicert.v2x.datastructures.base.GeographicRegion;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.HashedId3;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.base.Psid;
import com.multicert.v2x.datastructures.base.PsidSsp;
import com.multicert.v2x.datastructures.base.EccP256CurvePoint.EccP256CurvePointTypes;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.SignedData;
import com.multicert.v2x.datastructures.message.secureddata.SignerIdentifier;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.base.SequenceOfHashedId3;
import com.multicert.v2x.datastructures.base.ValidityPeriod;
import com.multicert.v2x.generators.certificaterequest.AuthorizationRequestGenerator;
import com.multicert.v2x.generators.certificaterequest.EnrollmentRequestGenerator;
import com.multicert.v2x.generators.message.CAMGenerator;
import com.multicert.v2x.generators.message.SecuredDataGenerator;

public class V2XImpl implements V2X {
	
	private CryptoHelper cryptoHelper;
	private EnrollmentRequestGenerator ecRequestGenerator;
	private AuthorizationRequestGenerator atRequestGenerator;
	private CAMGenerator camGenerator;
	
	public V2XImpl() throws Exception {
		cryptoHelper = new CryptoHelper("BC");
		ecRequestGenerator = new EnrollmentRequestGenerator(cryptoHelper, false);
		atRequestGenerator = new AuthorizationRequestGenerator(cryptoHelper, false);
		camGenerator = new CAMGenerator(cryptoHelper, false);
	}
	
	@Override
	public KeyPair genKeyPair(AlgorithmType alg) throws Exception
	{
		return cryptoHelper.genKeyPair(alg);
	}
	
	@Override
	public SecretKey genSecretKey (AlgorithmType alg) throws Exception
	{
		return cryptoHelper.genSecretKey(alg);
	}
	
	@Override
	public PublicVerificationKey buildVerificationKey(PublicKey publicKey, AlgorithmType alg) throws IllegalArgumentException, InvalidKeySpecException, IOException  
	{
		EccP256CurvePoint point = (EccP256CurvePoint)cryptoHelper.publicKeyToEccPoint(alg, EccP256CurvePointTypes.UNCOMPRESSED, publicKey);
		return new PublicVerificationKey(cryptoHelper.getVerificationKeyType(alg),point);
	
	}
	
	@Override
	public EtsiTs103097Data genEcRequest(String itsID, KeyPair canonicalKeys, KeyPair verificationKeys, AlgorithmType vehicleAlgorithm, 
			EtsiTs103097Certificate EnrollmentAuthorityCertificate, SecretKey sharedKey) throws Exception, GeneralSecurityException
	{
		int dummyAssurance = 1;
		int dummyConfidence = 2;
		return ecRequestGenerator.generateEcRequest(itsID, canonicalKeys, vehicleAlgorithm, verificationKeys, vehicleAlgorithm,
				null, null, dummyAssurance, dummyConfidence, EnrollmentAuthorityCertificate, sharedKey);
	}
	
	@Override
	public EtsiTs103097Data genAtRequest(EtsiTs103097Certificate enrollmentCredential, PrivateKey enrollmentSigningKey,
			AlgorithmType vehicleAlgorithm, KeyPair verificationKeys, EtsiTs103097Certificate AaCertificate,
			EtsiTs103097Certificate EaCertificate, SecretKey AASharedKey, SecretKey EASharedKey) throws Exception, GeneralSecurityException
	{
		int dummyAssurance = 1;
		int dummyConfidence = 2;
		PsidSsp dummyPermission = new PsidSsp(new Psid(44), null); // permissions to sign certificate basic v2x messages
        PsidSsp[] dummyPermissions = {dummyPermission};
        ValidityPeriod dummyValidityPeriod =  new ValidityPeriod(new Date(), Duration.DurationTypes.SIXTY_HOURS, 3); // need to change this so that ATs for a week have a different period than other week
		
		return atRequestGenerator.generateAtRequest(enrollmentCredential, enrollmentSigningKey, vehicleAlgorithm,
				verificationKeys, vehicleAlgorithm, AaCertificate, EaCertificate, dummyValidityPeriod, null, dummyPermissions, 
				dummyAssurance, dummyConfidence, AASharedKey, EASharedKey);
	}
	
	@Override
	public  Map<HashedId8, EtsiTs103097Certificate> genTrustStore(EtsiTs103097Certificate[] certificates) throws IllegalArgumentException, NoSuchAlgorithmException, IOException
	{
		return cryptoHelper.buildCertStore(certificates, HashAlgorithm.SHA_256); 
	}
	
	@Override
	public InnerEcResponse processEcResponse(byte[] encryptedResponse, EtsiTs103097Certificate eaCertificate, SecretKey sharedKey) throws IOException
	{	
		SecuredDataGenerator dataGenerator = new SecuredDataGenerator(cryptoHelper);
		
		byte[] decryptedResponse = null;
		try {
			decryptedResponse = dataGenerator.decryptEncryptedData(encryptedResponse, sharedKey);		
			dataGenerator.verifySignedEcResponse(decryptedResponse, eaCertificate);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IncorrectRecipientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (BadContentTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (DecryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ImcompleteRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidSignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		 InnerEcResponse innerResponse = dataGenerator.getInnerEcResponse();
		 return innerResponse;		
	}
	
	@Override
	public InnerAtResponse processAtResponse(byte[] encryptedResponse, EtsiTs103097Certificate aaCertificate, SecretKey sharedKey) throws IOException
	{	
		SecuredDataGenerator dataGenerator = new SecuredDataGenerator(cryptoHelper);
		
		byte[] decryptedResponse = null;
		try {
			decryptedResponse = dataGenerator.decryptEncryptedData(encryptedResponse, sharedKey);		
			dataGenerator.verifySignedAtResponse(decryptedResponse, aaCertificate);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IncorrectRecipientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (BadContentTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (DecryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ImcompleteRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidSignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		InnerAtResponse innerResponse = dataGenerator.getInnerAtResponse();
		 return innerResponse;
		
	}
	
	@Override
	public synchronized HashedId8 genCertificateHashedId8(EtsiTs103097Certificate certificate) throws NoSuchAlgorithmException, IOException 
	{
		return new HashedId8(cryptoHelper.getCertificateHashId(certificate,  HashAlgorithm.SHA_256).getHashedId());
	}
	
	@Override
	public synchronized HashedId3 genCertificateHashedId3(EtsiTs103097Certificate certificate) throws NoSuchAlgorithmException, IOException 
	{
		return new HashedId3(cryptoHelper.getCertificateHashId(certificate,  HashAlgorithm.SHA_256).getHashedId());
	}
	
	@Override
	public EtsiTs103097Data genCAM(EtsiTs103097Certificate authorizationTicket, KeyPair signingkeyPair,
                                        AlgorithmType signatureAlgorithm, String payload, HashedId3[] inlineP2pcdRequest,
                                        EtsiTs103097Certificate requestedCertificate, boolean includeCert) throws SignatureException, NoSuchAlgorithmException, IOException 
	{
		SequenceOfHashedId3 hashSeq = null;
	
		
		if (inlineP2pcdRequest != null && inlineP2pcdRequest.length > 0)
		{
			hashSeq = new SequenceOfHashedId3(inlineP2pcdRequest);
		}
				
		return camGenerator.generateCAM(authorizationTicket, signingkeyPair, signatureAlgorithm, payload, hashSeq, requestedCertificate, includeCert);	
	}
	
	@Override
	public SignerIdentifier getSignerId(EtsiTs103097Data signData) throws NoSuchAlgorithmException, IOException, BadContentTypeException 
	{
		SecuredDataGenerator dataGenerator = new SecuredDataGenerator(cryptoHelper);
		
		return dataGenerator.getSignerId(signData);
	}
	
	//TODO USAR A FULL CHAIN EM VEZ DE SÓ O SIGNER CERT, VER TAMBÉM A VERIFICAÇÃO 
	@Override
	public synchronized Boolean verifyCertificate(EtsiTs103097Certificate certificate, EtsiTs103097Certificate signerCertificate){
		try {
			return cryptoHelper.verifyCertificate(certificate, signerCertificate);
		} catch (IllegalArgumentException | SignatureException | IOException | BadContentTypeException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//TODO OTHER MESSAGE VERIFICATION (EXPIRATION, GEO LOCATION)
	@Override
	public boolean processCAM(EtsiTs103097Data cam, EtsiTs103097Certificate authTicket) 
	{
		SecuredDataGenerator dataGenerator = new SecuredDataGenerator(cryptoHelper);
		try {
			dataGenerator.verifyCAM(cam, authTicket);
		} catch (BadContentTypeException | ImcompleteRequestException | InvalidSignatureException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	public List<HashedId3> getRequestedCerts(EtsiTs103097Data signData) throws BadContentTypeException{
		SecuredDataGenerator dataGenerator = new SecuredDataGenerator(cryptoHelper);
		SequenceOfHashedId3 sequence = dataGenerator.getRequestedAts(signData);
		if(sequence != null) {
			HashedId3[] hashes = (HashedId3[]) sequence.getValues();
			return Arrays.asList(hashes);
		}	
		return new ArrayList<HashedId3>();
	}	
    

}
