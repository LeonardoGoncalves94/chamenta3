package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;

public class GeographicRegion extends COERChoice
{
    public enum GeographicRegionTypes implements COERChoiceEnumeration
    {
        CIRCULAR_REGION,
        SEQUENCE_OF_RECTANGULAR_REGION,
        POLYGONALREGION,
        SEQUENCE_OF_IDENTIFIED_REGION;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            switch (this)
            {
                case CIRCULAR_REGION:
                    return new CircularRegion();
                case SEQUENCE_OF_RECTANGULAR_REGION:
                    return new SequenceOfRectangularRegion();
                case POLYGONALREGION:
                    return new PolygonalRegion();
                default:
                    return new SequenceOfIdentifiedRegion();
            }
        }
    }

    /**
     * Constructor used when encoding a CircularRegion
     *
     */
    public GeographicRegion(CircularRegion value)
    {
        super(GeographicRegionTypes.CIRCULAR_REGION, value);
    }

    /**
     * Constructor used when encoding a SequenceOfRectangularRegion
     *
     */
    public GeographicRegion(SequenceOfRectangularRegion value)
    {
        super(GeographicRegionTypes.SEQUENCE_OF_RECTANGULAR_REGION, value);
    }

    /**
     * Constructor used when encoding a PolygonalRegion
     *
     */
    public GeographicRegion(PolygonalRegion value)
    {
        super(GeographicRegionTypes.POLYGONALREGION, value);
    }

    /**
     * Constructor used when encoding a SequenceOfIdentifiedRegion
     *
     */
    public GeographicRegion(SequenceOfIdentifiedRegion value)
    {
        super(GeographicRegionTypes.SEQUENCE_OF_IDENTIFIED_REGION, value);
    }

    /**
     * Constructor used when decoding
     *
     */
    public GeographicRegion()
    {
        super(GeographicRegionTypes.class);
    }

    /**
     *
     * @return the type of GeographicRegion, which is an item of GeographicRegionChoices
     */
    public GeographicRegionTypes getChoice()
    {
        return (GeographicRegionTypes) choice;
    }

    @Override
    public String toString() {
        return "GeographicRegion [" + value + "]";
    }


}
