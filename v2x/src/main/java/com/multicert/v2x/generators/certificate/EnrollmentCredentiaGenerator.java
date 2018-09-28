package com.multicert.v2x.generators.certificate;

import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.*;
import com.multicert.v2x.datastructures.base.BasePublicEncryptionKey.*;
import com.multicert.v2x.datastructures.certificate.SubjectPermissions.*;

import java.io.IOException;
import java.security.*;

/**
 * This class is used by the enrollment CA to generate and enrollment certificate for the vehicles (proof of identity)
 */
public class EnrollmentCredentiaGenerator extends CertificateGenerator
{
    public EnrollmentCredentiaGenerator(CryptoHelper cryptoHelper, boolean isCompressed)
    {
        super(cryptoHelper, isCompressed);
    }

    /**
     *
     * @param hostname
     * @param validityPeriod
     * @param region
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
    public EtsiTs103097Certificate generateEnrollmentCredential(String hostname,
                                                                ValidityPeriod validityPeriod,
                                                                GeographicRegion region,
                                                                int assuranceLevel,
                                                                int confidenceLevel,
                                                                AlgorithmType issuerSigningAlgorithm,
                                                                PublicKey signPublicKey,
                                                                EtsiTs103097Certificate issuerCertificate,
                                                                KeyPair issuerCertificateKeyPair,
                                                                SymmAlgorithm symmAlgorithm,
                                                                BasePublicEncryptionKeyTypes encPublicKeyAlgorithm,
                                                                PublicKey encPublicKey) throws IOException, SignatureException, NoSuchAlgorithmException
    {
        CertificateId id = new CertificateId(new Hostname(hostname));

        PublicEncryptionKey encryptionKey = null;
        if(symmAlgorithm != null && encPublicKeyAlgorithm != null && encPublicKey != null){
            encryptionKey = new PublicEncryptionKey(symmAlgorithm, new BasePublicEncryptionKey(encPublicKeyAlgorithm, convertToPoint(encPublicKeyAlgorithm, encPublicKey)));
        }
        SubjectAssurance subjectAssurance = new SubjectAssurance(assuranceLevel, confidenceLevel);
        VerificationKeyIndicator verificationKeyIndicator;

        //TODO VER MELHOR AS PERMISSOES, psid -> etsi ts 102 966;
        PsidSsp SignResponsePermissions = new PsidSsp(new Psid(36), null); // permissions to sign certificate response messages. ITS-AID value to sign certificate response not found in the standard ETSI TS 102 965 (update to the standard in preparation t the present time)
        PsidSsp[] values = {SignResponsePermissions};
        SequenceOfPsidSsp appPermissions = new SequenceOfPsidSsp(values);

        PublicVerificationKey publicVerificationKey = new PublicVerificationKey(getPublicVerificationKeyType(issuerSigningAlgorithm), convertToPoint(issuerSigningAlgorithm, signPublicKey));
        verificationKeyIndicator = new VerificationKeyIndicator(publicVerificationKey);

        ToBeSignedCertificate tbs = new ToBeSignedCertificate(id, validityPeriod, region, subjectAssurance, appPermissions, null,  encryptionKey, verificationKeyIndicator);

        return generateCertificate(tbs, issuerCertificate, issuerCertificateKeyPair, issuerSigningAlgorithm);
    }
}
