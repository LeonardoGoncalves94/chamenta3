package com.multicert.v2x.generators.certificaterequest;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.CertificateId;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificate.SubjectPermissions;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.CertificateSubjectAttributes;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.SharedAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.EcSignature;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcRequest;
import com.multicert.v2x.datastructures.certificaterequests.PublicKeys;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941Data;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent;
import com.multicert.v2x.datastructures.message.encrypteddata.RecipientInfo;
import com.multicert.v2x.datastructures.message.encrypteddata.SequenceOfRecipientInfo;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.HeaderInfo;
import com.multicert.v2x.generators.certificate.CertificateGenerator;
import com.multicert.v2x.generators.message.SecuredDataGenerator;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.util.Date;

/**
 * This class generates an authorization request as specified in ETSI 102-941, Section 6.2.3.2.1 standard
 * The request shall be sent by an ITS station to a authorization authority to request authorization tickets
 * It should be encrypted using the public key provided by the enrollment authority (in the certificate)
 * For each request, the ITS station should generate new verification keypairs
 * The requested subject attributes (innerAtRequest) of the EcRequest will appear on the authorization tickets , the validity period and
 * geographic region are controlled by the vehicle profiles that exist in the PKI and not by the ATrequest that vehicles send. The app permissions will express the permissions that
 * the vehicle has on the road and are alo controlled by the PKI. Cert issue permissions is set to null because vehicles do not issue certificates
 */
public class AuthorizationRequestGenerator extends SecuredDataGenerator
{
    private CertificateGenerator certificateGenerator; // needed to support some operations in the createInnerECRequest method



    public AuthorizationRequestGenerator(CryptoHelper cryptoHelper, boolean isCompressed)
    {
        super(cryptoHelper);
        certificateGenerator = new CertificateGenerator(cryptoHelper, isCompressed);
    }

    /**
     *
     * Method that generates an enrollment certificate request of a given ITS-S according to Etsi102 0941 standard
     * @param enrollmentCredential The enrollment credential that belongs to the vehicle
     * @param enrollmentSigningKey the private key that belongs to the enrollment credential
     * @param verificationKeys The newly generated verification key pair to be certified in the authorization ticket certificate. Required.
     * @param verificationAlgorithm The algorithm used for signing and verification by the ITS-S. Required.
     * @param validityPeriod The Validity period of the requested enrollment certificate. Optional.
     * @param geographicRegion The Validity region of the requested enrollment certificate. Optional.
     * @param appPermissions The requested app permissions for this vehicle. Optional.
     * @param assuranceLevel The assurance level of the requested enrollment certificate. Required
     * @param confidenceLevel The Confidence level of the requested enrollment certificate. Required.
     * @param AASharedKey the symmetric key to share with the AA. Required
     * @param EASharedKey The symmetric key to share with the EA. Optional, null for request with no privacy (i.e AA can see a hashId reference of he vehicle's enrollment credential contained in the signedExternalPayload)
     * @return The Enrollment certificate request signed and encrypted
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     */
    public EtsiTs103097Data generateAtRequest(EtsiTs103097Certificate enrollmentCredential, PrivateKey enrollmentSigningKey, AlgorithmType enrollmentAlgorithm, KeyPair verificationKeys, AlgorithmType verificationAlgorithm,
                                              EtsiTs103097Certificate AaCertificate, EtsiTs103097Certificate EaCertificate, ValidityPeriod validityPeriod, GeographicRegion geographicRegion, PsidSsp[] appPermissions,
                                              int assuranceLevel, int confidenceLevel, SecretKey AASharedKey, SecretKey EASharedKey ) throws IOException, GeneralSecurityException
    {
        PublicKey verificationKey = verificationKeys.getPublic();
        PrivateKey signingKey = verificationKeys.getPrivate();


        SharedAtRequest sharedAtRequest = createSharesAtRequest(EaCertificate,validityPeriod,geographicRegion, appPermissions, assuranceLevel,confidenceLevel);
        EcSignature ecSignature = createEcSignature(sharedAtRequest, enrollmentCredential, enrollmentSigningKey, null,  enrollmentAlgorithm);

        InnerAtRequest innerAtRequest = createInnerAtRequest(verificationKey,verificationAlgorithm, sharedAtRequest, ecSignature);

        EtsiTs102941Data etsiTs102941Data = createEtsiTs102941Data(innerAtRequest);

        EtsiTs103097Data POPSignature = createRequestSignedData(etsiTs102941Data.getEncoded(),null,signingKey, verificationAlgorithm);

        return createRequestEncryptedData(POPSignature.getEncoded(),AaCertificate,AASharedKey);

    }

    /**creates enrollment credential signature
     *
     * @param sharedAtRequest
     * @param enrollmentCredential
     * @param enrollCredentialPrivateKEy
     * @param signingAlgorithm
     * @return
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     */
    private EcSignature createEcSignature(SharedAtRequest sharedAtRequest, EtsiTs103097Certificate enrollmentCredential, PrivateKey enrollCredentialPrivateKEy, SecretKey EASharedKey, AlgorithmType signingAlgorithm) throws IOException, SignatureException, NoSuchAlgorithmException
    {
        HashAlgorithm hashAlgorithm = HashAlgorithm.SHA_256;
        HeaderInfo headerInfo = new HeaderInfo(new Psid(36),new Time64(new Date()),null, null, null, null,null); // the Psid element of the header is set to "CA Basic Service" because the "secured certificate request" cannot be found in ETSI TS 102 965 at the present time.

        EtsiTs103097Data signedExternalPayload = new EtsiTs103097Data();
        signedExternalPayload = createSignedExternalHash(hashAlgorithm,sharedAtRequest.getEncoded(),headerInfo, enrollmentCredential,enrollCredentialPrivateKEy,signingAlgorithm);

        if(EASharedKey != null) // if the user passes a symm ket to be share with the EA, then tis request goes with privacy
        {
            //TODO ENCRYPT THE signedExternalPayload ( ENCRYPT EASharedKey WITH PUBLIC KEY OF THE EA) + (ENCRYPT signedExternalPayload WITH EASharedKey)
        }

        return new EcSignature(EcSignature.EcSignatureTypes.EC_SIGNATURE, signedExternalPayload);
    }

    /**
     * Method that created the InnerAtRequest structure of the authorization certificate request.
     * @param verificationKey, the newly generated verification key to be certified by an authorization ticket
     */
    private InnerAtRequest createInnerAtRequest(PublicKey verificationKey, AlgorithmType signingAlgorithm, SharedAtRequest sharedAtRequest, EcSignature ecSignature) throws IOException
    {
        PublicVerificationKey publicVerificationKey = new PublicVerificationKey(certificateGenerator.getPublicVerificationKeyType(signingAlgorithm), certificateGenerator.convertToPoint(signingAlgorithm, verificationKey)); // type and key
        PublicKeys publicKeys = new PublicKeys(publicVerificationKey, null); // no encryption key, only the verification key to be certified in the Enrollment credential

        byte[] hmacKey = {1,2,3}; //TODO CHANGE THIS TO REAL H-MAC TAG

        return new InnerAtRequest(publicKeys,hmacKey, sharedAtRequest, ecSignature);
    }

    private SharedAtRequest createSharesAtRequest(EtsiTs103097Certificate enrollmentCAcertificate, ValidityPeriod validityPeriod, GeographicRegion geographicRegionegion, PsidSsp[] appPermissions, int assuranceLevel,
                                                  int confidenceLevel) throws IOException, NoSuchAlgorithmException
    {
        HashedId8 eaId = cryptoHelper.getCertificateHashId(enrollmentCAcertificate, HashAlgorithm.SHA_256);
        SubjectAssurance subjectAssurance = new SubjectAssurance(assuranceLevel, confidenceLevel);
        CertificateSubjectAttributes requestedSubjectAttributes;
        if(appPermissions == null)
        {
             requestedSubjectAttributes = new CertificateSubjectAttributes(null, validityPeriod, geographicRegionegion, subjectAssurance, null, null ); // vehicle uses the app permissions set by its profile at the PKI

        }
        else
        {
            SequenceOfPsidSsp appPerms = new SequenceOfPsidSsp(appPermissions);
            requestedSubjectAttributes = new CertificateSubjectAttributes(null, validityPeriod, geographicRegionegion, subjectAssurance, appPerms, null ); //vehicle requests new app permissions
        }
        byte[] keyTag = {1,2,3}; //TODO CHANGE THIS TO REAL H-MAC TAG

        return new SharedAtRequest(eaId,keyTag, requestedSubjectAttributes);
    }

    /**
     * This method creates the signed data structures of the certificate request:
     * The inner signed structure is to prove the possession (POP signature) of the newly generated verification key pair
     * The outer signed structure is done using the private key of the valid enrollment credential to prove the identity of the vehicle
     * @param message the data to be signed. Required.
     * @param privateKey the private key to sign the message
     * @param signingAlgorithm the digital signature algorithm
     *
     * @return the Signed data structure
     */
    private EtsiTs103097Data createRequestSignedData(byte[] message, EtsiTs103097Certificate enrollmentCredential, PrivateKey privateKey, AlgorithmType signingAlgorithm) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        HashAlgorithm hashAlgorithm = HashAlgorithm.SHA_256;
        HeaderInfo headerInfo = new HeaderInfo(new Psid(36),new Time64(new Date()),null, null, null,null,null); // the Psid element of the header is set to "CA Basic Service" because the "secured certificate request" cannot be found in ETSI TS 102 965 at the present time.
        return createSignedData(hashAlgorithm, message, headerInfo, enrollmentCredential, privateKey, signingAlgorithm, false);
    }

    /**
     * This method creates the EtsiTs102941Data structure of the authorization request.
     * @return
     */
    private EtsiTs102941Data createEtsiTs102941Data(InnerAtRequest InnerAtRequest) throws IOException
    {
        EtsiTs102941DataContent dataContent = new EtsiTs102941DataContent(InnerAtRequest);
        return new EtsiTs102941Data(dataContent);
    }

    /**
     * This method creates the encrypted data structure of the Ec request
     * @param POPSignature the POP structure to sign
     * @param AaCert specifies the certificate of the authorization CA which this request will be encrypted to
     * @param vehicleSecretKey A symmetric key that
     * @return the encrypted EC request
     * @throws IOException
     */
    private EtsiTs103097Data createRequestEncryptedData(byte[] POPSignature, EtsiTs103097Certificate AaCert, SecretKey vehicleSecretKey) throws IOException, GeneralSecurityException
    {
        RecipientInfo[] recipients = {genRecipientInfo(AaCert, vehicleSecretKey)};
        SequenceOfRecipientInfo sequenceOfRecipients = new SequenceOfRecipientInfo(recipients);

        return createEncryptedData(sequenceOfRecipients, POPSignature, SymmAlgorithm.AES_128_CCM, vehicleSecretKey);
    }

}
