package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERUTF8String;

import java.io.IOException;

/**
 * Hostname is a UTF-8 string as defined in IETF RFC 3629.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Hostname extends COERUTF8String
{
    private static final int LOWER_BOUND = 0;
    private static final int UPPER_BOUND = 255;

    /**
     * Constructor used when encoding
     */
    public Hostname(String hostname) throws IOException
    {
        super(hostname,LOWER_BOUND,UPPER_BOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Hostname()
    {
        super(LOWER_BOUND,UPPER_BOUND);
    }

    @Override
    public String toString() {
        return "Hostname [" + getUTF8String() + "]";
    }


}
