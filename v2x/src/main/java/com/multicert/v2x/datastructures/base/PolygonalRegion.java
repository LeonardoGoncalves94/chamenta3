package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

import java.io.IOException;

public class PolygonalRegion extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public PolygonalRegion(TwoDLocation[] values) throws IOException
    {
        super(values);

        if(values.length < 3)
        {
            throw new IOException("Error encoding Polygonal Region: It must have at least 3 TwoDlocations");
        }

    }

    /**
     * Constructor used when decoding
     */
    public PolygonalRegion()
    {
        super(new TwoDLocation());
    }
}
