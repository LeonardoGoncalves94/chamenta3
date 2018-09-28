import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.generators.certificate.CACertGenerator;

/**
 * This class will demostrate how to create the CA hierarchy, the enrollment certificates and authorization tickets for the vehicles, and the requests for such certificates
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        CryptoHelper cryptoHelper = new CryptoHelper("BC");
        CACertGenerator caCertGenerator = new CACertGenerator(cryptoHelper, false);

        //-------------------------Generate the RootCA-----------------------------------
/**

        // Generate a reference to the Root CA Keys
        KeyPair rootCASigningKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE,"alias1");
        KeyPair rootCAEncryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE, "alias2");


        // certificate validity info
        ValidityPeriod rootCAValidityPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 45);
        Countries.CountryTypes[] countries = {Countries.CountryTypes.SPAIN, Countries.CountryTypes.PORTUGAL};
        GeographicRegion rootCAValidityRegion = Countries.getGeographicRegion(countries);

        EtsiTs103097Certificate rootCACertificate = caCertGenerator.generateRootCA("RootCA",rootCAValidityPeriod,rootCAValidityRegion,
                7,3,3, -1,
                Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE,rootCASigningKeys,
                SymmAlgorithm.AES_128_CCM, BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_NIST_P256,
                rootCAEncryptionKeys.getPublic());
**/
    }
}
