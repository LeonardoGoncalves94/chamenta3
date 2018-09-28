package com.multicert.v2x.datastructures.base;

import net.time4j.Moment;
import net.time4j.TemporalType;
import net.time4j.scale.TimeScale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Time64 extends Uint64
{
    private static final long SECONDS_FROM_ZERO_TO_2004 = 1009843232L;

    /**
     * Main constructor
     *
     * @param date the timestamp to transform, represents the time at which the Date object was created.
     */
    public Time64(Date date)
    {
        super();
        Moment moment = TemporalType.JAVA_UTIL_DATE.translate(date); // create a Moment from java.util.Date
        BigDecimal bd = moment.transform(TimeScale.TAI); // translate that moment to TAI
        value = bd.subtract(new BigDecimal(SECONDS_FROM_ZERO_TO_2004)).multiply(new BigDecimal(1000000)).toBigInteger(); //(seconds elapsed from time ZERO to date) - ( seconds elapsed from ZERO to 2004)
    }

    /**
     * Constructor used when encoding
     */
    public Time64(BigInteger time)
    {
        super(time);
    }

    /**
     * Constructor used when decoding
     */
    public Time64()
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
        long elapsedTime = this.value.divide(new BigInteger("1000000")).longValue();
        int nanoSeconds = this.value.remainder(new BigInteger("1000000")).intValue() * 1000;
        Moment moment = Moment.of(elapsedTime + SECONDS_FROM_ZERO_TO_2004,nanoSeconds, TimeScale.TAI);
        return TemporalType.JAVA_UTIL_DATE.from(moment);
    }

    @Override
    public String toString() {
        return "Time64 [timeStamp=" + asDate() + " (" + getValue() + ")]";
    }
}
