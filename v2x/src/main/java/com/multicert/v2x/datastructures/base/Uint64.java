package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

/**
 *this type defines an integer between 0 and 18446744073709551615 (0xff ff ff ff ff ff ff ff)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Uint64 extends COERInteger
{
    protected static final BigInteger LOWERBOUND = BigInteger.ZERO;
    protected static final BigInteger UPPERBOUND = new BigInteger("18446744073709551615");

    /**
     * Constructor used when encoding
     */
    public Uint64(BigInteger value)
    {
        super(value, LOWERBOUND,UPPERBOUND);
    }

    /**
     * Constructor used when decoding
     */
    public Uint64()
    {
        super(LOWERBOUND, UPPERBOUND);
    }

    @Override
    public String toString() {
        return "Uint64 [" + value + "]";
    }
}
