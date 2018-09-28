package com.multicert.project.v2x.pkimanager;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Utils
 */
public class JavaKeyStoreUtils
{
 
    private static final int VALIDITY_PERIOD = 7 * 24 * 60 * 60 * 1000; // one week

    public static char[] KEY_PASSWD = "keyPassword".toCharArray();

    /**
     * Help method that generates a dummy certificate so we can store keypairs on the keystore
     * @param keyPair the keypair to store
     */
    public static X509Certificate createDummyRootCert(KeyPair keyPair)
            throws Exception
    {
        X509v1CertificateBuilder certBldr = new JcaX509v1CertificateBuilder(
                new X500Name("CN= Root Certificate"),
                BigInteger.valueOf(1),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + VALIDITY_PERIOD),
                new X500Name("CN= Root Certificate"),
                keyPair.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withECDSA").build(keyPair.getPrivate());

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBldr.build(signer));
    }



    /**
     * Create a random 2048 bit RSA key pair
     */
    public static KeyPair generateRSAKeyPair()
        throws Exception
    {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");

        kpGen.initialize(2048, new SecureRandom());

        return kpGen.generateKeyPair();
    }  

}
