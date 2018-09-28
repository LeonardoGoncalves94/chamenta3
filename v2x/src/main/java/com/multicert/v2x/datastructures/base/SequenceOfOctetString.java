package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfOctetString extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public SequenceOfOctetString(COEROctetString[] sequenceValues)
    {
        super(sequenceValues);
    }

    /**
     * Constructor used when decoding
     */
    public SequenceOfOctetString()
    {
        super(new COEROctetString());
    }


}
