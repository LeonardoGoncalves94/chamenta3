package com.multicert.v2x.datastructures.base;

public class Latitude extends NinetyDegreeInt
{
    /**
     * Constructor used when encoding
     * @param value between -900000000 and 900000000 (Unkown 900000001)
     */

    public Latitude(long value){
        super(value);
    }
    /**
     * Constructor used for decoding
     */
    public Latitude(){
        super();
    }

    @Override
    public String toString() {
        long val = getValueAsLong();
        return "Latitude [" + (val!= UNKNOWN ? val : "UNKNOWN") +"]";
    }
}
