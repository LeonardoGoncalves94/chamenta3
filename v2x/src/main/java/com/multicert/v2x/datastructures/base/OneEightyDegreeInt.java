package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERInteger;

import java.math.BigInteger;

public class OneEightyDegreeInt extends COERInteger
{
    public static final long MIN = -1799999999L;
    public static final long MAX = 900000000;
    public static final long UNKNOWN = 900000001;

    /**
     * Constructor used when encoding
     */
    public OneEightyDegreeInt(long value)
    {
        super(value, MIN, UNKNOWN);
    }

    /**
     * Constructor used for decoding
     */
    public OneEightyDegreeInt()
    {
        super(MIN, UNKNOWN);
    }
}
