package com.multicert.v2x.generators.certificaterequest;


import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.Psid;
import com.multicert.v2x.datastructures.base.SymmAlgorithm;
import com.multicert.v2x.datastructures.base.Time64;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.AuthorizationResponseCode;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
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
 * This class generates an Authorization Certificate response as specified in ETSI 102-941, Section 6.2.3.2.1 standard.
 * The Authorization Response message should be sent by the AA to the ITS station in response to a received authorixation request.
 * It should be encrypted with the same AES key as the one used by the ITS station to encrypt the request message.
 */
public class AuthorizationResponseGenerator extends SecuredDataGenerator
{


    public AuthorizationResponseGenerator(CryptoHelper cryptoHelper)
    {
        super(cryptoHelper);
    }

    /**
     * This method generates the enrollment response
     * @param request the signed request the outer signed structure, REQUIRED
     * @param preSharedKey the pre-shared symmetric key present on the original request, REQUIRED
     * @param responseCode the result of the authorization validation
     * @param authorizationTicket the authorization ticket to deliver to the ITS station, null if none, OPTIONAL
     * @param AAcertificate The certificate of the AA that will generate this response, REQUIRED
     * @param AAsigKeys The AA signature keys, the private key will be used to sign this enrollment response,  REQUIRED
     * @param signingAlgorithm The algorithm which will be used to sign this enrollment  response, REQUIRED
     */
    public EtsiTs103097Data generateAtResponse(EtsiTs103097Data request, SecretKey preSharedKey, AuthorizationResponseCode responseCode, EtsiTs103097Certificate authorizationTicket,
                                               EtsiTs103097Certificate AAcertificate, KeyPair AAsigKeys, AlgorithmType signingAlgorithm) throws IOException, GeneralSecurityException
    {
        InnerAtResponse inncerAtResponse = createInnerAtResponse(request,responseCode,authorizationTicket);

        EtsiTs102941Data etsiTs102941Data = createEtsiTs102941Data(inncerAtResponse);

        EtsiTs103097Data etsiTs102941DataSigned = createResponseSignedData(etsiTs102941Data, AAcertificate, AAsigKeys.getPrivate(), signingAlgorithm);

        EtsiTs103097Data etsiTs103097DataEncrypted = createRequestEncryptedData(etsiTs102941DataSigned, preSharedKey);

        return etsiTs103097DataEncrypted;
    }

    /**
     * Method that generates the inner enrollment credential response
     * @param request the signed request to digest
     * @param responseCode the result of the request
     * @param certificate the certificate to deliver, depending on the response
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private InnerAtResponse createInnerAtResponse(EtsiTs103097Data request, AuthorizationResponseCode responseCode, EtsiTs103097Certificate certificate) throws IOException, NoSuchAlgorithmException
    {
        COEROctetString requestHash = createRequestHash(request);
        return new InnerAtResponse(requestHash,responseCode,certificate);
    }

    /**
     * This method creates the EtsiTs102941Data structure of the enrollment response.
     * @return
     */
    private EtsiTs102941Data createEtsiTs102941Data(InnerAtResponse innerAtResponse) throws IOException
    {
        EtsiTs102941DataContent dataContent = new EtsiTs102941DataContent(innerAtResponse);
        return new EtsiTs102941Data(dataContent);
    }

    /**
     * This method creates the signed structure of the enrollment response
     * @param data the EtsiTs102941Data to be signed
     * @param AAcertificate the certificate of the AA
     * @param AAprivateKey the the AA private key that belongs to the certificate
     * @param signingAlgorithm the signing algorithm
     */
    private EtsiTs103097Data createResponseSignedData(EtsiTs102941Data data, EtsiTs103097Certificate AAcertificate, PrivateKey AAprivateKey,  AlgorithmType signingAlgorithm) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        HashAlgorithm hashAlgorithm = HashAlgorithm.SHA_256;
        HeaderInfo headerInfo = new HeaderInfo(new Psid(36),new Time64(new Date()),null, null, null, null, null); // the Psid element of the header is set to "CA Basic Service" because the "secured certificate request" cannot be found in ETSI TS 102 965 at the present time.
        if(AAcertificate == null)
        {
            throw new IllegalArgumentException("Error generating enrollment response: the EA certificate can not be null");
        }
        return createSignedData(hashAlgorithm, data.getEncoded(), headerInfo, AAcertificate, AAprivateKey, signingAlgorithm, false);
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
