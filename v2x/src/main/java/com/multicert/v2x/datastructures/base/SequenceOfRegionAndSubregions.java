package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfRegionAndSubregions extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public SequenceOfRegionAndSubregions(RegionAndSubregions[] values){
        super(values);
    }

    /**
     * Constructor used when decoding
     */
    public SequenceOfRegionAndSubregions(){
        super(new RegionAndSubregions());
    }

}
