package com.multicert.v2x.generators.message;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.datastructures.message.secureddata.HeaderInfo;
import com.multicert.v2x.generators.certificate.CertificateGenerator;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Date;

/**
 * This class generates the Cooperative Awareness Messages (CAMs) that will be used by the vehicles to communicate in V2X
 * The security profile for CAMs can be consulted in the EtsiTs103097 standard, Section 7
 */
public class CAMGenerator extends SecuredDataGenerator
{
    private CertificateGenerator certificateGenerator; // needed to support some operations in the createInnerECRequest method



    public CAMGenerator(CryptoHelper cryptoHelper, boolean isCompressed)
    {
        super(cryptoHelper);
        certificateGenerator = new CertificateGenerator(cryptoHelper, isCompressed);
    }

    /**
     * Method to generate a CAM
     * @param authorizationTicket the authorization ticket used to certificate this message. Required
     * @param signingkeyPair the keypair to that will be used to sign the message (should be related to the authorization ticket). Required
     * @param signatureAlgorithm the algorithm of the signature. Required
     * @param payload the payload to be signed
     * @param inlineP2pcdRequest used to broadcast a request gor missing authorization tickets (authorization tickets are needed to CAM verification). Optional
     * @param requestedCertificate used to broadcast the authorization ticket requested by the inlineP2pcdRequest. Optional
     * @param includeCert, true if the signerId should be the full certificate, false to used a hashedId. Required
     * @return
     */
    public EtsiTs103097Data generateCAM(EtsiTs103097Certificate authorizationTicket, KeyPair signingkeyPair,
                                        AlgorithmType signatureAlgorithm, String payload, SequenceOfHashedId3 inlineP2pcdRequest,
                                        EtsiTs103097Certificate requestedCertificate, boolean includeCert) throws IOException, SignatureException, NoSuchAlgorithmException
    {

        byte[] payloadData = payload.getBytes();
        return createRequestSignedData(payloadData,authorizationTicket,signingkeyPair.getPrivate(),signatureAlgorithm,inlineP2pcdRequest,requestedCertificate,includeCert);
    }

    /**
     * This method creates the signed data structures of the CAMs
     * The inner signed structure is to prove the possession (POP signature) of the newly generated verification key pair
     * The outer signed structure is done using the private key of the valid enrollment credential to prove the identity of the vehicle
     * @param message the data to be signed. Required.
     * @param authorizationTicket, the authorization ticket signer of this message
     * @param privateKey the private key to sign the message
     * @param signingAlgorithm the digital signature algorithm
     *
     * @return the Signed data structure
     */
    private EtsiTs103097Data createRequestSignedData(byte[] message, EtsiTs103097Certificate authorizationTicket, PrivateKey privateKey, AlgorithmType signingAlgorithm, SequenceOfHashedId3 inlineP2pcdRequest,
                                                     EtsiTs103097Certificate requestedCertificate, boolean includeCert) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        HashAlgorithm hashAlgorithm = HashAlgorithm.SHA_256;
        HeaderInfo headerInfo = new HeaderInfo(new Psid(36),new Time64(new Date()),null, null, null,inlineP2pcdRequest,requestedCertificate); // the Psid element of the header is set to "CA Basic Service" because the "value for CAMs" cannot be found in ETSI TS 102 965 at the present time.
        return createSignedData(hashAlgorithm, message, headerInfo, authorizationTicket, privateKey, signingAlgorithm, includeCert);
    }
}
