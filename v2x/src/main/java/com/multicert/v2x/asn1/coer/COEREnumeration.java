package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * COER encoding of an enumeration following the section 11 fo the ISO/IEC 8825-7:2015 standard,
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COEREnumeration implements COEREncodable
{
    COEREnumerationType enumerationValue;
    protected Class<?> enumeration;

    /**
     * Constructor used when encoding a COEREumeration
     *
     *@param enumerationValue the item of an enumeration implementing COEREnumerationType
     */
    public COEREnumeration(COEREnumerationType enumerationValue)
    {
        this.enumerationValue = enumerationValue;
    }

    /**
     * Constructor used when decoding a COEREnumeration
     *
     * @param enumeration the full enumeration (implements COEREnumerationType) to get the value from
     */
    public COEREnumeration(Class<?> enumeration)
    {
        this.enumeration = enumeration;
    }

    public void encode(DataOutputStream out) throws IOException
    {
        BigInteger enumerationOrdinal = BigInteger.valueOf(enumerationValue.ordinal());
        byte[] ordinalAsBytes = enumerationOrdinal.toByteArray();

        if(ordinalAsBytes.length > 127)
        {
            throw new IOException("Error encoding COER enumeration: enumeration length  must be less than 2^1016 octets.");
        }
        if(enumerationOrdinal.compareTo(BigInteger.ZERO) != -1 && enumerationOrdinal.compareTo(BigInteger.valueOf(127)) != 1) //short form
        {
            out.write(ordinalAsBytes);
            return;
        }
        // long form
        int signOctet = 0;
        if(ordinalAsBytes[0] == 0x00 && ordinalAsBytes.length > 1)
        {
            signOctet++;
        }

        int lengthIndicator = (ordinalAsBytes.length - signOctet) | 0x80;
        out.write(lengthIndicator);
        out.write(ordinalAsBytes, signOctet, ordinalAsBytes.length -signOctet);
    }

    public void decode(DataInputStream in) throws IOException
    {
        BigInteger ordinal;
        int firstValue = in.read();
        if (firstValue <= 127)
        {
            ordinal = BigInteger.valueOf(firstValue);
        }
        else
        {
            int lengthIndicator = firstValue & 0x7f;

            byte[] lengthValue = new byte[lengthIndicator];
            in.read(lengthValue, 0, lengthIndicator);
            ordinal = new BigInteger(lengthValue);
        }

        enumerationValue = (COEREnumerationType) enumeration.getEnumConstants()[ordinal.intValue()];
    }

    public COEREnumerationType getValue ()
    {
        return this.enumerationValue;
    }

    @Override
    public String toString() {
        return "COEREnumeration [value=" + enumerationValue + "]";
    }
}
