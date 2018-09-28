package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

/**
 *this type defines an integer between 0 and 255 (0x ff)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Uint8 extends COERInteger
{
    protected static final int LOWERBOUND = 0;
    protected static final int UPPERBOUND = 255;


    /**
     * Constructor used when encoding if value fits in long form
     */
    public Uint8(long value)
    {
        super(value, LOWERBOUND,UPPERBOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Uint8()
    {
        super(LOWERBOUND, UPPERBOUND);
    }


    @Override
    public String toString() {
        return "Uint8 [" + value + "]";
    }
}
