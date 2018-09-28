package com.multicert.v2x.IdentifiedRegions;


import com.multicert.v2x.datastructures.base.CountryOnly;
import com.multicert.v2x.datastructures.base.GeographicRegion;
import com.multicert.v2x.datastructures.base.IdentifiedRegion;
import com.multicert.v2x.datastructures.base.SequenceOfIdentifiedRegion;

import java.awt.*;

/**
 * This class identifies the existing countries to specify the a region in the tests
 *
 */
public class Countries
{
    /**
     * List of countries
     */
    public enum CountryTypes{
        PORTUGAL,
        SPAIN,
        FRANCE,
        GERMANY;

        public int getCountryNumber()
        {
            switch(this){
                case PORTUGAL: return 1;
                case SPAIN: return 2;
                case FRANCE: return 3;
                case GERMANY: return 4;
                default: return 0;
            }
        }

        public String toString()
        {
            switch(this){
                case PORTUGAL: return "Portugal";
                case SPAIN: return "Spain";
                case FRANCE: return "France";
                case GERMANY: return "Germany";
                default: return "Country not found";
            }
        }
    }

    /**
     * Help method that generates GeographicRegion based on an array of identified country
     * @param contries the array of counties, should be constructed using the list of countries (i.e CountryTypes) present in this class
     * @return a certificate GeographicRegion validity
     */
    static public GeographicRegion getGeographicRegion(CountryTypes[] contries)
    {
        int size = contries.length;
        IdentifiedRegion[] identifiedRegions = new IdentifiedRegion[size];
        for(int c = 0; c < size; c ++)
        {

            IdentifiedRegion identifiedRegion = new IdentifiedRegion(new CountryOnly(contries[c].getCountryNumber()));
            identifiedRegions[c] = identifiedRegion;
        }
        SequenceOfIdentifiedRegion sequenceOfIdentifiedRegion = new SequenceOfIdentifiedRegion(identifiedRegions);
        return new GeographicRegion(sequenceOfIdentifiedRegion);
    }



}
