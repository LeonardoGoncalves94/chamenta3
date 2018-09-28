package com.multicert.v2x.datastructures.base;

/**
 * This class identifies a series of CRLs issued under the authority with a particular CRACA.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class CrlSeries extends Uint16
{
    /**
     * Constructor used when encoding
     */
    public CrlSeries(int crlSeries)
    {
        super(crlSeries);
    }

    /**
     * Constructor used when decoding
     */
    public CrlSeries()
    {
        super();
    }

    @Override
    public String toString() {
        return "CrlSeries [" + getValueAsLong() + "]";
    }
}
