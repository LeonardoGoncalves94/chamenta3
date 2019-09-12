package com.multicert.project.v2x.pkimanager.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.DemoApplication;
import com.multicert.project.v2x.pkimanager.JavaKeyStore;
import com.multicert.project.v2x.pkimanager.model.CA;
import com.multicert.project.v2x.pkimanager.model.Certificate;
import com.multicert.project.v2x.pkimanager.model.EnrollmentCredential;
import com.multicert.project.v2x.pkimanager.model.Key;
import com.multicert.project.v2x.pkimanager.model.Profile;
import com.multicert.project.v2x.pkimanager.model.Region;
import com.multicert.project.v2x.pkimanager.model.Role;
import com.multicert.project.v2x.pkimanager.model.User;
import com.multicert.project.v2x.pkimanager.repository.CertRepository;
import com.multicert.project.v2x.pkimanager.repository.RegionRepository;
import com.multicert.project.v2x.pkimanager.repository.RoleRepository;
import com.multicert.project.v2x.pkimanager.repository.UserRepository;
import com.multicert.project.v2x.pkimanager.repository.VehicleProfileRepository;
import com.multicert.v2x.IdentifiedRegions.Countries;
import com.multicert.v2x.IdentifiedRegions.Countries.CountryTypes;
import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.BadContentTypeException;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.cryptography.DecryptionException;
import com.multicert.v2x.cryptography.ImcompleteRequestException;
import com.multicert.v2x.cryptography.IncorrectRecipientException;
import com.multicert.v2x.cryptography.InvalidSignatureException;
import com.multicert.v2x.cryptography.UnknownItsException;
import com.multicert.v2x.datastructures.base.BasePublicEncryptionKey.BasePublicEncryptionKeyTypes;
import com.multicert.v2x.datastructures.base.CountryOnly;
import com.multicert.v2x.datastructures.base.Duration.DurationTypes;
import com.multicert.v2x.datastructures.base.EccP256CurvePoint;
import com.multicert.v2x.datastructures.base.GeographicRegion;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.IdentifiedRegion;
import com.multicert.v2x.datastructures.base.Psid;
import com.multicert.v2x.datastructures.base.PsidSspRange;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.base.SequenceOfIdentifiedRegion;
import com.multicert.v2x.datastructures.base.SequenceOfPsidSsp;
import com.multicert.v2x.datastructures.base.Signature;
import com.multicert.v2x.datastructures.base.SspRange;
import com.multicert.v2x.datastructures.base.SubjectAssurance;
import com.multicert.v2x.datastructures.base.SymmAlgorithm;
import com.multicert.v2x.datastructures.base.ValidityPeriod;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationResponseCode;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationValidationRequest;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.SharedAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.EnrollmentResonseCode;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcRequest;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.generators.certificate.AuthorizationTicketGenerator;
import com.multicert.v2x.generators.certificate.CACertGenerator;
import com.multicert.v2x.generators.certificate.EnrollmentCredentiaGenerator;
import com.multicert.v2x.generators.certificaterequest.AuthorizationResponseGenerator;
import com.multicert.v2x.generators.certificaterequest.EnrollmentResponseGenerator;
import com.multicert.v2x.generators.message.SecuredDataGenerator;


@Service("V2XService")
public class V2XServiceImpl implements V2XService{

	private CryptoHelper cryptoHelper;
	private CACertGenerator caCertGenerator;

	private EnrollmentResponseGenerator eResponseGenerator;
	private AuthorizationResponseGenerator aResponseGenerator;
	private EnrollmentCredentiaGenerator enrollmentCertGenerator;
	private AuthorizationTicketGenerator authorizationTicketGenerator; 

	@Autowired
	private VehicleProfileRepository vehicleProfileRepository;
	
	@Autowired
	private EnrollCredentialService enrollService;
	
	public V2XServiceImpl() throws Exception {
		cryptoHelper = new CryptoHelper("BC");
		caCertGenerator = new CACertGenerator(cryptoHelper, false);
		eResponseGenerator = new EnrollmentResponseGenerator(cryptoHelper);
		aResponseGenerator = new AuthorizationResponseGenerator(cryptoHelper);
		enrollmentCertGenerator = new EnrollmentCredentiaGenerator(cryptoHelper, false);
		authorizationTicketGenerator = new AuthorizationTicketGenerator(cryptoHelper, false);
		
	}
	
	@Override
	public KeyPair genKeyPair( String algorithm) throws Exception {
		
		return cryptoHelper.genKeyPair(getCurveType(algorithm));
	}
	

	@Override
	public byte[] genRootCertificate(Certificate rootcertificate) throws Exception {
			
		try {
			CA subject = rootcertificate.getSubject();
			
			String hostname = subject.getCaName();
			ValidityPeriod validityPeriod = new ValidityPeriod(new Date(),DurationTypes.YEARS, rootcertificate.getValidity());	
			GeographicRegion geographicRegion = getGeographicRegion(rootcertificate.getRegions());
			
			
			
			Key signatureKey = subject.getSignatureKey(); 
			Key encryptionKey = subject.getEncryptionKey();
			
			Signature.SignatureTypes sigAlgorithm = getSignatureType(signatureKey.getAlgorithm());
			KeyPair subjectSigKeys = JavaKeyStore.getKeyPair(signatureKey.getAlias());
			
			BasePublicEncryptionKeyTypes encAlgorithm = null;	
			PublicKey subjectEncKey = null;
			SymmAlgorithm symmAlgorithm = null;
			
			if(encryptionKey != null)
			{
				encAlgorithm = getEncryptionType(encryptionKey.getAlgorithm());
				subjectEncKey = JavaKeyStore.getKeyPair(encryptionKey.getAlias()).getPublic();
				symmAlgorithm = SymmAlgorithm.AES_128_CCM;
			}
			
			EtsiTs103097Certificate rootCertificate = caCertGenerator.generateRootCA(hostname, validityPeriod, geographicRegion, 
					rootcertificate.getAssurance().intValue(), rootcertificate.getConfidence().intValue(), 
					rootcertificate.getMinChain().intValue(), rootcertificate.getChainRange().intValue(), sigAlgorithm, 
					subjectSigKeys, symmAlgorithm, encAlgorithm, subjectEncKey);
			return rootCertificate.getEncoded();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception ("Error generating certificate");
		}
	}

	@Override
	public byte[] genSubCertificate(Certificate subCertificate ) throws Exception {
		
		try {
			CA issuer = subCertificate.getIssuer();
			
			CA subject = subCertificate.getSubject();
			String caType = subject.getCaType();
			
			String hostname = subject.getCaName();
			ValidityPeriod validityPeriod = new ValidityPeriod(new Date(),DurationTypes.YEARS, subCertificate.getValidity());	
			GeographicRegion geographicRegion = getGeographicRegion(subCertificate.getRegions());
			
	        PsidSspRange[] subjectPerms = new PsidSspRange[1];
	        subjectPerms[0] = new PsidSspRange(new Psid(subCertificate.getPsId()), new SspRange(SspRange.SspRangeTypes.ALL, null));
	        int assurance = subCertificate.getAssurance().intValue();
	        int confidence = subCertificate.getConfidence().intValue();
	        int minChainDepth = subCertificate.getMinChain().intValue();
	        int chainDepthRange = subCertificate.getChainRange().intValue();
	       
			
			Key issuerKey = issuer.getSignatureKey();
			KeyPair issuerSigKeys = JavaKeyStore.getKeyPair(issuerKey.getAlias());
			EtsiTs103097Certificate issuerCertificate = new EtsiTs103097Certificate(issuer.getCertificate().getEncoded());
			
			Key signatureKey = subject.getSignatureKey(); 
			Key encryptionKey = subject.getEncryptionKey();
			Signature.SignatureTypes sigAlgorithm = getSignatureType(signatureKey.getAlgorithm());
			KeyPair subjectSigKeys = JavaKeyStore.getKeyPair(signatureKey.getAlias());
			
			BasePublicEncryptionKeyTypes encAlgorithm = null;	
			PublicKey subjectEncKey = null;
			SymmAlgorithm symmAlgorithm = null;
			
			if(encryptionKey != null)
			{
				encAlgorithm = getEncryptionType(encryptionKey.getAlgorithm());
				subjectEncKey = JavaKeyStore.getKeyPair(encryptionKey.getAlias()).getPublic();
				symmAlgorithm = SymmAlgorithm.AES_128_CCM;
			}
			
			if(caType.equals("Enrollment")) 
			{
				EtsiTs103097Certificate enrollmentCaCert = caCertGenerator.generateEnrollmentCa(hostname,validityPeriod,geographicRegion,
						subjectPerms,assurance,confidence,minChainDepth,chainDepthRange,sigAlgorithm,subjectSigKeys.getPublic(),
						issuerCertificate,issuerSigKeys,symmAlgorithm,encAlgorithm,subjectEncKey);
				return enrollmentCaCert.getEncoded();
			}
			if (caType.equals("Authorization"))
			{
				EtsiTs103097Certificate authorizationCaCert = caCertGenerator.generateAuthorizationAuthority(hostname,validityPeriod,geographicRegion,
						subjectPerms,assurance,confidence,minChainDepth,chainDepthRange,sigAlgorithm,subjectSigKeys.getPublic(),
						issuerCertificate,issuerSigKeys,symmAlgorithm,encAlgorithm,subjectEncKey);
				return authorizationCaCert.getEncoded();
			}
			 throw new IOException("Error generation Sub CA certificate: CA type is unknown");
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception ("Error generating certificate");
		}
		
	}
	@Override
	public SecuredDataGenerator initDataGenerator()
	{
		return new SecuredDataGenerator(cryptoHelper); // this object will contain the vehicle shared key (used for the encryption of the response) and the innerEcRequest (used for building the rnollment credential)
	}
	
	@Override
	public EtsiTs103097Data decryptRequest(SecuredDataGenerator securedDataGenerator, byte[] encodedEncryptedRequest, Certificate destinationCertificate, Key decriptionPair) throws Exception
	{
		EtsiTs103097Certificate destCert = new EtsiTs103097Certificate(destinationCertificate.getEncoded());
		
		System.out.println("SERVER_SIDE EA = "+Hex.toHexString(genCertificateHashedId(destCert)));
		
		PrivateKey decryptionKey = JavaKeyStore.getKeyPair(decriptionPair.getAlias()).getPrivate();
		
		byte[] decyptedBytes;
		decyptedBytes = securedDataGenerator.decryptEncryptedData(encodedEncryptedRequest, destCert ,decryptionKey); //if the CA can't decrypt the request, it cannot encrypt the response. The RA is notified of this by catching the exception.
		SecretKey sharedKey = securedDataGenerator.getSharedKey();
		
		return new EtsiTs103097Data(decyptedBytes);
	}
	@Override
	public EnrollmentResonseCode verifyEcRequest(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data decryptedRequest, PublicKey canonicalKey) throws IOException
	{
		EnrollmentResonseCode responseCode = EnrollmentResonseCode.OK;
		try {
			securedDataGenerator.verifySignedRequest(decryptedRequest, canonicalKey);
		} catch (IllegalArgumentException | ImcompleteRequestException
				| BadContentTypeException | InvalidSignatureException e) {
			e.printStackTrace();
			responseCode = exceptionToEnrollResponseCode(e); //if there are problems with the signature an EcResponse is generated containing the appropriate response code
			return responseCode;
			
		}
		// in case the request was successfully decrypted and the vehicle's signature checks, we need to check if the requested attributes are plausible for this vehicle's profile
		responseCode = verifyRequestedEcSubjectAttributes();
		 return responseCode;		
	}
	
	@Override
	public AuthorizationResponseCode verifyAtRequest(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data decryptedRequest) throws IOException
	{
		AuthorizationResponseCode responseCode = AuthorizationResponseCode.OK;
		try {
			securedDataGenerator.verifyAtPOPSignature(decryptedRequest);
		} catch (IllegalArgumentException | SignatureException | InvalidKeySpecException | ImcompleteRequestException
				| BadContentTypeException | InvalidSignatureException e) {
			e.printStackTrace();
			responseCode = exceptionToAuthResponseCode(e); //if there are problems with the signature an EcResponse is generated containing the appropriate response code
			return responseCode;
			
		}
		// in case the request was successfully decrypted and the vehicle's signature checks, we need to check if the requested attributes are plausible for this vehicle's profile
		responseCode = verifyRequestedAtSubjectAttributes();
		 return responseCode;	
			
	}
	
	@Override
	public void verifyEnrollmentSignature(SecuredDataGenerator securedDataGenerator, EtsiTs103097Certificate enrollmentCredential) throws InvalidSignatureException, BadContentTypeException
	{
		securedDataGenerator.verifyEnrollmentSignature(enrollmentCredential);
	}
	
	
	@Override
	 public byte[] getEnrollmentCertId(SecuredDataGenerator securedDataGenerator, AuthorizationValidationRequest authorizationValidationRequest) throws BadContentTypeException
	 {
		return securedDataGenerator.getEcSignatureSignerId(authorizationValidationRequest);	
	 }
	
	/**
	 * Dummy method that verifies the attributes that the vehicle is requesting
	 * @return response code OK if everything checks or DENIED_PERMISSIONS otherwise
	 */
	private EnrollmentResonseCode verifyRequestedEcSubjectAttributes() 
	{
		//TODO: make real verification of the attributes that the vehicle is requesting, i.e. validity period, region, app permissions etc
		return EnrollmentResonseCode.OK;
	
	}
	
	/**
	 * Dummy method that verifies the attributes that the vehicle is requesting (verify against the profile stored in the DB)
	 * @return response code OK if everything checks or DENIED_PERMISSIONS otherwise
	 */
	private AuthorizationResponseCode verifyRequestedAtSubjectAttributes() 
	{
		//TODO: make real verification of the attributes that the vehicle is requesting, i.e. validity period, region, app permissions etc
		return AuthorizationResponseCode.OK;
	
	}
	
	
	@Override
	public EtsiTs103097Data genEcResponse(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data signedRequest, String profile,
			EnrollmentResonseCode responseCode, Certificate EAcertificate, 
			Key signatureKeys, CA EA) throws Exception
	{	
		KeyPair sigKeyPair = JavaKeyStore.getKeyPair(signatureKeys.getAlias());
		AlgorithmType sigAlg = getSignatureType(signatureKeys.getAlgorithm());
		SecretKey sharedKey = securedDataGenerator.getSharedKey();  //get the symmetric shared key to encrypt the response to the vehicle
		EtsiTs103097Certificate EaCert = new EtsiTs103097Certificate(EAcertificate.getEncoded());
		
		if(responseCode != EnrollmentResonseCode.OK)
		{
			return eResponseGenerator.generateEcResponse(signedRequest, sharedKey, responseCode, null, EaCert, sigKeyPair, sigAlg); //negative response with an error code an no enrollment credential is returned
		}
		else
		{
			InnerEcRequest innerRequest = securedDataGenerator.getInnerEcRequest();
			PublicKey vehicleVerificationKey = securedDataGenerator.getECRequestPublicKey();
			EtsiTs103097Certificate enrollmentCredential =  genEnrollmentCredential(innerRequest, vehicleVerificationKey, profile, sigAlg,EaCert, sigKeyPair, EA);
			return eResponseGenerator.generateEcResponse(signedRequest, sharedKey, responseCode, enrollmentCredential, EaCert, sigKeyPair, sigAlg); //positive response with new enrollment credential is returned
		}
		
	}
	
	
	
	private EtsiTs103097Certificate genEnrollmentCredential(InnerEcRequest innerRequest, PublicKey vehicleVerificationKey, 
			String profileName, AlgorithmType signingAlgorithm, EtsiTs103097Certificate EaCert,
			KeyPair signatureKeys, CA EA) throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, IOException
	{
		try {
			String itsId = innerRequest.getItsId();
			Profile vehicleProfile = vehicleProfileRepository.findByProfileName(profileName);
			ValidityPeriod validityPeriod = new ValidityPeriod(new Date(),DurationTypes.YEARS, vehicleProfile.getEnrollmentPeriod());
			GeographicRegion validityRegion = getGeographicRegion(vehicleProfile.getRegions());		
			SubjectAssurance assurance = innerRequest.getRequestedSubjectAttributes().getAssuranceLevel();
			int assuranceLevel = assurance.getAssuranceLevel();
			int confidenceLevel = assurance.getConfidenceLevel();
		
				
			EtsiTs103097Certificate enrollCredential = enrollmentCertGenerator.generateEnrollmentCredential(itsId,validityPeriod,validityRegion,assuranceLevel,confidenceLevel,signingAlgorithm, vehicleVerificationKey, EaCert, signatureKeys, null, null, null); //Encryption information is set to null
		
			//Save the enrollment credential in the database as active
			EnrollmentCredential DBenrollCredential = new EnrollmentCredential(genCertificateHashedId(enrollCredential),EA,itsId,false, enrollCredential.getEncoded());
			enrollService.saveEnrollmentCredential(DBenrollCredential);
			return enrollCredential;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public EtsiTs103097Data genAtResponse(SecuredDataGenerator securedDataGenerator, EtsiTs103097Data signedRequest, String profile,
			AuthorizationResponseCode responseCode, Certificate AAcertificate, Key signatureKeys, CA AA) throws Exception
	{	
		KeyPair sigKeyPair = JavaKeyStore.getKeyPair(signatureKeys.getAlias());
		AlgorithmType sigAlg = getSignatureType(signatureKeys.getAlgorithm());
		SecretKey sharedKey = securedDataGenerator.getSharedKey();  //get the symmetric shared key to encrypt the response to the vehicle
		EtsiTs103097Certificate AaCert = new EtsiTs103097Certificate(AAcertificate.getEncoded());
		
		if(responseCode != AuthorizationResponseCode.OK)
		{
			return aResponseGenerator.generateAtResponse(signedRequest, sharedKey, responseCode, null, AaCert, sigKeyPair, sigAlg); //negative response with an error code an no enrollment credential is returned
		}
		else
		{
			SharedAtRequest sharedAtRequest = securedDataGenerator.getSharedAtRequest();
			PublicKey vehicleVerificationKey = securedDataGenerator.getATRequestPublicKey();
			EtsiTs103097Certificate authorizationTicket = genAuthorizationTicket(sharedAtRequest, vehicleVerificationKey, profile, sigAlg, AaCert, sigKeyPair, AA);
			return aResponseGenerator.generateAtResponse(signedRequest, sharedKey, responseCode, authorizationTicket, AaCert, sigKeyPair, sigAlg);
		}
		
	
	}
	
	private EtsiTs103097Certificate genAuthorizationTicket(SharedAtRequest sharedAtRequest, PublicKey vehicleVerificationKey, 
			String profileName, AlgorithmType signingAlgorithm, EtsiTs103097Certificate AaCert,
			KeyPair signatureKeys, CA AA) throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, IOException
	{
		try {
		
			Profile vehicleProfile = vehicleProfileRepository.findByProfileName(profileName);
			GeographicRegion validityRegion = getGeographicRegion(vehicleProfile.getRegions());		
			SubjectAssurance assurance = sharedAtRequest.getSubjectAttributes().getAssuranceLevel();
			int assuranceLevel = assurance.getAssuranceLevel();
			int confidenceLevel = assurance.getConfidenceLevel();
			ValidityPeriod validityPeriod = sharedAtRequest.getSubjectAttributes().getValidityPeriod();
			SequenceOfPsidSsp appPermissions = sharedAtRequest.getSubjectAttributes().getAppPermissions();
			return authorizationTicketGenerator.generateAuthorizationTicket(validityPeriod, validityRegion, appPermissions, assuranceLevel, confidenceLevel, signingAlgorithm, vehicleVerificationKey, AaCert, signatureKeys, null, null, null);  //Encryption information is set to null
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	@Override
	public byte[] genCertificateHashedId(EtsiTs103097Certificate certificate) throws NoSuchAlgorithmException, IOException 
	{
		return cryptoHelper.getCertificateHashId(certificate,  HashAlgorithm.SHA_256).getHashedId();
	}
    
	
	
	@Override
	public PublicKey extractPublicKey(PublicVerificationKey verificationKey) throws InvalidKeySpecException {
		AlgorithmType alg = verificationKey.getType();
		EccP256CurvePoint curvePoint = (EccP256CurvePoint) verificationKey.getValue();
		return (PublicKey) cryptoHelper.eccPointToPublicKey(alg, curvePoint);
	}
	
	@Override
	public Boolean verifyCertificate(EtsiTs103097Certificate certificate, EtsiTs103097Certificate signerCertificate){
		try {
			return cryptoHelper.verifyCertificate(certificate, signerCertificate);
		} catch (IllegalArgumentException | SignatureException | IOException | BadContentTypeException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Help method that converts a known string that represents an algorithm into a signature algorithm 
	 * Used when generating a keyPair to specify the type of curve.
	 * @throws IOException 
	 */
	private Signature.SignatureTypes getCurveType (String alg) throws IOException {
		
		if(alg.equals("ECDSA-Nist" ) || alg.equals("ECIES-Nist")) {
			return Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
		}
		if(alg.equals("ECDSA-Brainpool") || alg.equals("ECIES-Brainpool")){
			
			return Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE;
		}
		
		throw new IOException("Error geting curve type: specified algorithm does not exist");		
	}
	
	
	/**
	 * Help method that converts a known string that represents an algorithm into a signature algorithm 
	 * Used when generating a certificate to specify the algorithm of the encryption key
	 * @throws IOException 
	 */
	private Signature.SignatureTypes getSignatureType (String alg) throws IOException {
		
		if(alg.equals("ECDSA-Nist" )) {
			return Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
		}
		if(alg.equals("ECDSA-Brainpool")){
			
			return Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE;
		}
		
		throw new IOException("Error geting signature algorithm: specified algorithm does not exist");
			
	}
	
	/**
	 * Help method that converts a known string that represents an algorithm into an encryption algorithm object
	 * Used when generating a certificate to specify the algorithm of the encryption key
	 * @throws IOException 
	 */
	private BasePublicEncryptionKeyTypes getEncryptionType (String alg) throws IOException {
		
		if(alg.equals("ECIES-Nist")) {
			return BasePublicEncryptionKeyTypes.ECIES_NIST_P256;
		}
		if(alg.equals("ECIES-Brainpool")){
			
			return BasePublicEncryptionKeyTypes.ECIES_BRAINPOOL_P256r1;
		}
		
		throw new IOException("Error geting encryption algorithm: specified algorithm does not exist");
			
	}
	
	private GeographicRegion getGeographicRegion(List <Region> regions) {
		
		int size = regions.size();
		IdentifiedRegion[] identifiedRegions = new IdentifiedRegion[size];
		
		for(int i = 0; i < size; i++) {
			
			IdentifiedRegion identifiedRegion = new IdentifiedRegion(new CountryOnly(regions.get(i).getRegionNumber()));
            identifiedRegions[i] = identifiedRegion;
		}
		 SequenceOfIdentifiedRegion sequenceOfIdentifiedRegion = new SequenceOfIdentifiedRegion(identifiedRegions);
		 return new GeographicRegion(sequenceOfIdentifiedRegion);
	}
	
	
	/**
	 * Method that converts a known enrollment validation exception into an EnrollmentResonseCode to be part of the Enrollment response
	 */
	private EnrollmentResonseCode exceptionToEnrollResponseCode (Exception e)
	{
			if(e instanceof BadContentTypeException) {
				e.printStackTrace();
				return EnrollmentResonseCode.BAD_CONTENT_TYPE;
			}
			if(e instanceof DecryptionException) {
				e.printStackTrace();
				return EnrollmentResonseCode.DECRYPTION_FAILED;
			}
			if(e instanceof ImcompleteRequestException) {
				e.printStackTrace();
			return EnrollmentResonseCode.INCOMPLETE_REQUEST;
			}
		
			if(e instanceof InvalidSignatureException) {
				e.printStackTrace();
				return EnrollmentResonseCode.INVALID_SIGNATURE;
			}
			if(e instanceof UnknownItsException) {
				e.printStackTrace();
				return EnrollmentResonseCode.UNKNOWN_ITS;
			}
			return EnrollmentResonseCode.DENIED_REQUEST;
	}	
	
	/**
	 * Method that converts a known enrollment validation exception into an EnrollmentResonseCode to be part of the Enrollment response
	 */
	private AuthorizationResponseCode exceptionToAuthResponseCode (Exception e)
	{
			if(e instanceof BadContentTypeException) {
				e.printStackTrace();
				return AuthorizationResponseCode.BAD_CONTENT_TYPE;
			}
			if(e instanceof DecryptionException) {
				e.printStackTrace();
				return AuthorizationResponseCode.DECRYPTION_FAILED;
			}
			if(e instanceof ImcompleteRequestException) {
				e.printStackTrace();
			return AuthorizationResponseCode.INCOMPLETE_REQUEST;
			}
		
			if(e instanceof InvalidSignatureException) {
				e.printStackTrace();
				return AuthorizationResponseCode.INVALID_SIGNATURE;
			}
			if(e instanceof UnknownItsException) {
				e.printStackTrace();
				return AuthorizationResponseCode.UNKNOWN_ITS;
			}
			return AuthorizationResponseCode.DENIED_REQUEST;
	}	
}
