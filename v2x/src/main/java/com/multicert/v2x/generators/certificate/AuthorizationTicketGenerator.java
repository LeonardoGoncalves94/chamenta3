package com.multicert.v2x.generators.certificate;


import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.*;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

/**
 * This class generates the authorization tickets which will be used by the vehicles to authenticate V2X messages
 *
 */
public class AuthorizationTicketGenerator extends CertificateGenerator
{
    public AuthorizationTicketGenerator(CryptoHelper cryptoHelper, boolean isCompressed)
    {
        super(cryptoHelper, isCompressed);
    }

    /**
     *
     * @param validityPeriod
     * @param region
     * @param appPerms used to indicate message signing permissions, i.e. permissions to sign v2x messages
     * @param assuranceLevel
     * @param confidenceLevel
     * @param issuerSigningAlgorithm
     * @param signPublicKey
     * @param issuerCertificate
     * @param issuerCertificateKeyPair
     * @param symmAlgorithm
     * @param encPublicKeyAlgorithm
     * @param encPublicKey
     * @return
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     */
    public EtsiTs103097Certificate generateAuthorizationTicket(ValidityPeriod validityPeriod,
                                                               GeographicRegion region,
                                                               SequenceOfPsidSsp appPerms,
                                                               int assuranceLevel,
                                                               int confidenceLevel,
                                                               AlgorithmType issuerSigningAlgorithm,
                                                               PublicKey signPublicKey,
                                                               EtsiTs103097Certificate issuerCertificate,
                                                               KeyPair issuerCertificateKeyPair,
                                                               SymmAlgorithm symmAlgorithm,
                                                               BasePublicEncryptionKey.BasePublicEncryptionKeyTypes encPublicKeyAlgorithm,
                                                               PublicKey encPublicKey) throws IOException, SignatureException, NoSuchAlgorithmException
    {
        CertificateId id = new CertificateId(); // certificateId is set to NONE

        if(appPerms.getValues() == null)
        {
            throw new IllegalArgumentException("Error generating authorization ticket: app permissions should not be null");
        }



        PublicEncryptionKey encryptionKey = null;
        if(symmAlgorithm != null && encPublicKeyAlgorithm != null && encPublicKey != null){
            encryptionKey = new PublicEncryptionKey(symmAlgorithm, new BasePublicEncryptionKey(encPublicKeyAlgorithm, convertToPoint(encPublicKeyAlgorithm, encPublicKey)));
        }
        SubjectAssurance subjectAssurance = new SubjectAssurance(assuranceLevel, confidenceLevel);
        VerificationKeyIndicator verificationKeyIndicator;

        PublicVerificationKey publicVerificationKey = new PublicVerificationKey(getPublicVerificationKeyType(issuerSigningAlgorithm), convertToPoint(issuerSigningAlgorithm, signPublicKey));
        verificationKeyIndicator = new VerificationKeyIndicator(publicVerificationKey);

        ToBeSignedCertificate tbs = new ToBeSignedCertificate(id, validityPeriod, region, subjectAssurance, appPerms, null, encryptionKey, verificationKeyIndicator);

        return generateCertificate(tbs, issuerCertificate, issuerCertificateKeyPair, issuerSigningAlgorithm);
    }
}
