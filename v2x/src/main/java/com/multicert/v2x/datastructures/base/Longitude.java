package com.multicert.v2x.datastructures.base;

public class Longitude extends OneEightyDegreeInt
{
    /**
     * Constructor used when decoding.
     */
    public Longitude() {
        super();
    }

    /**
     * Constructor used when encoding
     * @param value no more than 1 800 000 000 and no less than -1 799 999 999 or 1 800 000 001 for UNKNOWN
     */
    public Longitude(long value) {
        super(value);
    }

    @Override
    public String toString() {
        long val = getValueAsLong();
        return "Longitude [" + (val!= UNKNOWN ? val : "UNKNOWN") +"]";
    }
}
