package com.multicert.v2x;

import com.multicert.v2x.IdentifiedRegions.Countries;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.generators.certificate.CACertGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;

/**
 * Help class that generates a basic PKI with a RootCA, Enrollment CA and Authorization CA.
 * This PKI is used to facilitate the testing of some datastructure which require existing certificates and keys
 */
public class PkiGenerator
{
    public EtsiTs103097Certificate rootCA = null;
    public EtsiTs103097Certificate enrollmentCA= null;
    public KeyPair enrollmentCAEncryptionKeys  = null;
    public EtsiTs103097Certificate authorizationCA = null;
    private CryptoHelper cryptoHelper;
    private CACertGenerator caCertGenerator;

    public PkiGenerator() throws Exception
    {
        cryptoHelper = new CryptoHelper("BC");
        caCertGenerator = new CACertGenerator(cryptoHelper, false);
        generatePki();
    }

    public void generatePki() throws Exception
    {
        //-------------------------Generate the RootCA-----------------------------------

        // Generate a reference to the Root CA Keys
        KeyPair rootCASigningKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        Signature.SignatureTypes rootSigAlgorithm = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
        KeyPair rootCAEncryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE);
        BasePublicEncryptionKey.BasePublicEncryptionKeyTypes rootEncAlg = BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_BRAINPOOL_P256r1;


        ValidityPeriod rootPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 3);
        Countries.CountryTypes[] countries = {Countries.CountryTypes.SPAIN, Countries.CountryTypes.PORTUGAL};
        GeographicRegion rootRegion = Countries.getGeographicRegion(countries);

        EtsiTs103097Certificate rootCACertificate = caCertGenerator.generateRootCA("RootCA", rootPeriod, rootRegion,
                7, 3, 3, -1,
                rootSigAlgorithm, rootCASigningKeys,
                SymmAlgorithm.AES_128_CCM,rootEncAlg ,
                rootCAEncryptionKeys.getPublic());
        rootCA = rootCACertificate;


        //-------------------------Generate the EnrollmentCA-----------------------------------

        // Generate a reference to the Root CA Keys
        KeyPair enrollmentCASigningKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        enrollmentCAEncryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        BasePublicEncryptionKey.BasePublicEncryptionKeyTypes enrollmentEncAlg = BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_NIST_P256;

        ValidityPeriod enrollmentPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 5);
        GeographicRegion enrollmentRegion = Countries.getGeographicRegion(countries);


        PsidSspRange[] enrollmentPerms = new PsidSspRange[1];
        enrollmentPerms[0] = new PsidSspRange(new Psid(5), new SspRange(SspRange.SspRangeTypes.ALL, null)); // Insert proper subject permissions here.

        EtsiTs103097Certificate enrollmentCACertificate = caCertGenerator.generateEnrollmentCa("enrollmentCA",enrollmentPeriod,enrollmentRegion,
                enrollmentPerms,5,2,1,0,rootSigAlgorithm,
                enrollmentCASigningKeys.getPublic(),rootCACertificate,rootCASigningKeys,SymmAlgorithm.AES_128_CCM,enrollmentEncAlg,enrollmentCAEncryptionKeys.getPublic());

        enrollmentCA = enrollmentCACertificate;


        //-------------------------Generate the authorization CA-----------------------------------

        // Generate a reference to the Root CA Keys
        KeyPair authorizationCASigningKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        KeyPair authorizationCAEncryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
        BasePublicEncryptionKey.BasePublicEncryptionKeyTypes authorizationEncAlg = BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_NIST_P256;

        ValidityPeriod authorizationPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 3);
        GeographicRegion authorizationRegion = Countries.getGeographicRegion(countries);

        byte[] authorizationcracaId = Hex.decode("01313203"); // Some cracaid
        PsidSspRange[] authorizationPerms = new PsidSspRange[1];
        enrollmentPerms[0] = new PsidSspRange(new Psid(5), new SspRange(SspRange.SspRangeTypes.ALL, null)); // Insert proper subject permissions here.

/**        EtsiTs103097Certificate authorizationCACertificate = caCertGenerator.generateEnrollmentCa("authorizationCA",authorizationPeriod,authorizationRegion,
                authorizationPerms,authorizationcracaId,99,5,2,1,0,rootSigAlgorithm,
                authorizationCASigningKeys.getPublic(),rootCACertificate,rootCASigningKeys,SymmAlgorithm.AES_128_CCM,enrollmentEncAlg,authorizationCAEncryptionKeys.getPublic());
**/
        authorizationCA = null;

    }

    public EtsiTs103097Certificate getAuthorizationCA()
    {
        return authorizationCA;
    }

    public EtsiTs103097Certificate getRootCA()
    {
        return rootCA;
    }

    public EtsiTs103097Certificate getEnrollmentCA()
    {
        return enrollmentCA;
    }

    public KeyPair getEnrollmentCAEncryptionKeys()
    {
        return enrollmentCAEncryptionKeys;
    }

    public CryptoHelper getCryptoHelper()
    {
        return cryptoHelper;
    }
}
