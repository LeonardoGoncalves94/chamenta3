package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfUint8 extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public SequenceOfUint8(Uint8[] sequenceValues)
    {
        super(sequenceValues);
    }

    /**
     * Constructor used when decoding
     */
    public SequenceOfUint8()
    {
        super(new Uint8());
    }


}
