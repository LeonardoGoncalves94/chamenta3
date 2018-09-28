package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

/**
 *this type defines an integer between 0 and 7 (0x07)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Uint3 extends COERInteger
{
    protected static final int LOWERBOUND = 0;
    protected static final int UPPERBOUND = 7;


    /**
     * Constructor used when encoding
     */
    public Uint3(long value)
    {
        super(value, LOWERBOUND,UPPERBOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Uint3()
    {
        super(LOWERBOUND, UPPERBOUND);
    }

    @Override
    public String toString() {
        return "Uint3 [" + value + "]";
    }
}
