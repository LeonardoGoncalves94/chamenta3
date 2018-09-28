package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class RectangularRegion extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int NORTH_WEST = 0;
    private static final int SOUTH_EAST = 1;

    /**
     * Constructor used when encoding a RectangularRegion
     *
     * @throws IllegalArgumentException if positions are equal
     */
    public RectangularRegion(TwoDLocation northWest, TwoDLocation southEast) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(NORTH_WEST, northWest);
        setComponentValue(SOUTH_EAST, southEast);

        if(northWest != null && northWest.equals(southEast)){
            throw new IllegalArgumentException("Error encoding RectangularRegion: North west position cannot be the same as south east position.");
        }
    }

    /**
     * Constructor used when deconding a RectangularRegion
     */
    public RectangularRegion()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public void createSequence()
    {
        addComponent(NORTH_WEST, false, new TwoDLocation(), null);
        addComponent(SOUTH_EAST, false, new TwoDLocation(), null);
    }

    /**
     *
     * @return the northwest position
     */
    public TwoDLocation getNorthWest(){
        return (TwoDLocation) getComponentValue(NORTH_WEST);
    }

    /**
     *
     * @return the southeast position
     */
    public TwoDLocation getSouthEast(){
        return (TwoDLocation) getComponentValue(SOUTH_EAST);
    }

}
