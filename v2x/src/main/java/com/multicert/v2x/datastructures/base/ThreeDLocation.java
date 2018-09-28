package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class ThreeDLocation extends COERSequence
{
    private static final int SEQUENCE_SIZE = 3;

    private static final int LATITUDE = 0;
    private static final int LONGITUDE = 1;
    private static final int ELEVATION = 2;

    /**
     * Constructor used when encoding
     */
    public ThreeDLocation(Latitude latitude, Longitude longitude, Elevation elevation) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(LATITUDE, latitude);
        setComponentValue(LONGITUDE, longitude);
        setComponentValue(ELEVATION, elevation);
    }

    /**
     * Constructor used when encoding
     */
    public ThreeDLocation(long latitude, long longitude, int elevationDecimeters) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(LATITUDE, new Latitude(latitude));
        setComponentValue(LONGITUDE, new Longitude(longitude));
        setComponentValue(ELEVATION, new Elevation(elevationDecimeters));
    }

    /**
     * Constructor used when decoding
     */
    public ThreeDLocation(){
        super(SEQUENCE_SIZE);
        createSequence();
    }

    /**
     *
     * @return the locations longitude
     */
    public Longitude getLongitude(){
        return (Longitude) getComponentValue(LONGITUDE);
    }

    /**
     *
     * @return the locations latitude
     */
    public Latitude getLatitude(){
        return (Latitude) getComponentValue(LATITUDE);
    }

    /**
     *
     * @return the locations elevation
     */
    public Elevation getElevation(){
        return (Elevation) getComponentValue(ELEVATION);
    }


    private void createSequence(){
        addComponent(LATITUDE, false, new Latitude(), null);
        addComponent(LONGITUDE, false, new Longitude(), null);
        addComponent(ELEVATION, false, new Elevation(), null);
    }
}
