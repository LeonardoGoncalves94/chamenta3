package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class CountryAndSubregions extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 2;
    protected static final int COUNTRY = 0;
    protected static final int REGION_AND_SUBREGIONS = 1;

    /**
     * Constructor used when encoding
     */
    public CountryAndSubregions(CountryOnly countryOnly, SequenceOfRegionAndSubregions sequenceOfRegionAndSubregions) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(COUNTRY, countryOnly);
        setComponentValue(REGION_AND_SUBREGIONS, sequenceOfRegionAndSubregions);
    }

    /**
     * Constructor used when decoding
     */
    public CountryAndSubregions()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    private void createSequence()
    {
        addComponent(COUNTRY, false, new CountryOnly(), null);
        addComponent(REGION_AND_SUBREGIONS, false, new SequenceOfRegionAndSubregions(), null);
    }
}
