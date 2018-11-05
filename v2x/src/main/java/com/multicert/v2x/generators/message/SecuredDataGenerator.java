package com.multicert.v2x.generators.message;

import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.cryptography.*;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.base.Signature;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;

import com.multicert.v2x.datastructures.certificate.SequenceOfCertificate;
import com.multicert.v2x.datastructures.certificate.SequenceOfPsidGroupPermissions;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationValidationRequest;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.SharedAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.EcSignature;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcRequest;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941Data;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent;
import com.multicert.v2x.datastructures.message.encrypteddata.RecipientInfo.RecipientInfoChoices;
import com.multicert.v2x.datastructures.message.encrypteddata.EncryptedDataEncryptionKey.EncryptedDataEncryptionKeyTypes;
import com.multicert.v2x.datastructures.message.encrypteddata.*;
import com.multicert.v2x.datastructures.message.secureddata.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * This class generates secured data such as encrypted and signed data, also it decrypts and verifies signed data
 * An object of SecuredDataGenerator should be instantiated per message decryption/verification
 */
public class SecuredDataGenerator
{
    private static final int CURRENT_VERSION = EtsiTs103097Data.CURRENT_VERSION;
    protected CryptoHelper cryptoHelper;

    private InnerEcRequest innerEcRequest;
    private InnerEcResponse innerEcResponse;
    private SecretKey sharedKey; //a secret key that is shared by the vehicle to the CA
    private InnerAtRequest innerAtRequest; //The enrollment validation request that will be send to the EA by the AA
    private InnerAtResponse innerAtResponse;

    private SharedAtRequest sharedAtRequest;
    private EcSignature ecSignature;


    public SecuredDataGenerator(CryptoHelper cryptoHelper)
    {
        this.cryptoHelper = cryptoHelper;
    }


    /**
     *  Method to generate a EtsiTs103097Data-SignedExternalPayload,  where only a hash of the data is included as reference.
     * @param hashType the hash algorithm
     * @param message the message data to sign.
     * @param headerInfo the header information to include
     * @param signerCertificate the certificate of the signer, null if self-signed
     * @param signerPrivateKey the private key to sign the message
     * @param signingAlgorithm the digital signature algorithm to use
     * @return
     */
    public EtsiTs103097Data createSignedExternalHash(HashAlgorithm hashType, byte[] message, HeaderInfo headerInfo, EtsiTs103097Certificate signerCertificate, PrivateKey signerPrivateKey, AlgorithmType signingAlgorithm) throws IOException, SignatureException, NoSuchAlgorithmException
    {
        HashAlgorithm hashedId = hashType;

        HashedData hashedData = new HashedData(getHashedDataTypes(hashType), cryptoHelper.digest(message, hashType));
        ToBeSignedData toBeSignedData = new ToBeSignedData(new SignedDataPayload(null, hashedData), headerInfo);

        SignerIdentifier signer;
        Signature signature;
        if(signerCertificate == null)
        {
            signer = new SignerIdentifier(); // set to SELF
        }
        else
        {
            signer = new SignerIdentifier(cryptoHelper.getCertificateHashId(signerCertificate, hashType)); // set to DIGEST


        }

        signature = cryptoHelper.signMessage(toBeSignedData.getEncoded(), signerPrivateKey, signerCertificate, signingAlgorithm);

        SignedData signedData = new SignedData(hashedId, toBeSignedData, signer, signature);

        EtsiTs103097Content etsiTs103097Content = new EtsiTs103097Content(signedData);
        return new EtsiTs103097Data(etsiTs103097Content);
    }

    /**
     * Help method to return the available hashed data types
     * @return the related HashDataChoice used in referenced signed data.
     */
    protected HashedData.HashedDataTypes getHashedDataTypes(HashAlgorithm hashType)
    {
        switch (hashType)
        {
            default:
                return HashedData.HashedDataTypes.SHA256_HASHED_DATA;
        }
    }

    /**
    * Method to generate an EtsiTs103097Data-Signed structure.
    * @param hashType the hash algorithm
    * @param message the message data to sign.
    * @param headerInfo the header information to include
    * @param signerCertificate the certificate of the signer, null if self-signed
    * @param signerPrivateKey the private key to sign the message
    * @param signingAlgorithm the digital signature algorithm to use
    * @param includeCert true if the full certificate present in the signerIdentifier, false to include a digest
    */
    public EtsiTs103097Data createSignedData(HashAlgorithm hashType, byte[] message, HeaderInfo headerInfo, EtsiTs103097Certificate signerCertificate, PrivateKey signerPrivateKey, AlgorithmType signingAlgorithm, boolean includeCert) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        HashAlgorithm hashedId = hashType;

        EtsiTs103097Data unsecuredData = new EtsiTs103097Data(CURRENT_VERSION, new EtsiTs103097Content(EtsiTs103097Content.EtsiTs103097ContentChoices.UNSECURED_DATA, new Opaque(message)));
        ToBeSignedData toBeSignedData = new ToBeSignedData(new SignedDataPayload(unsecuredData, null), headerInfo);

        SignerIdentifier signer;
        Signature signature;
        if(signerCertificate == null)
        {
            signer = new SignerIdentifier(); // set to SELF

        }
        else
        {
            if(includeCert)
            {
                EtsiTs103097Certificate[] signerCert = {signerCertificate};
                signer = new SignerIdentifier(new SequenceOfCertificate(signerCert)); // set to CERTIFICATE
            }
            else
            {
                signer = new SignerIdentifier(cryptoHelper.getCertificateHashId(signerCertificate, hashType)); // set to DIGEST
            }

        }

        signature = cryptoHelper.signMessage(toBeSignedData.getEncoded(), signerPrivateKey, signerCertificate, signingAlgorithm);

        SignedData signedData = new SignedData(hashedId, toBeSignedData, signer, signature);

        EtsiTs103097Content etsiTs103097Content = new EtsiTs103097Content(signedData);
        return new EtsiTs103097Data(etsiTs103097Content);
    }

    /**
     * Method used to verify an ECRequest, when the vehicle does not have a certificate (see com.multicert.v2x.generators.certificaterequest fro more information)
     * This method verifies both signed data structures of the enrollment request.
     * The outer signature is verified with the vehicle's canonical public key. (identify the vehicle)
     * The inner signature is verified with the public key that exists on the InnerEcRequest structure. (proof of possession of the verification key)
     * @param encodedSignedData the encoded decrypted Enrollment Credential Request (OuterEcRequest)
     * @param canonicalPubKey the vehicle's canonical public key to verify the OuterEcRequest
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedRequest(byte[] encodedSignedData, PublicKey canonicalPubKey) throws IllegalArgumentException, SignatureException, ImcompleteRequestException, BadContentTypeException, IOException, InvalidSignatureException, InvalidKeySpecException
    {
        EtsiTs103097Data signedRequest;
        try {
            signedRequest =  new EtsiTs103097Data(encodedSignedData);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error verifying EcRequest, EcRequest is badly formed");
        }
        verifySignedRequest(signedRequest, canonicalPubKey);
    }

    /**
     * Method used to verify an ECRequest, when the vehicle does not have a certificate (see com.multicert.v2x.generators.certificaterequest fro more information)
     * This method verifies both signed data structures of the enrollment request.
     * The outer signature is verified with the vehicle's canonical public key. (identify the vehicle)
     * The inner signature is verified with the public key that exists on the InnerEcRequest structure. (proof of possession of the verification key)
     * @param signedData the decrypted Enrollment Credential Request (OuterEcRequest)
     * @param canonicalPubKey the vehicle's canonical public key to verify the OuterEcRequest
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedRequest(EtsiTs103097Data signedData, PublicKey canonicalPubKey) throws IllegalArgumentException, ImcompleteRequestException, BadContentTypeException, IOException, InvalidSignatureException
    {
        if(signedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
        {
            throw new BadContentTypeException("Error verifying outer signature: Only signed EtsiTs103097Data can verified");
        }
        SignedData sd = (SignedData) signedData.getContent().getValue();
        EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();
        if(payloadData == null){
            throw new ImcompleteRequestException("Error verifying outer signature, no payload data found");
        }

        Boolean outerSignatureResult = false;
        //try to verify the outer signature using the shared vehicle canonical public key
        try
        {
            outerSignatureResult = cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), canonicalPubKey);

        }
        catch (Exception e)
        {
            throw new InvalidSignatureException("problem while verifying outer signature");
        }

         //Get to the inner signed structure
        EtsiTs102941Data etsiTs102941Data =  new EtsiTs102941Data(((Opaque) payloadData.getContent().getValue()).getData());
        if(etsiTs102941Data.getContent().getType() != EtsiTs102941DataContent.EtsiTs102941DataContentTypes.ENROLLMENT_REQUEST)
        {
            throw new BadContentTypeException("Error verifying EcRequest: specified structure is not and enrollment credential request");
        }
        EtsiTs103097Data InnerECRequestSignedForPOP = (EtsiTs103097Data)etsiTs102941Data.getContent().getValue();

        Boolean innerSignatureResult = false;
        //try Verify the inner signed  structure
        try
        {
            innerSignatureResult = verifyEcPOPSignature(InnerECRequestSignedForPOP);
        }
        catch (Exception e)
        {
            throw new InvalidSignatureException("problem while verifying inner signature");
        }

        //If a signature is invalid the whole request is invalid
        if(!(outerSignatureResult & innerSignatureResult))
        {
            throw new InvalidSignatureException("The signature does not verify");
        }
    }

    /**
     * Methot hat verifies the inner signature of the enrollment request See package com.multicert.v2x.generators.certificaterequest;

     * @return
     */
    private Boolean verifyEcPOPSignature(EtsiTs103097Data InnerECRequestSignedForPOP) throws SignatureException, BadContentTypeException, ImcompleteRequestException, IOException, InvalidKeySpecException
    {
        if(InnerECRequestSignedForPOP.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA){
            throw new BadContentTypeException("Error verifying POP signature: Only signed EtsiTs103097Data can verified");
        }

            SignedData sd = (SignedData) InnerECRequestSignedForPOP.getContent().getValue();
            EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();
            if (payloadData == null)
            {
                throw new ImcompleteRequestException("Error verifying POP signature, no payload data found");
            }
            //Get the verification key to verify POP signature
            try
            {
                innerEcRequest = new InnerEcRequest(((Opaque) payloadData.getContent().getValue()).getData());
            }catch(Exception e)
            {
                throw new BadContentTypeException("Error verifying EcRequest: specified structure is not and authorization ticket request");
            }
            PublicKey verificationKey = getECRequestPublicKey();


            return cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), verificationKey);


    }

    /**
     * Methot hat verifies the POP signed structure of the authorization request See package com.multicert.v2x.generators.certificaterequest; (used by the AA)

     * @return
     */
    public void verifyAtPOPSignature(byte[] encodedSignedRequest) throws SignatureException, BadContentTypeException, ImcompleteRequestException, IOException, InvalidKeySpecException, InvalidSignatureException
    {

        EtsiTs103097Data signedRequest;
        try {
            signedRequest =  new EtsiTs103097Data(encodedSignedRequest);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error verifying AtRequest, AtRequest is badly formed");
        }

         verifyAtPOPSignature(signedRequest);

    }

        /**
         * Methot hat verifies the POP signed structure of the authorization request See package com.multicert.v2x.generators.certificaterequest; (used by the AA)

         * @return
         */
    public void verifyAtPOPSignature(EtsiTs103097Data signedRequest) throws SignatureException, BadContentTypeException, ImcompleteRequestException, IOException, InvalidKeySpecException, InvalidSignatureException
    {
        if(signedRequest.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA){
            throw new BadContentTypeException("Error verifying POP signature: Only signed EtsiTs103097Data can verified");
        }

        SignedData sd = (SignedData) signedRequest.getContent().getValue();
        EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();

        if (payloadData == null)
        {
            throw new ImcompleteRequestException("Error verifying POP signature, no payload data found");
        }

        //Get the verification key to verify POP signature
        EtsiTs102941Data etsiTs102941Data;
        try
        {
            etsiTs102941Data  = new EtsiTs102941Data(((Opaque) payloadData.getContent().getValue()).getData());
            if(etsiTs102941Data.getContent().getType() != EtsiTs102941DataContent.EtsiTs102941DataContentTypes.AUTHORIZATION_REQUEST)
            {
                throw new BadContentTypeException("Error verifying AtRequest: wrong type of EtsiTs102941 data");
            }
            innerAtRequest = (InnerAtRequest)etsiTs102941Data.getContent().getValue();

        }catch (Exception e)
        {
            throw new BadContentTypeException("Error verifying AtRequest: specified structure is not and authorization ticket request");

        }

        sharedAtRequest = innerAtRequest.getSharedAtRequest();
        ecSignature = innerAtRequest.getEcSignature();

        PublicKey verificationKey = getATRequestPublicKey();

        if(!cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), verificationKey))
        {
            throw new InvalidSignatureException("The signature does not verify");
        }
    }

    /**
     * Method that gets the hashed id of the enrollment certificate in the authorization validation request (used by the EA)
     * This method also initializes the EcSignature and sharedAtRequest variables for this verification of enrollment
     */
    public byte[] getEcSignatureSignerId(AuthorizationValidationRequest authorizationValidationRequest) throws BadContentTypeException
    {
        ecSignature = authorizationValidationRequest.getEcSignature();
        sharedAtRequest = authorizationValidationRequest.getSharedAtRequest();

        SignedData signedData = getEcSignatureSignData();

        SignerIdentifier signerId = signedData.getSigner();

        if(signerId.getType() != SignerIdentifier.SignerIdentifierChoices.DIGEST)
        {
            throw new BadContentTypeException("Error getting Ec signature signer id: the signer identifier is not an enrollment credential digest");
        }

        return ((HashedId8) signerId.getValue()).getData();

    }



    /**
     * Method hat verifies the enrollment signature of a given vehicle (used by the EA)
     * First, this methot validates the enrollment certificate.
     * Then it verifies that the vehicle's signature can be decrypted using enrollment verification key on the certificate
     * //TODO add suport for encrypted eSignature (ITSS_WITH_PRIVACY)
     */
    public void verifyEnrollmentSignature(EtsiTs103097Certificate enrollmentCredential) throws InvalidSignatureException, BadContentTypeException
    {

        SignedData signedData = getEcSignatureSignData();
        try
        {
            cryptoHelper.verifySignature(signedData.getTbsData().getEncoded(), signedData.getSignature(), enrollmentCredential);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new InvalidSignatureException("Error verifying vehicle enrollment: the signature does not verify");

        }
    }

    private SignedData getEcSignatureSignData() throws BadContentTypeException
    {

        if(ecSignature.getChoice() == EcSignature.EcSignatureTypes.EC_SIGNATURE)
        {
            EtsiTs103097Data etsiTs103097Data = (EtsiTs103097Data)ecSignature.getValue();
            return (SignedData) etsiTs103097Data.getContent().getValue();

        }
        else
        {
            //TODO add suport for encrypted eSignature (ITSS_WITH_PRIVACY)
            return null;
        }
    }

    /**
     * Method used to verify an ECresponse,
     * @param encodedSignedData  encoded decrypted Enrollment Credential Response
     * @param eaCertificate the certificate of the trusted Enrollment Authority
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedEcResponse(byte[] encodedSignedData, EtsiTs103097Certificate eaCertificate) throws IllegalArgumentException, ImcompleteRequestException, BadContentTypeException, IOException, InvalidSignatureException, InvalidKeySpecException, SignatureException
    {

        EtsiTs103097Data signedRequest;
        try {
            signedRequest =  new EtsiTs103097Data(encodedSignedData);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error verifying EcResponse, EcResponse is badly formed");
        }

        verifySignedEcResponse(signedRequest, eaCertificate);
    }

    /**
     * Method used to verify an ECresponse,
     * @param signedData the decrypted Enrollment Credential Response
     * @param eaCertificate the certificate of the trusted Enrollment Authority
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedEcResponse(EtsiTs103097Data signedData, EtsiTs103097Certificate eaCertificate) throws IllegalArgumentException, ImcompleteRequestException, BadContentTypeException, InvalidSignatureException, IOException
    {
        if(signedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
        {
            throw new BadContentTypeException("Error verifying EC response: Only signed EtsiTs103097Data can verified");
        }
        SignedData sd = (SignedData) signedData.getContent().getValue();
        EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();
        if(payloadData == null){
            throw new ImcompleteRequestException("Error verifying outer signature, no payload data found");
        }

        Boolean result = false;
        //try to verify the EA's signature
        try
        {
            result = cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), eaCertificate);

        }
        catch (Exception e)
        {
            throw new InvalidSignatureException("problem while verifying outer signature");
        }

        //If a signature is invalid the response is invalid
        if(!result)
        {
            throw new InvalidSignatureException("The signature does not verify");
        }

        EtsiTs102941Data etsiTs102941Data =  new EtsiTs102941Data(((Opaque) payloadData.getContent().getValue()).getData());
        if(etsiTs102941Data.getContent().getType() != EtsiTs102941DataContent.EtsiTs102941DataContentTypes.ENROLLMENT_RESPONSE)
        {
            throw new BadContentTypeException("Error verifying EcResponse: specified structure is not and enrollment credential response");
        }

        innerEcResponse = (InnerEcResponse) etsiTs102941Data.getContent().getValue();

    }

    /**
     * Method used to verify an ATresponse,
     * @param encodedSignedData  decrypted authorization ticket Response
     * @param aaCertificate the certificate of the trusted authorization authority
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedAtResponse(byte[] encodedSignedData, EtsiTs103097Certificate aaCertificate) throws IllegalArgumentException, ImcompleteRequestException, BadContentTypeException, IOException, InvalidSignatureException, InvalidKeySpecException, SignatureException
    {

        EtsiTs103097Data signedRequest;
        try {
            signedRequest =  new EtsiTs103097Data(encodedSignedData);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error verifying EcResponse, EcResponse is badly formed");
        }

        verifySignedAtResponse(signedRequest, aaCertificate);
    }

    /**
     * Method used to verify an ATresponse,
     * @param signedData  encoded decrypted authorization ticket Response
     * @param aaCertificate the certificate of the trusted authorization authority
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public void verifySignedAtResponse(EtsiTs103097Data signedData, EtsiTs103097Certificate aaCertificate) throws IllegalArgumentException, ImcompleteRequestException, BadContentTypeException, InvalidSignatureException, IOException
    {
        if(signedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
        {
            throw new BadContentTypeException("Error verifying EC response: Only signed EtsiTs103097Data can verified");
        }
        SignedData sd = (SignedData) signedData.getContent().getValue();
        EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();
        if(payloadData == null){
            throw new ImcompleteRequestException("Error verifying outer signature, no payload data found");
        }

        Boolean result = false;
        //try to verify the EA's signature
        try
        {
            result = cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), aaCertificate);

        }
        catch (Exception e)
        {
            throw new InvalidSignatureException("problem while verifying outer signature");
        }

        //If a signature is invalid the response is invalid
        if(!result)
        {
            throw new InvalidSignatureException("The signature does not verify");
        }

        EtsiTs102941Data etsiTs102941Data =  new EtsiTs102941Data(((Opaque) payloadData.getContent().getValue()).getData());
        if(etsiTs102941Data.getContent().getType() != EtsiTs102941DataContent.EtsiTs102941DataContentTypes.AUTHORIZATION_RESPONSE)
        {
            throw new BadContentTypeException("Error verifying atResponse: specified structure is not and authorization ticket response");
        }

        innerAtResponse = (InnerAtResponse) etsiTs102941Data.getContent().getValue();

    }

    /**
     * Method to extract the signer identifier of a signed message
     * @param signedData
     * @return
     * @throws BadContentTypeException
     */
    public SignerIdentifier getSignerId(EtsiTs103097Data signedData) throws BadContentTypeException
    {
        if(signedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
        {
            throw new BadContentTypeException("Error verifying EC response: Only signed EtsiTs103097Data can verified");
        }
        SignedData sd = (SignedData) signedData.getContent().getValue();
        return sd.getSigner();
    }

    /**
     * Method used to verify a Cooperative Awareness Message
     * @param signedData the CAM to verify
     * @param authorizationTicket The authorization ticket of the signing vehicle
     */
    public void verifyCAM(EtsiTs103097Data signedData, EtsiTs103097Certificate authorizationTicket) throws BadContentTypeException, ImcompleteRequestException, InvalidSignatureException
    {
        if(signedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
        {
            throw new BadContentTypeException("Error verifying EC response: Only signed EtsiTs103097Data can verified");
        }
        SignedData sd = (SignedData) signedData.getContent().getValue();
        EtsiTs103097Data payloadData = sd.getTbsData().getPayload().getData();

        if(payloadData == null){
            throw new ImcompleteRequestException("Error verifying outer signature, no payload data found");
        }

        Boolean result = false;
        //try to verify the EA's signature
        try
        {
            result = cryptoHelper.verifySignature(sd.getTbsData().getEncoded(), sd.getSignature(), authorizationTicket);

        }
        catch (Exception e)
        {
            throw new InvalidSignatureException("problem while verifying outer signature");
        }
    }

    /**
     * Help method that extracts a public key from a trusted certificate
     */
    private PublicKey getSignerPublicKey(EtsiTs103097Certificate signerCertificate) throws InvalidKeySpecException
    {
        PublicVerificationKey  pubVerKey = (PublicVerificationKey) signerCertificate.getToBeSigned().getVerifyKeyIndicator().getValue();
        AlgorithmType alg = pubVerKey.getType();
        return (PublicKey) cryptoHelper.eccPointToPublicKey(alg, (EccP256CurvePoint) pubVerKey.getValue());
    }

    /**
     * Method that returns the innerECRequest structure
     * @throws IOException
     */
    public InnerEcRequest getInnerEcRequest() throws IOException
    {
        return innerEcRequest;
    }

    /**
     * Method that returns the innerECRequest structure
     * @throws IOException
     */
    public InnerEcResponse getInnerEcResponse() throws IOException
    {
        return innerEcResponse;
    }

    /**
     * Help method to read the ITS public key from the decrypted and verified enrollment request
     * @return
     */
    public PublicKey getECRequestPublicKey() throws InvalidKeySpecException
    {
        AlgorithmType sigAlgorithm = innerEcRequest.getPublicKeys().getVerificationKey().getType();
        EccP256CurvePoint point = (EccP256CurvePoint) innerEcRequest.getPublicKeys().getVerificationKey().getValue();

        PublicKey verificationKey = (PublicKey) cryptoHelper.eccPointToPublicKey(sigAlgorithm,point);
        return verificationKey;
    }

    /**
     * Help method to read the ITS public key from the decrypted and verified authorization request
     * @return
     */
    public PublicKey getATRequestPublicKey() throws InvalidKeySpecException
    {
        AlgorithmType sigAlgorithm = innerAtRequest.getPublicKeys().getVerificationKey().getType();
        EccP256CurvePoint point = (EccP256CurvePoint) innerAtRequest.getPublicKeys().getVerificationKey().getValue();

        PublicKey verificationKey = (PublicKey) cryptoHelper.eccPointToPublicKey(sigAlgorithm,point);
        return verificationKey;
    }

    /**
     * Help method to read requested cert issue permissions the decrypted and verified request
     * @return
     */
    public SequenceOfPsidGroupPermissions getCertIssuePermissions()
    {

        return innerEcRequest.getRequestedSubjectAttributes().getCertIssuePermissions();
    }




    /**
     * Method that encrypts data to a list of recipients
     *
     */
    public EtsiTs103097Data createEncryptedData(SequenceOfRecipientInfo sequenceOfRecipientInfo, byte[] tobeEncryptedData, AlgorithmType alg, SecretKey symmKey) throws IOException, GeneralSecurityException
    {
        byte[] nounce = cryptoHelper.generateNounce(alg);
        byte[] cipherText = cryptoHelper.encryptSymmetric(alg, symmKey, nounce, tobeEncryptedData);

        AesCcmCiphertext aesCcmCiphertext = new AesCcmCiphertext(nounce,cipherText);
        SymmetricCiphertext symmetricCiphertext = new SymmetricCiphertext(aesCcmCiphertext);
        EncryptedData encryptedData = new EncryptedData(sequenceOfRecipientInfo,symmetricCiphertext);

        EtsiTs103097Content etsiTs103097Content = new EtsiTs103097Content(encryptedData);
        return new EtsiTs103097Data(etsiTs103097Content);
    }


    /**
     * Method that decrypts data a to certificate receiver (CERT_RECIP_INFO)
     * @param  encodedEncryptedData the encoded data to decrypt
     * @param receiverCert the certificate of the receiver
     * @param privateKey the private key associated with the receiver certificate
     *
     */
    public byte[] decryptEncryptedData(byte[] encodedEncryptedData, EtsiTs103097Certificate receiverCert, PrivateKey privateKey) throws IOException, GeneralSecurityException, IncorrectRecipientException, BadContentTypeException, DecryptionException
    {
        EtsiTs103097Data encryptedData;
        try {
            encryptedData =  new EtsiTs103097Data(encodedEncryptedData);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error decrypting EcRequest, EcRequest is badly formed"); //if the request is badly formed, the EA can't build a response. The RA is notified of this by catching the exception.
        }
        return decryptEncryptedData(encryptedData, receiverCert,privateKey);
    }



    /**
     * Method that decrypts data a to certificate receiver (CERT_RECIP_INFO)
     * @param  encryptedData the data to decrypt
     * @param receiverCert the certificate of the receiver
     * @param privateKey the private key associated with the receiver certificate
     *
     */
    public byte[] decryptEncryptedData(EtsiTs103097Data encryptedData, EtsiTs103097Certificate receiverCert, PrivateKey privateKey) throws IOException, GeneralSecurityException, IncorrectRecipientException, BadContentTypeException, DecryptionException
    {


        if(encryptedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.ENCRYPTED_DATA){

            throw new BadContentTypeException("Error decrypting EncryptedData: invalid data type " + encryptedData.getContent().getType() +" only the type ENCRYPTED_DATA can be decrypted.");
        }

        EncryptedData data = (EncryptedData) encryptedData.getContent().getValue();
        COEREncodable[] recipientInfos = data.getRecipients().getValues();

        SecretKey decryptionKey = null;
        HashedId8 receiverId = cryptoHelper.getCertificateHashId(receiverCert,HashAlgorithm.SHA_256); //generate the Id for the receiver certificate

        for(COEREncodable ri : recipientInfos)
        {
            RecipientInfo recipientInfo = (RecipientInfo) ri;

            if(recipientInfo.getType() != RecipientInfoChoices.CERT_RECIP_INFO)
            {
                throw new BadContentTypeException("Error decrypting EncryptedData: invalid  recipient info type found " +  recipientInfo.getType() + " should be "+ RecipientInfoChoices.CERT_RECIP_INFO);
            }

            HashedId8 reference = getRecipientId(recipientInfo); // get the recipient Id present in the encrypted data
            System.out.println("Expecting: " + receiverId + " Inside Request: "+ reference);
            if(receiverId.toString().equals(reference.toString())) //if they match, the encrypted data was sent to this specific receiverCert
            {
                try
                {
                    PKRecipientInfo pkRecInfo = (PKRecipientInfo) recipientInfo.getValue();
                    decryptionKey = cryptoHelper.eceisDecryptSymmetricKey(pkRecInfo.getEncKey(), privateKey, pkRecInfo.getEncKey().getType());
                    sharedKey = decryptionKey;

                    SymmetricCiphertext symmetricCiphertext = data.getCipherText();

                    return cryptoHelper.symmetricDecrypt(symmetricCiphertext.getType(), getEncryptedData(symmetricCiphertext), decryptionKey, getNounce(symmetricCiphertext));
                }catch(Exception e)
                {
                    e.printStackTrace();
                    throw new DecryptionException("Error decrypting data");
                }

            }
        }

        throw new IncorrectRecipientException("Error decrypting data, no matching receiver info could be found to retrieve the decryption key.");

    }


    /**
     * Method that decrypts data a to symmetric receiver (PSK_RECIP_INFO)
     * @param encodedEncryptedData the encoded data to decrypt
     *
     */
    public byte[] decryptEncryptedData(byte[] encodedEncryptedData, SecretKey sharedKey) throws IOException, GeneralSecurityException, IncorrectRecipientException, BadContentTypeException, DecryptionException
    {

        EtsiTs103097Data encryptedData;
        try {
            encryptedData =  new EtsiTs103097Data(encodedEncryptedData);
        }
        catch(Exception e)
        {
            throw new BadContentTypeException("Error decrypting EcRequest, EcRequest is badly formed"); //if the request is badly formed, the EA can't build a response. The RA is notified of this by catching the exception.
        }
        return decryptEncryptedData(encryptedData, sharedKey);
    }

    /**
     * Method that decrypts data a to symmetric receiver (PSK_RECIP_INFO)
     * @param  encryptedData the data to decrypt
     *
     */
    public byte[] decryptEncryptedData(EtsiTs103097Data encryptedData, SecretKey sharedKey) throws IOException, GeneralSecurityException, IncorrectRecipientException, BadContentTypeException, DecryptionException
    {


        if(encryptedData.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.ENCRYPTED_DATA){

            throw new BadContentTypeException("Error decrypting EncryptedData: invalid data type " + encryptedData.getContent().getType() +" only the type ENCRYPTED_DATA can be decrypted.");
        }

        EncryptedData data = (EncryptedData) encryptedData.getContent().getValue();
        COEREncodable[] recipientInfos = data.getRecipients().getValues();

        HashedId8 sharedKeyId = cryptoHelper.getSecretKeyHashId(sharedKey, HashAlgorithm.SHA_256); //generate the Id for enrollmentSharedKey

        for(COEREncodable ri : recipientInfos)
        {
            RecipientInfo recipientInfo = (RecipientInfo) ri;

            if(recipientInfo.getType() != RecipientInfoChoices.PSK_RECIP_INFO)
            {
                throw new BadContentTypeException("Error decrypting EncryptedData: invalid  recipient info type found " +  recipientInfo.getType() + " should be "+ RecipientInfoChoices.PSK_RECIP_INFO);
            }

            HashedId8 reference = getRecipientId(recipientInfo); // get the recipient Id present in the encrypted data

            if(sharedKeyId.toString().equals(reference.toString())) //if they match, the encrypted data was sent to this shared key
            {
                try
                {
                    SymmetricCiphertext symmetricCiphertext = data.getCipherText();

                    return cryptoHelper.symmetricDecrypt(symmetricCiphertext.getType(), getEncryptedData(symmetricCiphertext), sharedKey, getNounce(symmetricCiphertext));
                }catch(Exception e)
                {
                    throw new DecryptionException("Error decrypting data");
                }

            }
        }

        throw new IncorrectRecipientException("Error decrypting data, no matching receiver info could be found to retrieve the decryption key.");

    }

    public SecretKey getSharedKey()
    {
        return sharedKey;
    }


    //TODO TEST THIS
    /**
     * Help method that creates the enrollment request hash to be included in  the enrollment response
     * @param request the request from which to create digest
     */
    public COEROctetString createRequestHash(EtsiTs103097Data request) throws IOException, NoSuchAlgorithmException
    {
        byte[] fullDigest = cryptoHelper.digest(request.getEncoded(),HashAlgorithm.SHA_256);
        byte[] result = new byte[16];
        System.arraycopy(fullDigest, 0, result, 0, 16);
        return new COEROctetString(result,16,16);
    }

    /**


    /**
     * This method generates a recipient info of type CERT_RECIP_INFO (encrypt to a certificate holder)
     *
     */
    public RecipientInfo genRecipientInfo(EtsiTs103097Certificate recipientCert, SecretKey symmKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        if(recipientCert.getTbsCert().getEncryptionKey() == null){
            throw new IllegalArgumentException("Error encrypting certificate request: The certificate cannot be used as encryption receipient, no public encryption key found.");
        }

        HashedId8 recipientId = cryptoHelper.getCertificateHashId(recipientCert, HashAlgorithm.SHA_256);
        PublicEncryptionKey publicEncryptionKey = recipientCert.getTbsCert().getEncryptionKey();
        AlgorithmType publicKeyAlg = publicEncryptionKey.getPublicKey().getType();
        PublicKey certificateEncKey = (PublicKey) cryptoHelper.eccPointToPublicKey(publicKeyAlg, (EccP256CurvePoint) publicEncryptionKey.getPublicKey().getValue());

        EncryptedDataEncryptionKey encryptedKey = cryptoHelper.eceisEncryptSymmetricKey(getEnckeyType(publicKeyAlg), publicEncryptionKey.getSupportedSymmalgorith(),certificateEncKey,symmKey);

        PKRecipientInfo pkRecipientInfo = new PKRecipientInfo(recipientId, encryptedKey);

        return new RecipientInfo(RecipientInfoChoices.CERT_RECIP_INFO, pkRecipientInfo);
    }

    /**
     * This method generates a recipient info of type PSK_RECIP_INFO (encrypt to a pre-shared-key recipient)
     *
     */
    public RecipientInfo genRecipientInfo(SecretKey preSharedKey) throws NoSuchAlgorithmException
    {
       return new RecipientInfo(new PreSharedKeyRecipientInfo(cryptoHelper.digest(preSharedKey.getEncoded(),HashAlgorithm.SHA_256)));
    }

    /**
     * Help method that returns a reference to a recipient from the recipient information
     */
    protected HashedId8 getRecipientId(RecipientInfo recipientInfo) {
        switch (recipientInfo.getType()) {
            case PSK_RECIP_INFO:
                return (PreSharedKeyRecipientInfo) recipientInfo.getValue();
            case SYMM_RECIP_INFO:
                SymmRecipientInfo sri = (SymmRecipientInfo) recipientInfo.getValue();
                return sri.getRecipientId();
            case CERT_RECIP_INFO:
            case SIGNED_DATA_RECIP_INFO:
            case REK_RECIP_INFO:
                PKRecipientInfo pri = (PKRecipientInfo) recipientInfo.getValue();
                return pri.getRecipientId();
            default:
        }
        throw new IllegalArgumentException("Unknown RecipientInfo type: " + recipientInfo.getType());
    }

    //TODO other forms of RecipientInfo

    public EncryptedDataEncryptionKeyTypes getEnckeyType (AlgorithmType alg)
    {
        if(alg.getAlgorithm().getSignature() == null){
            throw new IllegalArgumentException("Error unsupported algorithm: " + alg);
        }

        switch(alg.getAlgorithm().getEncryption()){
            case ECIES_Nist_P256:
                return EncryptedDataEncryptionKeyTypes.ECIES_NIST_P256;
            case ECIES_BRAINPOOL_P256R1:
            default:
                return EncryptedDataEncryptionKeyTypes.ECIES_BRAINPOOL_P256R1;
        }
    }

    public static byte[] getEncryptedData(SymmetricCiphertext symmetricCiphertext) {
        switch (symmetricCiphertext.getType()) {
            default:
            case AES_128_CCM:
                AesCcmCiphertext aesCcmCiphertext = (AesCcmCiphertext) symmetricCiphertext.getValue();
                return aesCcmCiphertext.getCcmCipherText();
        }
    }

    public static byte[] getNounce(SymmetricCiphertext symmetricCiphertext) {
        switch (symmetricCiphertext.getType()) {
            default:
            case AES_128_CCM:
                AesCcmCiphertext aesCcmCiphertext = (AesCcmCiphertext) symmetricCiphertext.getValue();
                return aesCcmCiphertext.getNounce();
        }
    }

    public InnerAtRequest getInnerAtRequest()
    {
        return innerAtRequest;
    }

    public InnerAtResponse getInnerAtResponse()
    {
        return innerAtResponse;
    }

    public SharedAtRequest getSharedAtRequest()
    {
        return sharedAtRequest;
    }

    public EcSignature getEcSignature()
    {
        return ecSignature;
    }

}
