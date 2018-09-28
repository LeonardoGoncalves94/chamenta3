package com.multicert.v2x.generators.certificaterequest;

import com.multicert.v2x.IdentifiedRegions.Countries;
import com.multicert.v2x.PkiGenerator;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;
import com.multicert.v2x.generators.message.SecuredDataGenerator;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnrollmentRequestGeneratorTest
{
    EnrollmentRequestGenerator requestGenerator = null; //the request generator, to give access to the components of the request
    PkiGenerator pki = null; // a sample pki
    EtsiTs103097Data ecRequest = null; //the enrollment request

    PublicKey cannonicalPubKey = null; //the vehicle's canonical public key, to verify the signature of the request
    EtsiTs103097Data decryptedRequest = null; //the decrypted enrollment request



    /**
     * setup method to initialize a pki with certificates and keys; generate an enrollment certificate request and decrypt it
     */
    @Before
    public void setup() throws Exception
    {
        CryptoHelper cryptoHelper = new CryptoHelper("BC");

        //generata a sample pki
        pki = new PkiGenerator();

        //generate a secret key for the vehicle
        SecretKey sharedKey = cryptoHelper.genSecretKey(SymmAlgorithm.AES_128_CCM);

        // generata a request
        requestGenerator = new EnrollmentRequestGenerator(cryptoHelper,false);

        KeyPair keyPair = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE); //a keypair to be certified
        Signature.SignatureTypes keyPairAlgorithm = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;

        KeyPair cankeyPair = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE); //a canonical keypair
        cannonicalPubKey = cankeyPair.getPublic();


        ValidityPeriod validityPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 3);
        Countries.CountryTypes[] countries = {Countries.CountryTypes.SPAIN, Countries.CountryTypes.PORTUGAL};
        GeographicRegion region = Countries.getGeographicRegion(countries);
        EtsiTs103097Certificate enrollmentCACert = pki.getEnrollmentCA(); // the certificate of the recipient enrollment CA

        ecRequest = requestGenerator.generateEcRequest("123456789",cankeyPair, keyPairAlgorithm,keyPair,keyPairAlgorithm,validityPeriod,region,3,2,enrollmentCACert, sharedKey);


        //decrypt the request
        SecuredDataGenerator securedDataGenerator = new SecuredDataGenerator(cryptoHelper);
        KeyPair encryptionKeys = pki.getEnrollmentCAEncryptionKeys();

        byte[] decryptedRequestBytes = securedDataGenerator.decryptEncryptedData(ecRequest, enrollmentCACert, encryptionKeys.getPrivate());

        decryptedRequest = new EtsiTs103097Data(decryptedRequestBytes);


    }



    @Test
    public void testEncodeAndDecode() throws Exception
    {

        byte[] encodedRequest = ecRequest.getEncoded();
        EtsiTs103097Data decodedRequest = new EtsiTs103097Data(encodedRequest);
        assertEquals(ecRequest.toString(), decodedRequest.toString());

    }
    // test to see if the recipient Enrollment CA can decrypt the  enrollment credential request
    @Test
    public void testDecrypt() throws Exception
    {
        System.out.println("------------------Encrypted Request------------------");
        System.out.println(ecRequest.toString());

        System.out.println("------------------Decrypted Request------------------");
        System.out.println(decryptedRequest.toString());

        assertEquals(decryptedRequest.toString(), requestGenerator.getOuterEcRequest().toString() ); //compare the decrypted request to the pre-encrypted request
    }

    /**
     * Metho to verify the signature of the ECrequest, the request must be decrypted and the verificationkey must be the vehicle's public canonical key
     * @throws Exception
     */
    @Test
    public void testverify() throws Exception
    {
        CryptoHelper cryptoHelper = pki.getCryptoHelper();
        SecuredDataGenerator securedDataGenerator = new SecuredDataGenerator(cryptoHelper);

        securedDataGenerator.verifySignedRequest(decryptedRequest,cannonicalPubKey);

    }

}