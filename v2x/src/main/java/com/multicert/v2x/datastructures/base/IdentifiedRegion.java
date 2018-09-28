package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;

import java.io.IOException;

public class IdentifiedRegion extends COERChoice
{
    public enum IdentifiedRegionTypes implements COERChoiceEnumeration{
        COUNTRY_ONLY,
        COUNTRY_AND_REGIONS,
        COUNTRY_AND_SUBREGIONS;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case COUNTRY_ONLY:
                    return new CountryOnly();
                case COUNTRY_AND_REGIONS:
                    return new CountryAndRegions();
                default: return new CountryAndSubregions();

            }
        }
    }

    /**
     * Constructor used when encoding a choice of type CountryOnly
     *
     */
    public IdentifiedRegion(CountryOnly countryOnly)
    {
        super(IdentifiedRegionTypes.COUNTRY_ONLY, countryOnly);
    }

    /**
     * Constructor used when encoding a choice of type CountryAndRegions
     *
     */
    public IdentifiedRegion(CountryAndRegions countryAndRegions)
    {
        super(IdentifiedRegionTypes.COUNTRY_AND_REGIONS, countryAndRegions);
    }

    /**
     * Constructor used when encoding a choice of type CountryAndSub
     *
     */
    public IdentifiedRegion(CountryAndSubregions countryAndSubregions)
    {
        super(IdentifiedRegionTypes.COUNTRY_AND_SUBREGIONS, countryAndSubregions);
    }

    /**
     * Constructor used when decoding
     *
     */
    public IdentifiedRegion()
    {
        super(IdentifiedRegionTypes.class);
    }

    /**
     *
     * @return the choice of IdentifiedRegion, which is an item of IdentifiedRegionChoices
     */
    public IdentifiedRegionTypes getChoice()
    {
        return (IdentifiedRegionTypes) choice;
    }

    @Override
    public String toString() {
        return "IdentifiedRegion [" + value + "]";
    }
}
