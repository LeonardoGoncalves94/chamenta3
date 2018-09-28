package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COEROctetString;

import java.io.IOException;


public class SymmetricEncryptionKey extends COERChoice
{
    private static final int OCTETSTRING_SIZE = 16;

    public enum SymmetricEncryptionKeyChoices implements COERChoiceEnumeration
    {
        AES_128_CCM;
        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            return new COEROctetString(OCTETSTRING_SIZE,OCTETSTRING_SIZE);
        }
    }

    /**
     * Constructor used when encoding.
     */
    public SymmetricEncryptionKey(SymmetricEncryptionKeyChoices choice, byte[] value)
    {
        super(choice, new COEROctetString(value, OCTETSTRING_SIZE,OCTETSTRING_SIZE));
    }

    /**
     * Constructor used when decoding.
     */
    public SymmetricEncryptionKey()
    {
        super(SymmetricEncryptionKeyChoices.class);
    }

    /**
     * Returns the type of point.
     */
    public SymmetricEncryptionKeyChoices getType()
    {
        return (SymmetricEncryptionKeyChoices) choice;
    }
}
