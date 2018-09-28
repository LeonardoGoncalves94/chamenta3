package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERBitString;

import java.io.IOException;


public class EndEntityType extends COERBitString
{
    private static final int BITSTRING_SIZE = 8;
    private static final int APP = 0;
    private static final int ENROL = 1;

    /**
     * Constructor used when encoding
     */
    public EndEntityType(boolean app, boolean enroll) throws IllegalArgumentException, IOException
    {
        super(0,BITSTRING_SIZE,true);
        setFlag(APP, app);
        setFlag(ENROL, enroll);
    }

    /**
     * Constructor used when decoding
     */
    public EndEntityType()
    {
        super(BITSTRING_SIZE);
    }

    public boolean isApp()
    {
        return getFlag(APP);
    }

    public boolean isEnrol()
    {
        return getFlag(ENROL);
    }


    @Override
    public String toString() {
        return "EndEntityType [app=" + isApp()+ ", enroll=" +  isEnrol() + "]";
    }
}
