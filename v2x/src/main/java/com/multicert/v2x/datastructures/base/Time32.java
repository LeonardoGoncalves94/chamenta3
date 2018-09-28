package com.multicert.v2x.datastructures.base;

import net.time4j.Moment;
import net.time4j.TemporalType;
import net.time4j.scale.TimeScale;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Time32 is an unsigned 32-bit integer giving the number of International Atomic Time (TAI) seconds since 00:00:00 UTC, 1 January, 2004.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class Time32 extends Uint32
{
    private static final long SECONDS_FROM_ZERO_TO_2004 = 1009843232L;

    /**
     * Main constructor
     *
     * @param date the timestamp to transform, represents the time at which the Date object was created.
     */
    public Time32(Date date)
    {
        super();
        Moment moment = TemporalType.JAVA_UTIL_DATE.translate(date); // create a Moment from java.util.Date representing date
        BigDecimal bigDecimal = moment.transform(TimeScale.TAI); // translate that moment to TAI
        value = bigDecimal.subtract(new BigDecimal(SECONDS_FROM_ZERO_TO_2004)).toBigInteger(); // (seconds elapsed from time ZERO to date) - (seconds elapsed from ZERO to 2004) = seconds elapsed from 2004 to date
    }

    /**
     * Constructor used when encoding
     */
    public Time32(long time)
    {
        super(time);
    }

    /**
     * Constructor used when decoding
     */
    public Time32()
    {
        super();
    }

    /**
     * Returns the timestamp as a Java util date.
     *
     * <b>Important: Note that this transformation is sometimes lossy: Leap seconds will get lost as well as micro- or nanoseconds.
     * @return the timestamp value
     */
    public Date asDate(){
        Moment m = Moment.of(value.longValue() + SECONDS_FROM_ZERO_TO_2004, TimeScale.TAI); // add the seconds elapsed from ZERO to 2004
        return TemporalType.JAVA_UTIL_DATE.from(m);
    }

    @Override
    public String toString() {
        return "Time32 [timeStamp=" + asDate() + " (" + getValueAsLong() + ")]";
    }
}
