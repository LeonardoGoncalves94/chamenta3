package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.IdentifiedRegions.Countries;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.generators.certificate.CACertGenerator;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Date;

import static org.junit.Assert.*;

public class EtsiTs103097CertificateTest
{
    //TODO
    /***
     * Ideia para testar mais do que um tipo de certificado. Ter um main que cria a hierarquia de certificados e depois testar aqui o seu encoding e decoding.
     */

    /**
     * Test for Root Certificate
     *
     */
    @Test
    public void testEncodeAndDecodeRoot() throws Exception
    {
        CryptoHelper cryptoHelper = new CryptoHelper("BC");
        CACertGenerator caCertGenerator = new CACertGenerator(cryptoHelper, false);

        //-------------------------Generate the RootCA-----------------------------------


        // Generate a reference to the Root CA Keys
        KeyPair rootCASigningKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        KeyPair rootCAEncryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE);


        // certificate validity info
        ValidityPeriod validityPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 45);
        Countries.CountryTypes[] countries = {Countries.CountryTypes.SPAIN, Countries.CountryTypes.PORTUGAL};
        GeographicRegion validityRegion = Countries.getGeographicRegion(countries);

        EtsiTs103097Certificate rootCACertificate = caCertGenerator.generateRootCA("RootCA", validityPeriod, validityRegion,
                7, 3, 3, -1,
                Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE, rootCASigningKeys,
                SymmAlgorithm.AES_128_CCM, BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_NIST_P256,
                rootCAEncryptionKeys.getPublic());
        System.out.println(rootCACertificate.toString());
        byte[] encoded = rootCACertificate.getEncoded();

        EtsiTs103097Certificate decodedRootCACertificate = new EtsiTs103097Certificate(encoded);
        assertEquals(rootCACertificate.toString(), decodedRootCACertificate.toString());

    }
}
