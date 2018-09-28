package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfHashedId3 extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public SequenceOfHashedId3(HashedId3[] values)
    {
        super(values);
    }

    /**
     * Constructor used when decoding
     */
    public SequenceOfHashedId3()
    {
        super(new HashedId3());
    }
}
