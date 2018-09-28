package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

public class NinetyDegreeInt extends COERInteger
{
    public static final long MIN = -900000000L;
    public static final long MAX = 900000000L;
    public static final long UNKNOWN = 900000001L;

    /**
     * Constructor used when encoding
     *
     * @param value between -900000000 and 900000000 (unknown value is 900000001)
     */
    public NinetyDegreeInt(long value)
    {
        super(value, MIN, UNKNOWN);
    }

    /**
     * Constructor used when decoding.
     */
    public NinetyDegreeInt()
    {
        super(MIN, UNKNOWN);
    }
}
