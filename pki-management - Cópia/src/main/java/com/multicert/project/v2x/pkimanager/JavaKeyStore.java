package com.multicert.project.v2x.pkimanager;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

public class JavaKeyStore 
{
   static private KeyStore keyStore;

    public JavaKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException 
    {
    	 keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    	 
    	 try(InputStream keyStoreData = new FileInputStream("keystore.ks")){
    		    keyStore.load(keyStoreData, JavaKeyStoreUtils.KEY_PASSWD);
    		} catch (FileNotFoundException e) 
    	 {
    			keyStore.load(null, JavaKeyStoreUtils.KEY_PASSWD);
    	 }
    	 
    	 
    	
    }

    static public void addKeyPair(KeyPair keys, String alias) throws Exception
    {

        keyStore.setKeyEntry(alias, keys.getPrivate(),JavaKeyStoreUtils.KEY_PASSWD, new Certificate[] {JavaKeyStoreUtils.createDummyRootCert(keys)});

        //Store the keystore
        FileOutputStream keyStoreOutputStream = new FileOutputStream("keystore.ks");
        keyStore.store(keyStoreOutputStream, JavaKeyStoreUtils.KEY_PASSWD);

    }
    
    /**
	 * Method that gets a given KeyPair from the keystore
	 * @param alias the alias of the desired key pair
	 */
    static public KeyPair getKeyPair(String alias) throws Exception
    {
        try
        {
            ECPrivateKey privateKey = (ECPrivateKey) keyStore.getKey(alias, JavaKeyStoreUtils.KEY_PASSWD);
            ECPublicKey publicKey = (ECPublicKey) keyStore.getCertificate(alias).getPublicKey();
            return new KeyPair(publicKey, privateKey);
        }
        catch(NullPointerException e)
        {
            throw new Exception("Error getting key from Keystore: Alias " + alias +" does not exist" );
        }

    }

    static public void printKestore() throws KeyStoreException
    {
        System.out.println("########## KeyStore Dump");

        for (Enumeration en = keyStore.aliases(); en.hasMoreElements();)
        {
            String alias = (String)en.nextElement();

            if (keyStore.isCertificateEntry(alias))
            {
                System.out.println("Certificate Entry: " + alias + ", Subject: " + (((X509Certificate)keyStore.getCertificate(alias)).getSubjectDN()));
            }
            else if (keyStore.isKeyEntry(alias))
            {
                System.out.println("Key Entry: " + alias + ", Subject: " + (((X509Certificate)keyStore.getCertificate(alias)).getSubjectDN()));
            }
        }

        System.out.println();
    }
}
