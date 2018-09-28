package com.multicert.v2x.generators.certificaterequest;


import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.Psid;
import com.multicert.v2x.datastructures.base.SymmAlgorithm;
import com.multicert.v2x.datastructures.base.Time64;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.*;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941Data;
import com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent;
import com.multicert.v2x.datastructures.message.encrypteddata.RecipientInfo;
import com.multicert.v2x.datastructures.message.encrypteddata.SequenceOfRecipientInfo;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.HeaderInfo;
import com.multicert.v2x.generators.message.SecuredDataGenerator;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.util.Date;

/**
 * This class generates an Enrollment Certificate response as specified in ETSI 102-941, Section 6.2.3.2.1 standard.
 * The Enrollment Response message should be sent by the EA to the ITS station in response to a received enrollment request.
 * It should be encrypted with the same AES key as the one used by the ITS station to encrypt the request message.
 */
public class EnrollmentResponseGenerator extends SecuredDataGenerator
{


    public EnrollmentResponseGenerator(CryptoHelper cryptoHelper)
    {
        super(cryptoHelper);
    }

    /**
     * This method generates the enrollment response
     * @param request the signed request i.e. the outer signed structure (see InnerEcResponse class and ETSI TS 102 941 for more information for more information), REQUIRED
     * @param preSharedKey the pre-shared symmetric key present on the original request, REQUIRED
     * @param responseCode the result of the enrollment credential request validation
     * @param enrollmentCredential the enrollment credential to deliver to the ITS station, null if none, OPTIONAL
     * @param EAcertificate The certificate of the EA that will generate this response, REQUIRED
     * @param EAsigKeys The EA signature keys, the private key will be used to sign this enrollment response,  REQUIRED
     * @param signingAlgorithm The algorithm which will be used to sign this enrollment  response, REQUIRED
     */
    public EtsiTs103097Data generateEcResponse(EtsiTs103097Data request, SecretKey preSharedKey, EnrollmentResonseCode responseCode, EtsiTs103097Certificate enrollmentCredential,
                                               EtsiTs103097Certificate EAcertificate, KeyPair EAsigKeys, AlgorithmType signingAlgorithm) throws IOException, GeneralSecurityException
    {
        InnerEcResponse inncerEcResponse = createInnerEcResponse(request,responseCode,enrollmentCredential);

        EtsiTs102941Data etsiTs102941Data = createEtsiTs102941Data(inncerEcResponse);

        EtsiTs103097Data etsiTs102941DataSigned = createResponseSignedData(etsiTs102941Data, EAcertificate, EAsigKeys.getPrivate(), signingAlgorithm);

        EtsiTs103097Data etsiTs103097DataEncrypted = createRequestEncryptedData(etsiTs102941DataSigned, preSharedKey);

        return etsiTs103097DataEncrypted;
    }

    /**
     * Method that generates the inner enrollment credential response
     * @param request the the original request to digest
     * @param responseCode the result of the request
     * @param certificate the certificate to deliver, depending on the response
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private InnerEcResponse createInnerEcResponse(EtsiTs103097Data request, EnrollmentResonseCode responseCode, EtsiTs103097Certificate certificate) throws IOException, NoSuchAlgorithmException
    {
        COEROctetString requestHash = createRequestHash(request);
        return new InnerEcResponse(requestHash,responseCode,certificate);
    }

    /**
     * This method creates the EtsiTs102941Data structure of the enrollment response.
     * @return
     */
    private EtsiTs102941Data createEtsiTs102941Data(InnerEcResponse innerEcReponse) throws IOException
    {
        EtsiTs102941DataContent dataContent = new EtsiTs102941DataContent(innerEcReponse);
        return new EtsiTs102941Data(dataContent);
    }

    /**
     * This method creates the signed structure of the enrollment response
     * @param data the EtsiTs102941Data to be signed
     * @param EAcertificate the signer certificate
     * @param EAprivateKey the key to sign
     * @param signingAlgorithm the signing algorithm
     */
    private EtsiTs103097Data createResponseSignedData(EtsiTs102941Data data, EtsiTs103097Certificate EAcertificate, PrivateKey EAprivateKey,  AlgorithmType signingAlgorithm) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        HashAlgorithm hashAlgorithm = HashAlgorithm.SHA_256;
        HeaderInfo headerInfo = new HeaderInfo(new Psid(36),new Time64(new Date()),null, null, null, null, null); // the Psid element of the header is set to "CA Basic Service" because the "secured certificate request" cannot be found in ETSI TS 102 965 at the present time.
        if(EAcertificate == null)
        {
            throw new IllegalArgumentException("Error generating enrollment response: the EA certificate can not be null");
        }
        return createSignedData(hashAlgorithm, data.getEncoded(), headerInfo, EAcertificate, EAprivateKey, signingAlgorithm, false);
    }

    /**
     * This method creates the encrypted data structure of the enrollment response
     * @param data the signed EtsiTs103097Data to be encrypted
     * @param preSharedKey the key to encrypt
     */
    private EtsiTs103097Data createRequestEncryptedData(EtsiTs103097Data data, SecretKey preSharedKey) throws IOException, GeneralSecurityException
    {

        RecipientInfo[] recipients = {genRecipientInfo(preSharedKey)};
        SequenceOfRecipientInfo sequenceOfRecipients = new SequenceOfRecipientInfo(recipients);

        return createEncryptedData(sequenceOfRecipients, data.getEncoded(), SymmAlgorithm.AES_128_CCM, preSharedKey);
    }

}
