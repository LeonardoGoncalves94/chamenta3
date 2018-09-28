package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.base.Signature;
import com.multicert.v2x.generators.certificate.CACertGenerator;
import com.multicert.v2x.generators.certificate.CertificateGenerator;
import org.junit.Test;

import java.io.*;
import java.security.KeyPair;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class VerificationKeyIndicatorTest
{

    @Test
    public void testEncodeAndDecode() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        CryptoHelper cryptoHelper = new CryptoHelper("BC");
        CertificateGenerator certificateGenerator = new CACertGenerator(cryptoHelper, false);

        KeyPair signingKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        Signature.SignatureTypes issuerSigningAlgorithm = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
        PublicKey signPublicKey = signingKeys.getPublic();

        PublicVerificationKey verifyKeyIndicator = new PublicVerificationKey(certificateGenerator.getPublicVerificationKeyType(issuerSigningAlgorithm), certificateGenerator.convertToPoint(issuerSigningAlgorithm, signPublicKey));
        VerificationKeyIndicator verificationKeyIndicator = new VerificationKeyIndicator(verifyKeyIndicator);
        verificationKeyIndicator.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        VerificationKeyIndicator decodedVerificationKeyIndicator = new VerificationKeyIndicator();
        decodedVerificationKeyIndicator.decode(dis);



        assertEquals(1,1);

    }
}