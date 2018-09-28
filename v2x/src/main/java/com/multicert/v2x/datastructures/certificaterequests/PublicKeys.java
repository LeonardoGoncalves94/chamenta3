package com.multicert.v2x.datastructures.certificaterequests;

import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.PublicEncryptionKey;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;

import java.io.IOException;

public class PublicKeys extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 2;
    protected static final int VERIFICATION_KEY = 0;
    protected static final int ENCRYPTION_KEY = 1;

    /**
     * Constructor used when encoding (populates a sequence with its values)
     *
     */
    public PublicKeys(PublicVerificationKey verificationKey, PublicEncryptionKey encryptionKey) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(VERIFICATION_KEY, verificationKey);
        setComponentValue(ENCRYPTION_KEY, encryptionKey);
    }

    /**
     * Constructor used when decoding (creates an empty sequence)
     *
     */
    public PublicKeys()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public void createSequence()
    {
        addComponent(VERIFICATION_KEY, false, new PublicVerificationKey(), null);
        addComponent(ENCRYPTION_KEY, true, new PublicEncryptionKey(), null);
    }

    /**
     * @return the encryption key, optional
     */
    public PublicEncryptionKey getEncryptionKey()
    {
        return (PublicEncryptionKey) getComponentValue(ENCRYPTION_KEY);
    }

    /**
     * @return the verification key, required
     */
    public  PublicVerificationKey getVerificationKey()
    {
        return (PublicVerificationKey) getComponentValue(VERIFICATION_KEY);
    }

    public String toString()
    {
        return "publicKeys [verificationKey=" + getVerificationKey().toString().replaceAll("PublicVerificationKey ", "") + ", encryptionKey=" + (getEncryptionKey() != null ? getEncryptionKey().toString().replaceAll("PublicEncryptionKey ", "") : "NULL") + "]";
    }

}
