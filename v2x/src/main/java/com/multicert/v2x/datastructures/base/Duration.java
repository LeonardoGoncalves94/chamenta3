package com.multicert.v2x.datastructures.base;

/**
 * This class represents the duration of the validity of a certificate. The duration is composed of a value and a type, the value is a Uint16 and the type is an item of the enumeration DurationChoices
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;

import java.math.BigInteger;

public class Duration extends COERChoice
{
    public enum DurationTypes implements COERChoiceEnumeration
    {
        MICROSECONDS,
        MILLISECONDS,
        SECONDS,
        MINUTES,
        SIXTY_HOURS,
        YEARS;

        @Override
        public COEREncodable getEncodableType()
        {
            return new Uint16();
        }

        public int myOrdinal()
        {
            return this.ordinal();
        }
    }

    /**
     * Constructor used when encoding
     *
     * @param type an item of the DurationChoices
     * @param value the value for that choice
     */
    public Duration(Duration.DurationTypes type, int value)
    {
        super(type, new Uint16(value));
    }

    /**
     * Constructor used when decoding.
     */
    public Duration()
    {
        super(DurationTypes.class);
    }

    /**
     * Returns the unit of the Duration, which is an item of DurationChoices
     */
    public DurationTypes getChoice()
    {
        return (DurationTypes) choice;
    }

    @Override
    public String toString() {
        return "Choice:"+ choice + " Duration:" + ((Uint16) value).getValueAsLong();
    }
}
