package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class RegionAndSubregions extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 2;
    protected static final int REGION = 0;
    protected static final int SUBREGIONS = 1;

    /**
     * Constructor used when encoding
     */
    public RegionAndSubregions(Uint8 region, SequenceOfUint8 subregions) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(REGION, region);
        setComponentValue(SUBREGIONS, subregions);
    }

    /**
     * Constructor used when decoding
     */
    public RegionAndSubregions()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    private void createSequence()
    {
        addComponent(REGION, false, new Uint8(), null);
        addComponent(SUBREGIONS, false, new SequenceOfUint8(), null);
    }
}
