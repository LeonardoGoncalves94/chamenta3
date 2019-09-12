package com.multicert.v2x.cryptography;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.EncodeHelper;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.base.EccP256CurvePoint.*;
import com.multicert.v2x.datastructures.base.Signature;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.message.encrypteddata.EncryptedDataEncryptionKey;
import com.multicert.v2x.datastructures.message.encrypteddata.EncryptedDataEncryptionKey.*;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *Implementation of the CryptoHelper with support to ecdsa_nistp256_with_sha256 and ecies_nistp256 algorithms
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class CryptoHelper {


    protected KeyPairGenerator ecNistP256Generator;
    protected KeyPairGenerator brainpoolp256r1P256Generator;
    protected KeyGenerator aesGenerator;
    protected ECParameterSpec ecNistP256Spec = ECNamedCurveTable.getParameterSpec("P-256");
    protected ECParameterSpec brainpoolp256r1P256Spec = ECNamedCurveTable.getParameterSpec("brainpoolp256r1");
    protected SecureRandom secureRandom = new SecureRandom();
    protected static final int AES_PARAM = 128;
    protected KeyFactory keyFactory;
    protected MessageDigest sha256Digest;
    protected IESCipher iesCipher = new IESCipher(new IESEngine(new ECDHCBasicAgreement(),
            new KDF2BytesGenerator(new SHA256Digest()),
            new Mac(new SHA256Digest(),128)));


    protected String provider;


    /**
     * Constructor used when instantiating a cryptohelper
     * @param provider
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public CryptoHelper(String provider) throws Exception
    {
        this.provider = provider;
        try
        {
            if (Security.getProvider("BC") == null)
            {
                Security.addProvider(new BouncyCastleProvider());
            }
            ecNistP256Generator = KeyPairGenerator.getInstance("ECDSA", provider);
            ecNistP256Generator.initialize(ecNistP256Spec, secureRandom);
            brainpoolp256r1P256Generator = KeyPairGenerator.getInstance("ECDSA", provider);
            brainpoolp256r1P256Generator.initialize(brainpoolp256r1P256Spec, secureRandom);
            aesGenerator = KeyGenerator.getInstance("AES", provider);
            sha256Digest = MessageDigest.getInstance("SHA-256","BC");
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            aesGenerator.init(AES_PARAM);
        }
        catch (InvalidAlgorithmParameterException e)
        {
            throw new NoSuchAlgorithmException("Error initializing CryptoHelper: Invalid algorithm parameter" + e.getMessage(),e);
        }

    }

    /**
     * Method that generates a keypair given a digital signing algorithm
     *
     * Use ECDSA_NIST_P256_SIGNATURE for Nist generator and ECDSA_BRAINPOOL_P256R1_SIGNATURE for Brainpool generator
     */
    public KeyPair genKeyPair(AlgorithmType algorithm) throws Exception
    {

        if(algorithm == null)
        {
            throw new IllegalArgumentException("Error generating key pair: Algorithm must not be null");
        }
        if(algorithm.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            KeyPair keys = ecNistP256Generator.generateKeyPair();
            return keys;

        }
        if(algorithm.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1)
        {
            KeyPair keys = brainpoolp256r1P256Generator.generateKeyPair();
            return keys;
        }
        throw new IllegalArgumentException("Error generating key pair: Unsupported algorithm" + algorithm);
    }


    public SecretKey genSecretKey(AlgorithmType alg)
    {
        if(alg.getAlgorithm().getSymmetric() != Algorithm.Symmetric.AES_128_CCM)
        {
            throw new IllegalArgumentException("Error generating secret key: unsupported algorithm:" +alg);
        }
        return aesGenerator.generateKey();
    }

    public SecretKey constructSecretKey(AlgorithmType alg, byte[] keyData)
    {
        if(alg.getAlgorithm().getSymmetric() != Algorithm.Symmetric.AES_128_CCM){
            throw new IllegalArgumentException("Error constructing secret key: unsupported algorithm: " + alg);
        }
        return new SecretKeySpec(keyData, "AES");
    }

    /**
     * Method to build a cert store map of HashedId8 to Certificate from a collection of certificates.
     * @param certificates array of certificate to build store of.
     * @return a map of HashedId8 to certificate.
     */
    public Map<HashedId8, EtsiTs103097Certificate> buildCertStore(EtsiTs103097Certificate[] certificates, HashAlgorithm hashAlgorithm) throws IllegalArgumentException, NoSuchAlgorithmException, IOException{
        Map<HashedId8, EtsiTs103097Certificate> retval = new HashMap<HashedId8, EtsiTs103097Certificate>();

        for(EtsiTs103097Certificate cert : Arrays.asList(certificates)){
            retval.put(new HashedId8(digest(cert.getEncoded(), hashAlgorithm)), cert);
        }

        return retval;
    }


    /**
     * Method that converts a public key of a given algorithm to a EccP256CurvePoint structure (encode point)
     *
     */
    public EccP256CurvePoint publicKeyToEccPoint(AlgorithmType alg, EccP256CurvePointTypes type, PublicKey publicKey) throws IllegalArgumentException, InvalidKeySpecException, IOException
    {
        if(! (publicKey instanceof java.security.interfaces.ECPublicKey))
        {
            throw new IllegalArgumentException("Error converting public key to ECC curve point: Only EC public keys are supported");
        }
        BCECPublicKey bcPub = toBCECPublicKey(alg, (java.security.interfaces.ECPublicKey) publicKey);

        if(type == EccP256CurvePointTypes.UNCOMPRESSED){
            return new EccP256CurvePoint(bcPub.getW().getAffineX(), bcPub.getW().getAffineY());
        }
        if(type == EccP256CurvePointTypes.COMPRESSED_Y_0 || type == EccP256CurvePointTypes.COMPRESSED_Y_1){
            return new EccP256CurvePoint(bcPub.getQ().getEncoded(true));
        }
        if(type == EccP256CurvePointTypes.X_ONLY){
            return new EccP256CurvePoint(bcPub.getW().getAffineX());
        }

        throw new IllegalArgumentException("Unsupported ecc point type: " + type);
    }

    /**
     * Help method that converts ECC point into public key (decode the point)
     */
    public Object eccPointToPublicKey(AlgorithmType alg, EccP256CurvePoint eccPoint) throws InvalidKeySpecException {
        switch(eccPoint.getType()){
            case FILL:
                throw new InvalidKeySpecException("Unsupported EccPoint type: fill");
            case X_ONLY:
                byte[] data = ((COEROctetString) eccPoint.getValue()).getData();
                return new BigInteger(1,data);
            case COMPRESSED_Y_0:
            case COMPRESSED_Y_1:
                byte[] compData = ((COEROctetString) eccPoint.getValue()).getData();
                byte[] compressedEncoding = new byte[compData.length +1];
                System.arraycopy(compData, 0, compressedEncoding, 1, compData.length);
                if(eccPoint.getType() == EccP256CurvePointTypes.COMPRESSED_Y_0){
                    compressedEncoding[0] = 0x02;
                }else{
                    compressedEncoding[0] = 0x03;
                }
                return getECPublicKeyFromECPoint(alg, getECCurve(alg).decodePoint(compressedEncoding));
            case UNCOMPRESSED:
                UncompressedEccPoint uep = (UncompressedEccPoint) eccPoint.getValue();
                BigInteger x = new BigInteger(1, uep.getX());
                BigInteger y = new BigInteger(1, uep.getY());
                return getECPublicKeyFromECPoint(alg, getECCurve(alg).createPoint(x, y));
        }
        return null;
    }

    protected ECPublicKey getECPublicKeyFromECPoint(AlgorithmType alg, ECPoint eCPoint) throws InvalidKeySpecException{
        ECPublicKeySpec spec = new ECPublicKeySpec(eCPoint, getECParameterSpec(alg));
        return (ECPublicKey) keyFactory.generatePublic(spec);
    }

    protected BCECPublicKey toBCECPublicKey(AlgorithmType alg, java.security.interfaces.ECPublicKey ecPublicKey) throws InvalidKeySpecException
    {
        if(ecPublicKey instanceof BCECPublicKey)
        {
            return (BCECPublicKey) ecPublicKey;
        }

        org.bouncycastle.math.ec.ECPoint ecPoint = EC5Util.convertPoint(getECCurve(alg), ecPublicKey.getW(), false);
        ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, getECParameterSpec(alg));

        return (BCECPublicKey) keyFactory.generatePublic(keySpec);
    }



    /**
     * Main method used for sinign data, this method should be used to sign data providing the signing certificate
     * @param tbsData
     * @param signerPrivateKey
     * @param signerCertificate
     * @param signingAlgorithm
     * @return
     * @throws SignatureException
     */
    public Signature signMessage(byte[] tbsData, PrivateKey signerPrivateKey, EtsiTs103097Certificate signerCertificate,  AlgorithmType signingAlgorithm) throws SignatureException
    {
        if(signingAlgorithm.getAlgorithm().getSignature() == null)
        {
            throw new IllegalArgumentException("Error signing certificate: No signature algorithm indicated");
        }

        try
        {
            return signMessageDigest(digestCertificate(tbsData, signingAlgorithm, signerCertificate), signingAlgorithm, signerPrivateKey);
        } catch (SignatureException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        throw new SignatureException("Error creating signature");
    }

    /**
     * This method creates a certificate digest according to the ieee1609.2 standards
     * @param tbsData the to be signed certificate data
     * @param hashAlgorithm the algorithm to ude
     * @param issuerCertificate the certificate used for signing, null if self sign
     */
    public byte[] digestCertificate(byte[] tbsData, AlgorithmType hashAlgorithm, EtsiTs103097Certificate issuerCertificate) throws NoSuchAlgorithmException, IOException
    {
        byte[] dataDigest = digest(tbsData, hashAlgorithm);
        byte[] signerDigest;

        if(issuerCertificate == null)
        {
            signerDigest = digest(new byte[0], hashAlgorithm);
        }
        else
        {
            signerDigest = digest(issuerCertificate.getEncoded(), hashAlgorithm);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(dataDigest);
        baos.write(signerDigest);

        byte[] result = digest(baos.toByteArray(), hashAlgorithm);
        return result;
    }

    public byte[] digest(byte[] message, AlgorithmType hashAlgorithm) throws IllegalArgumentException, NoSuchAlgorithmException {

        if(hashAlgorithm != null && hashAlgorithm.getAlgorithm().getHash() == Algorithm.Hash.SHA_256){
            sha256Digest.reset();
            sha256Digest.update(message);
            return sha256Digest.digest();
        }else{
            throw new IllegalArgumentException("Unsupported hash algorithm: " + hashAlgorithm);
        }
    }

    /**
     * Method used to sign
     * @param digest
     * @param signingAlgorithm
     * @param privateKey
     * @return
     * @throws SignatureException
     * @throws IOException
     */
    public Signature signMessageDigest(byte[] digest, AlgorithmType signingAlgorithm, PrivateKey privateKey) throws SignatureException, IOException
    {
        if (signingAlgorithm == null)
        {
            throw new IllegalArgumentException("Error signing digest: no signature algorithm specified");
        }

        ASN1InputStream asn1InputStream = null;
        try
        {
            java.security.Signature signature = java.security.Signature.getInstance("NONEwithECDSA", provider);
            signature.initSign(privateKey);
            signature.update(digest);
            byte[] dERSignature = signature.sign();

            ByteArrayInputStream inStream = new ByteArrayInputStream(dERSignature);
            asn1InputStream = new ASN1InputStream(inStream);

            DLSequence dLSequence = (DLSequence) asn1InputStream.readObject();
            BigInteger r = ((ASN1Integer) dLSequence.getObjectAt(0)).getPositiveValue();
            BigInteger s = ((ASN1Integer) dLSequence.getObjectAt(1)).getPositiveValue();

            int signatureSize = Algorithm.Signature.size;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(signatureSize);
            EncodeHelper.writeFixedFieldSizeKey(signatureSize, baos, s);
            return new Signature(getSignatureType(signingAlgorithm), new EcdsaP256Signature(new EccP256CurvePoint(r),baos.toByteArray()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SignatureException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(asn1InputStream != null)
            {
                asn1InputStream.close();
            }
        }

        throw new SignatureException("Error creating signature");
    }


    public boolean verifyCertificate(
            EtsiTs103097Certificate certificate,
            EtsiTs103097Certificate signerCertificate)
            throws IllegalArgumentException, SignatureException, IOException, BadContentTypeException
    {
        if(certificate.equals(signerCertificate)){
            return verifySignature(certificate.getTbsCert().getEncoded(), certificate.getSignature(), certificate);

        }else{
            return verifySignature(certificate.getToBeSigned().getEncoded(), certificate.getSignature(), signerCertificate);
        }

    }



    /**
     * Method that verifies a signature given the signing certificate
     * @param message the signed message
     * @param signature the signature
     * @param signerCert the certificate of the signer
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public boolean verifySignature(
            byte[] message,
            Signature signature,
            EtsiTs103097Certificate signerCert)
            throws IllegalArgumentException, SignatureException, IOException, BadContentTypeException
    {
            EccP256CurvePoint eccPoint;
            try
            {
                PublicVerificationKey pubVerKey = (PublicVerificationKey) signerCert.getToBeSigned().getVerifyKeyIndicator().getValue();
                eccPoint = (EccP256CurvePoint) pubVerKey.getValue();
            }
            catch(Exception e)
            {
                throw new BadContentTypeException("Error verifying EC response: Could not extract public key from the EA cert");
            }
            AlgorithmType alg = getSignatureAlgorithm(signature.getType());
            Algorithm.Signature sigAlg = alg.getAlgorithm().getSignature();

            if(sigAlg == null){
                throw new IllegalArgumentException("Error no signature algorithm specified");
            }
            try{
                return verifySignatureDigest(digestCertificate(message,alg, signerCert), signature, (PublicKey) eccPointToPublicKey(alg, eccPoint));
            }catch(Exception e){
                if(e instanceof IllegalArgumentException){
                    throw (IllegalArgumentException) e;
                }
                if(e instanceof IOException){
                    throw (IOException) e;
                }
                if(e instanceof SignatureException){
                    throw (SignatureException) e;
                }

                throw new SignatureException("Internal error verifying signature " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }


    }

    /**
     * Method that verifies a self sign signature given the public key
     * @param message the signed message
     * @param signature the signature
     * @param publicKey the public key to decrypt the signature
     * @return
     * @throws IllegalArgumentException
     * @throws SignatureException
     * @throws IOException
     */
    public boolean verifySignature(
            byte[] message,
            Signature signature,
            PublicKey publicKey)
            throws IllegalArgumentException, SignatureException, IOException {

        AlgorithmType alg = getSignatureAlgorithm(signature.getType());
        Algorithm.Signature sigAlg = alg.getAlgorithm().getSignature();
        if(sigAlg == null){
            throw new IllegalArgumentException("Error no signature algorithm specified");
        }
        try{
            return  verifySignatureDigest(digestCertificate(message,alg, null), signature, publicKey);
        }catch(Exception e){
            if(e instanceof IllegalArgumentException){
                throw (IllegalArgumentException) e;
            }
            if(e instanceof IOException){
                throw (IOException) e;
            }
            if(e instanceof SignatureException){
                throw (SignatureException) e;
            }

            throw new SignatureException("Internal error verifying signature " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }

    }


    public boolean verifySignatureDigest(
            byte[] digest,
            Signature signature,
            PublicKey publicKey) throws IllegalArgumentException,
            SignatureException, IOException {

        AlgorithmType alg = getSignatureAlgorithm(signature.getType());
        Algorithm.Signature sigAlg = alg.getAlgorithm().getSignature();
        if(sigAlg == null){
            throw new IllegalArgumentException("Error no signature algorithm specified");
        }

        try{
            EcdsaP256Signature ecdsaSignature = (EcdsaP256Signature) signature.getValue();

            // Create Signature Data
            EccP256CurvePoint xPoint = ecdsaSignature.getR();
            BigInteger r = new BigInteger(1,((COEROctetString) xPoint.getValue()).getData());
            ASN1Integer asn1R = new ASN1Integer(r);
            ASN1Integer asn1S = new ASN1Integer(EncodeHelper.readFixedFieldSizeKey(sigAlg.size, new ByteArrayInputStream(ecdsaSignature.getS())));
            DLSequence dLSequence = new DLSequence(new ASN1Encodable[]{asn1R, asn1S});
            byte[] dERSignature = dLSequence.getEncoded();

            java.security.Signature sig = java.security.Signature.getInstance("NONEwithECDSA", provider);
            sig.initVerify(publicKey);
            sig.update(digest);
            return sig.verify(dERSignature);
        }catch(Exception e){
            if(e instanceof IllegalArgumentException){
                throw (IllegalArgumentException) e;
            }
            if(e instanceof IOException){
                throw (IOException) e;
            }
            if(e instanceof SignatureException){
                throw (SignatureException) e;
            }

            throw new SignatureException("Internal error verifying signature " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }


    protected ECCurve getECCurve (AlgorithmType alg)
    {
        if (alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            return new SecP256R1Curve(); //Nist curve

        }
        if ((alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1))
        {
            return TeleTrusTNamedCurves.getByOID(TeleTrusTObjectIdentifiers.brainpoolP256r1).getCurve(); //Brainpool curve
        }
        throw new IllegalArgumentException("Unsupported EC Algorithm: " + alg);
    }

    /**
     * Help method to get the signature algorithm from its type
     */
    protected AlgorithmType getSignatureAlgorithm(Signature.SignatureTypes sigType) {
        switch (sigType) {
            case ECDSA_NIST_P256_SIGNATURE:
                return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_NIST_P256;
            case ECDSA_BRAINPOOL_P256R1_SIGNATURE:
            default:
                return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_BRAINPOOL_P256r1;
        }
    }

    /**
     * Help method to get the public verification key type from its algorithm
     */
    public PublicVerificationKey.PublicVerificationKeyTypes getVerificationKeyType(AlgorithmType alg)
    {
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_NIST_P256;
        }
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1)
        {
            return PublicVerificationKey.PublicVerificationKeyTypes.ECDSA_BRAINPOOL_P256r1;
        }
        throw new IllegalArgumentException("Unsupported key algorithm: "+alg);
    }

    /**
     * Help method to get the signature type from its algorithm
     */
    protected Signature.SignatureTypes getSignatureType(AlgorithmType signingAlgorithm)
    {
        if(signingAlgorithm.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            return Signature.SignatureTypes.ECDSA_NIST_P256_SIGNATURE;
        }
        if(signingAlgorithm.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1)
        {
            return Signature.SignatureTypes.ECDSA_BRAINPOOL_P256R1_SIGNATURE;
        }
        throw new IllegalArgumentException("Unsupported Signature algorithm: "+signingAlgorithm);
    }

    /**
     * Help method to get the curve from the signature algorithm
     * @param alg
     * @return
     */
    protected ECParameterSpec getECParameterSpec(AlgorithmType alg)
    {
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_NIST_P256)
        {
            return ecNistP256Spec;
        }
        if(alg.getAlgorithm().getSignature() == Algorithm.Signature.ECDSA_BRAINPOOL_P256R1)
        {
            return brainpoolp256r1P256Spec;
        }
        throw new IllegalArgumentException("Unsupported EC Algorithm: " +alg);
    }

    public HashAlgorithm getHashAlgorithm(AlgorithmType alg)
    {
        if(alg.getAlgorithm().getHash() != Algorithm.Hash.SHA_256)
        {
            throw new IllegalArgumentException("Error getting hash algorithm: Unsupported algorithm"+ alg);
        }
        return HashAlgorithm.SHA_256;
    }

    public byte[] encryptSymmetric(AlgorithmType alg, Key symmetricKey, byte[] nounce, byte[] data) throws IllegalArgumentException, GeneralSecurityException
    {
        Cipher cipher = getSymmetricCihper(alg);
        cipher.init(Cipher.ENCRYPT_MODE, symmetricKey, new IvParameterSpec(nounce));
        return cipher.doFinal(data);
    }

    public byte[] generateNounce(AlgorithmType alg) throws IllegalArgumentException, GeneralSecurityException{
        if( alg.getAlgorithm().getSymmetric() == null){
            throw new IllegalArgumentException("Error generating nounce: algorithm scheme does not support symmetric encryption");
        }
        int nounceLen = alg.getAlgorithm().getSymmetric().getNounceLength();
        byte[] nounce = new byte[nounceLen];
        secureRandom.nextBytes(nounce);

        return nounce;
    }


    public byte[] symmetricDecrypt(AlgorithmType alg, byte[] data, Key symmetricKey, byte[] nounce) throws IllegalArgumentException, GeneralSecurityException{

        Cipher c = getSymmetricCihper(alg);
        c.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(nounce));

        return c.doFinal(data);
    }


    protected Cipher getSymmetricCihper(AlgorithmType alg) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException
    {
        if(alg.getAlgorithm().getSymmetric() != Algorithm.Symmetric.AES_128_CCM)
        {
            throw new IllegalArgumentException("Error encrypting/decrypting data: invalid algorithm" + alg);
        }
        return Cipher.getInstance("AES/CCM/NoPadding", "BC");
    }

    protected byte[] decryptSymmetric(AlgorithmType alg, Key symmetricKey, byte[] nounce, byte[] data) throws IllegalArgumentException, GeneralSecurityException
    {
        Cipher cipher = getSymmetricCihper(alg);
        cipher.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(nounce));
        return cipher.doFinal(data);
    }

    /**
     * Help method to perform a ECIES encryption of a symmetric key.
     */
    public EncryptedDataEncryptionKey eceisEncryptSymmetricKey(EncryptedDataEncryptionKeyTypes keyType, AlgorithmType alg, PublicKey encryptionKey, SecretKey symmKey) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        byte[] keyData = symmKey.getEncoded();

        iesCipher.engineInit(Cipher.ENCRYPT_MODE, encryptionKey, new IESParameterSpec(null, null, 128,-1, null, true),secureRandom);

        byte[] encryptedData = iesCipher.engineDoFinal(keyData, 0, keyData.length);
        byte[] v = new byte[keyType.getVLength()];
        System.arraycopy(encryptedData, 0, v, 0,keyType.getVLength());

        EccP256CurvePoint p = new EccP256CurvePoint(v);

        byte[] c = new byte[alg.getAlgorithm().getSymmetric().getKeyLength()];
        byte[] t = new byte[keyType.getOutputTagLength()];
        System.arraycopy(encryptedData, keyType.getVLength(), c, 0, alg.getAlgorithm().getSymmetric().getKeyLength());
        System.arraycopy(encryptedData, keyType.getVLength() + alg.getAlgorithm().getSymmetric().getKeyLength(), t, 0, keyType.getOutputTagLength());

        EciesP256EncryptedKey key = new EciesP256EncryptedKey(p,c,t);
        return new EncryptedDataEncryptionKey(keyType, key);
    }
    /**
     * Help method to perform a ECIES decryption of a symmetric key.
     */
    public SecretKey eceisDecryptSymmetricKey(EncryptedDataEncryptionKey encryptedDataEncryptionKey, PrivateKey decryptionKey, AlgorithmType alg) throws IllegalArgumentException, GeneralSecurityException, IOException{
        try{
            EncryptedDataEncryptionKeyTypes keyType = encryptedDataEncryptionKey.getType();
            iesCipher.engineInit(Cipher.DECRYPT_MODE, decryptionKey, new IESParameterSpec(null, null, 128,-1, null, true),secureRandom);

            byte[] encryptedData = new byte[keyType.getVLength() + alg.getAlgorithm().getSymmetric().getKeyLength() + keyType.getOutputTagLength()];


            EciesP256EncryptedKey eciesP256EncryptedKey = (EciesP256EncryptedKey) encryptedDataEncryptionKey.getValue();
            ECPublicKey pubKey = (ECPublicKey) eccPointToPublicKey(alg, eciesP256EncryptedKey.getV());
            BCECPublicKey bcPubKey = toBCECPublicKey(alg, pubKey);

            System.arraycopy(bcPubKey.getQ().getEncoded(true), 0, encryptedData, 0, keyType.getVLength());
            System.arraycopy(eciesP256EncryptedKey.getC(), 0, encryptedData, keyType.getVLength(), alg.getAlgorithm().getSymmetric().getKeyLength());
            System.arraycopy(eciesP256EncryptedKey.getT(), 0, encryptedData, keyType.getVLength()+alg.getAlgorithm().getSymmetric().getKeyLength(), keyType.getOutputTagLength());

            byte[] decryptedData = iesCipher.engineDoFinal(encryptedData, 0, encryptedData.length);
            return new SecretKeySpec(decryptedData, "AES");
        }catch(BadPaddingException e){
            throw new InvalidKeyException("Error decrypting symmetric key using supplied private key: " + e.getMessage(), e);
        }
    }


    /**
     * This method generated the HashedId8 certificate identifier value
     */
    public HashedId8 getCertificateHashId(EtsiTs103097Certificate certificate, AlgorithmType hashAlgorithm) throws IOException, NoSuchAlgorithmException
    {
        return new HashedId8(digest(certificate.getEncoded(), hashAlgorithm));
    }

    /**
     * This method generated the HashedId8 secret key identifier value
     */
    public HashedId8 getSecretKeyHashId(SecretKey secretKey, AlgorithmType hashAlgorithm) throws NoSuchAlgorithmException
    {
        return new HashedId8(digest(secretKey.getEncoded(), hashAlgorithm));
    }

}
