package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;
import java.io.IOException;
import java.util.Date;

/**
 * This type defines the validity period of a certificate.
 * The validity is composed by the stat and duration.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class ValidityPeriod extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 2;

    protected static final int START = 0;
    protected static final int DURATION = 1;

    /**
     * Constructor use when encoding
     *
     * @param start the start of the validity.
     * @param durationType the type of the duration (e.g minutes, years, etc.)
     * @param duration the value of the duration
     */
    public ValidityPeriod(Date start, Duration.DurationTypes durationType, int duration) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(START, new Time32(start));
        setComponentValue(DURATION, new Duration(durationType, duration));
    }

    /**
     * Constructor used when decoding
     *
     * @throws IOException
     */
    public ValidityPeriod() throws IOException
    {
       super(SEQUENCE_SIZE);
       createSequence();
    }

    private void createSequence()
    {
        addComponent(START, false, new Time32(), null);
        addComponent(DURATION, false, new Duration(), null);
    }

    /**
     *
     * @return the validity period start
     */
    public Time32 getStart(){
        return (Time32) getComponentValue(START);
    }

    /**
     *
     * @return the validity period duration
     */
    public Duration getDuration(){
        return (Duration) getComponentValue(DURATION);
    }

    @Override
    public String toString() {
        return "ValidityPeriod [start=" + getStart() + ", duration=" + getDuration()+"]";
    }
}
