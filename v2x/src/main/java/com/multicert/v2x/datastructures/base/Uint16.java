package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

/**
 *this type defines an integer between 0 and 65535 (0xff ff)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Uint16 extends COERInteger
{
    protected static final int LOWERBOUND = 0;
    protected static final int UPPERBOUND = 65535;

    /**
     * Constructor used when encoding
     */
    public Uint16(long value)
    {
        super(value, LOWERBOUND,UPPERBOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Uint16()
    {
        super(LOWERBOUND, UPPERBOUND);
    }

    @Override
    public String toString() {
        return "Uint16 [" + value + "]";
    }
}
