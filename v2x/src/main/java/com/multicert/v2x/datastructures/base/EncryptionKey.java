package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;

import java.io.IOException;


public class EncryptionKey extends COERChoice
{

    private static final long serialVersionUID = 1L;

    public enum EncryptionKeyChoices implements COERChoiceEnumeration
    {
        PUBLIC,
        SYMMETRIC;

       public int myOrdinal()
       {
           return this.ordinal();
       }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case PUBLIC:
                    return new PublicEncryptionKey();
                default:
                    return new SymmetricEncryptionKey();
            }
        }
    }

    /**
     * Constructor used when encoding public key.
     */
    public EncryptionKey(PublicEncryptionKey publicKey) throws IllegalArgumentException
    {
        super(EncryptionKeyChoices.PUBLIC, publicKey);
    }

    /**
     * Constructor used when encoding symmetric key.
     */
    public EncryptionKey(SymmetricEncryptionKey symmetricKey) throws IllegalArgumentException
    {
        super(EncryptionKeyChoices.SYMMETRIC, symmetricKey);
    }

    /**
     * Constructor used when decoding
     */
    public EncryptionKey() throws IllegalArgumentException
    {
        super(EncryptionKeyChoices.class);
    }


    /**
     * Returns the type of key.
     */
    public EncryptionKeyChoices getType(){
        return (EncryptionKeyChoices) choice;
    }
}
