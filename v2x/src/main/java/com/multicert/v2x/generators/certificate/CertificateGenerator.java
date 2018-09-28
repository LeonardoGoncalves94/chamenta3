package com.multicert.v2x.generators.certificate;

import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificate.IssuerIdentifier;
import com.multicert.v2x.datastructures.certificate.ToBeSignedCertificate;


import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class CertificateGenerator
{
    private CryptoHelper cryptoHelper;
    private boolean isCompressed;

    public CertificateGenerator(CryptoHelper cryptoHelper, boolean isCompressed)
    {
        this.cryptoHelper = cryptoHelper;
        this.isCompressed = isCompressed;
    }

    /**
     * Help method to converts a public key to EccP256CurvePoint given compression.
     */
    public EccP256CurvePoint convertToPoint(AlgorithmType alg, PublicKey pk) throws IllegalArgumentException, IOException{
        try
        {
            if(isCompressed)
            {
                return cryptoHelper.publicKeyToEccPoint(alg, EccP256CurvePoint.EccP256CurvePointTypes.COMPRESSED_Y_0, pk);
            }
            return cryptoHelper.publicKeyToEccPoint(alg, EccP256CurvePoint.EccP256CurvePointTypes.UNCOMPRESSED, pk);

        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Error, invalid keyspec: " + e.getMessage());
        }
    }

    /**
     * Help method to get the type of PublicVerificationKey given the algorithm
     */
    public PublicVerificationKey.PublicVerificationKeyTypes getPublicVerificationKeyType(AlgorithmType alg) throws IllegalArgumentException, IOException
    {
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_NIST_P256;
        }
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1)
        {
            return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_BRAINPOOL_P256r1;
        }
        return null;
    }

    public EtsiTs103097Certificate generateCertificate(ToBeSignedCertificate toBeSignedCertificate, EtsiTs103097Certificate issuerCertificate, KeyPair issuerKeypair, AlgorithmType signingAlgorithm) throws IOException, NoSuchAlgorithmException, SignatureException
    {
        byte[] toBeSignedData = toBeSignedCertificate.getEncoded();
        IssuerIdentifier issuerIdentifier;
        try
        {
            if (issuerCertificate == null) // we are generating a root CA certificate
            {
                issuerIdentifier = new IssuerIdentifier(HashAlgorithm.SHA_256);
            } else
            {
                HashedId8 certId = cryptoHelper.getCertificateHashId(issuerCertificate, HashAlgorithm.SHA_256);
                issuerIdentifier = new IssuerIdentifier(certId);
            }
            Signature signature = cryptoHelper.signMessage(toBeSignedData, issuerKeypair.getPrivate(), issuerCertificate, signingAlgorithm);
            return new EtsiTs103097Certificate(issuerIdentifier, toBeSignedCertificate, signature);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException("Error creating certificate: no such algorithm "+e);
        }
    }


}
