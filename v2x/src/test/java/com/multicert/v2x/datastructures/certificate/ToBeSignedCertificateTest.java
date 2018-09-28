package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.IdentifiedRegions.Countries;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.cryptography.CryptoHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.generators.certificate.CACertGenerator;
import com.multicert.v2x.generators.certificate.CertificateGenerator;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;

import static org.junit.Assert.*;

public class ToBeSignedCertificateTest
{
    @Test
    public void testEncodeAndDecode() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        CryptoHelper cryptoHelper = new CryptoHelper("BC");
        CertificateGenerator certificateGenerator = new CACertGenerator(cryptoHelper, false);
        try
        {

            KeyPair signingKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE);
            Signature.SignatureTypes issuerSigningAlgorithm = Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
            PublicKey signPublicKey = signingKeys.getPublic();

            KeyPair encryptionKeys = cryptoHelper.genKeyPair(Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE);
            BasePublicEncryptionKey.BasePublicEncryptionKeyTypes encPublicKeyAlgorithm = BasePublicEncryptionKey.BasePublicEncryptionKeyTypes.ECIES_NIST_P256;
            PublicKey encPublicKey = encryptionKeys.getPublic();

            CertificateId id = new CertificateId(new Hostname("SomeHostName"));

            SubjectAssurance subjectAssurance = new SubjectAssurance(7, 3);

            ValidityPeriod validityPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.YEARS, 45);

            Countries.CountryTypes[] countries = {Countries.CountryTypes.SPAIN, Countries.CountryTypes.PORTUGAL};
            GeographicRegion geographicRegionegion = Countries.getGeographicRegion(countries);

            PsidSsp permissionsCRL = new PsidSsp(new Psid(256), null);
            PsidSsp permissionsCTL = new PsidSsp(new Psid(36), null);
            PsidSsp[] values = {permissionsCRL, permissionsCTL};
            SequenceOfPsidSsp appPermissions = new SequenceOfPsidSsp(values);

            SubjectPermissions subjectPermissions = new SubjectPermissions(SubjectPermissions.SubjectPermissionsTypes.ALL, null);
            PsidGroupPermissions psidGroupPermissions = new PsidGroupPermissions(subjectPermissions, 3, -1, new EndEntityType(true, true));
            PsidGroupPermissions psidGroupPermissions2 = new PsidGroupPermissions(subjectPermissions, 4, 1, new EndEntityType(false, true));
            SequenceOfPsidGroupPermissions certIssuePermissions = new SequenceOfPsidGroupPermissions(new PsidGroupPermissions[]{psidGroupPermissions, psidGroupPermissions2});

            SymmAlgorithm symmAlgorithm = SymmAlgorithm.AES_128_CCM;

            PublicEncryptionKey encryptionKey = new PublicEncryptionKey(symmAlgorithm, new BasePublicEncryptionKey(encPublicKeyAlgorithm, certificateGenerator.convertToPoint(encPublicKeyAlgorithm, encPublicKey)));

            PublicVerificationKey verifyKeyIndicator = new PublicVerificationKey(certificateGenerator.getPublicVerificationKeyType(issuerSigningAlgorithm), certificateGenerator.convertToPoint(issuerSigningAlgorithm, signPublicKey));
            VerificationKeyIndicator verificationKeyIndicator = new VerificationKeyIndicator(verifyKeyIndicator);

            ToBeSignedCertificate toBeSignedCertificate = new ToBeSignedCertificate(id,
                    validityPeriod, geographicRegionegion, subjectAssurance, appPermissions, certIssuePermissions, encryptionKey, verificationKeyIndicator);
            toBeSignedCertificate.encode(dos);

            System.out.println(toBeSignedCertificate.toString());

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
            ToBeSignedCertificate decodedToBeSignedCertificate = new ToBeSignedCertificate();
            decodedToBeSignedCertificate.decode(dis);

            System.out.println("//--------Decoded TBS Certificate--------//");
            System.out.println(decodedToBeSignedCertificate.toString());

            assertEquals(toBeSignedCertificate.toString(), decodedToBeSignedCertificate.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}