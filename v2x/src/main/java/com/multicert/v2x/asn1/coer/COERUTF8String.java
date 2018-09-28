package com.multicert.v2x.asn1.coer;

import java.io.UnsupportedEncodingException;

public class COERUTF8String extends COEROctetString
{
    /**
     * Constructor used when encoding a UFT-8 string with known size
     */
    public COERUTF8String(String utf8String, Integer lowerBound, Integer upperBound) throws UnsupportedEncodingException
    {
        super(utf8String.getBytes("UTF-8"), lowerBound, upperBound);
    }

    /**
     *Constructor used when encoding a UFT-8 string with unknown size
     */
    public COERUTF8String(String utf8String) throws UnsupportedEncodingException
    {
        super(utf8String.getBytes("UTF-8"));
    }

    /**
     * Constructor used when decoding a string with unknown size
     */
    public COERUTF8String()
    {
        super();
    }

    /**
     * Constructor used when decoding a string with known size
     */
    public COERUTF8String(Integer lowerBound, Integer upperBound)
    {
        super(lowerBound, upperBound);
    }

    /**
     *
     * @return the UTF8 String inside the COER encoding.
     */
    public String getUTF8String()
    {
        try {
            return new String(getData(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error unsupported encoding: UTF-8");
        }
    }

}
