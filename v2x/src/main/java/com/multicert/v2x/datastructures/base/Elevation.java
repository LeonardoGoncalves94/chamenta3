package com.multicert.v2x.datastructures.base;

public class Elevation extends ElevInt
{
    /**
     * Constructor used when encoding
     * @param elevationDecimeters should be height in decimeters, between -4095 and 61439
     */
    public Elevation(int elevationDecimeters) throws IllegalArgumentException
    {
        super(elevationDecimeters);
    }

    /**
     * Constructor used when decoding
     */
    public Elevation()
    {
        super();
    }


    @Override
    public String toString() {
        return "Elevation [" + getElevationInDecimeters() + "(" + Integer.toString(getEncodedElevation(),16)+ ")"+ "]";
    }
}
