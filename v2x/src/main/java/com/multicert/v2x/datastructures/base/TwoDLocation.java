package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;


import java.io.IOException;

public class TwoDLocation extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private NinetyDegreeInt latitude;
    private OneEightyDegreeInt longitude;

    /**
     * Constructor used when encoding
     */
    public TwoDLocation(Latitude latitude, Longitude longitude) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(LATITUDE, latitude);
        setComponentValue(LONGITUDE, longitude);

        this.latitude = latitude;
        this.longitude = longitude;
        if(latitude.getValueAsLong() == latitude.UNKNOWN || longitude.getValueAsLong() == longitude.UNKNOWN)
        {
            throw new IllegalArgumentException("Error encoding TwoDLocation: Both latitude and longitude values must be known");
        }
    }

    /**
     * Constructor used when decoding
     */
    public TwoDLocation()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    private void createSequence()
    {
        addComponent(LATITUDE,false, new NinetyDegreeInt(), null);
        addComponent(LONGITUDE, false, new OneEightyDegreeInt(), null);
    }

    public OneEightyDegreeInt getLongitude()
    {
        return longitude;
    }

    public NinetyDegreeInt getLatitude()
    {
        return latitude;
    }
}
