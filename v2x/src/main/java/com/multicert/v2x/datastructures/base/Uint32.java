package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

/**
 *this type defines an integer between 0 and 4294967295 (0xff ff ff ff)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Uint32 extends COERInteger
{
    protected static final int LOWERBOUND = 0;
    protected static final long UPPERBOUND = 4294967295L;

    /**
     * Constructor used when encoding when value fits in long form
     */
    public Uint32(long value)
    {
        super(value, LOWERBOUND, UPPERBOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Uint32()
    {
        super(LOWERBOUND, UPPERBOUND);
    }

    @Override
    public String toString() {
        return "Uint32 [" + value + "]";
    }
}
