package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class CountryAndRegions extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int COUNTRY_ONLY = 0;
    private static final int REGIONS = 1;

    /**
     * Constructor used when encoding
     *
     */
    public CountryAndRegions(CountryOnly countryOnly, SequenceOfUint8 regions) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(COUNTRY_ONLY, countryOnly);
        setComponentValue(REGIONS, regions);
    }
    /**
     * Constructor used when decoding
     *
     */
    public CountryAndRegions()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    private void createSequence()
    {
        addComponent(COUNTRY_ONLY,false, new CountryOnly(), null);
        addComponent(REGIONS,false, new SequenceOfUint8(), null);
    }
}
