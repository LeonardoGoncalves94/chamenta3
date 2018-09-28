package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEREnumeration;
import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;
import java.security.PublicKey;

/**
 * This class represents a public encryption key of a certificate, it contains the supported symmetric algorithm (of the symmetric key to be encrypted) and the public key (to encrypt the symmetric key)
 */
public class PublicEncryptionKey extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int SUPPORTED_SYMM_ALG = 0;
    private static final int PUBLICKEY = 1;

    /**
     * Constructor used when encoding
     */
    public PublicEncryptionKey(SymmAlgorithm symmAlgorithm, BasePublicEncryptionKey publicKey) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(SUPPORTED_SYMM_ALG, new COEREnumeration(symmAlgorithm));
        setComponentValue(PUBLICKEY,publicKey);
    }

    /**
     * Constructor used when decoding
     */
    public PublicEncryptionKey()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public void createSequence()
    {
        addComponent(SUPPORTED_SYMM_ALG, false, new COEREnumeration(SymmAlgorithm.class), null);
        addComponent(PUBLICKEY, false, new BasePublicEncryptionKey(), null);
    }

    public SymmAlgorithm getSupportedSymmalgorith()
    {
        return (SymmAlgorithm) ((COEREnumeration) getComponentValue(SUPPORTED_SYMM_ALG)).getValue();
    }

    public BasePublicEncryptionKey getPublicKey()
    {
        return (BasePublicEncryptionKey) getComponentValue(PUBLICKEY);
    }

    @Override
    public String toString() {
        return "PublicEncryptionKey [supportedSymmAlg=" + getSupportedSymmalgorith() + ", publicKey=" + getPublicKey().toString().replace("BasePublicEncryptionKey ","") + "]";
    }
}
