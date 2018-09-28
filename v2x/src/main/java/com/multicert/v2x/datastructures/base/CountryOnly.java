package com.multicert.v2x.datastructures.base;

/**
 * This is the integer representation of a country or area identifier
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class CountryOnly extends Uint16
{
    /**
     * Constructor used when encoding
     */
    public CountryOnly(int value)
    {
        super(value);
    }

    /**
     * Constructor used when decoding
     */
    public CountryOnly()
    {
        super();
    }

    @Override
    public String toString() {
        return "CountryOnly [" + getValueAsLong() + "]";
    }
}
