package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;
import java.io.IOException;

public class CircularRegion extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;

    private static final int CENTER = 0;
    private static final int RADIUS = 1;

    /**
     * Constructor used when encoding
     *
     */
    public CircularRegion(TwoDLocation center, Uint16 radius) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(CENTER, center);
        setComponentValue(RADIUS, radius);
    }

    /**
     * Constructor used when decoding
     *
     */
    public CircularRegion()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    private void createSequence()
    {
        addComponent(CENTER,false, new TwoDLocation(), null);
        addComponent(RADIUS, false, new Uint16(), null);
    }

    /**
     *
     * @return the northwest position
     */
    public TwoDLocation getCenter(){
        return (TwoDLocation) getComponentValue(CENTER);
    }

    /**
     *
     * @return the radius in meter
     */
    public int getRadius(){
        return (int) ((Uint16) getComponentValue(RADIUS)).getValueAsLong();
    }

    @Override
    public String toString() {
        return "CircularRegion [center=" + getCenter().toString().replace("TwoDLocation ", "") + ", radius=" +  getRadius() + "]";
    }

}
