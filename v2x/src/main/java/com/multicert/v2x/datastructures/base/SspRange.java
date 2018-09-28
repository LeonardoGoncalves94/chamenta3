package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COERNull;

import java.io.IOException;

public class SspRange extends COERChoice
{
    public enum SspRangeTypes implements COERChoiceEnumeration
    {
        OPAQUE,
        ALL;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case OPAQUE:
                    return new SequenceOfOctetString();
                default:
                    return new COERNull();
            }
        }
    }

    /**
     * Constructor used when encoding
     *
     * @param type type of SspRange
     * @param value SequenceOfOctetString used if type is opaque, otherwise use null.
     */
    public SspRange(SspRangeTypes type, SequenceOfOctetString value)
    {

        super(type, type == SspRangeTypes.OPAQUE ? value : new COERNull());
    }

    /**
     * Constructor used when decoding
     */
    public SspRange()
    {
        super(SspRangeTypes.class);
    }
}
