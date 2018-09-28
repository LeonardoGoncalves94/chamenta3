package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class COERIA5String extends COEROctetString
{

    /**
     * Constructor used when encoding a IA5String
     */
    public COERIA5String (String value, int size) throws UnsupportedEncodingException
    {
        super (value.getBytes("US-ASCII"), size, size); // convert the string into ascii bytes

    }

    /**
     * Contructor used when decoding
     */
    public COERIA5String (int size)
    {
        super(size,size);
    }

    /**
     *
     * @return @return the String inside the COER encoding.
     */
    public String getString()
    {
        try {
            return new String(getData(),"US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error unsupported encoding: US-ASCII");
        }
    }
}


