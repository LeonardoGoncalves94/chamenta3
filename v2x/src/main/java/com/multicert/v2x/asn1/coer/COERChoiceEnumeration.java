package com.multicert.v2x.asn1.coer;

import java.io.IOException;

public interface COERChoiceEnumeration
{
    /**
     * Method that returns the ordinal of an enumeration item
     * */
     int myOrdinal();

    /**
     * Method that returns the type of COEREncodable of a given COERChoiceEnumeration item (to be used when decoding in order to cast COEREncodable to the specific type, see COERchoice for more information)
     */
    COEREncodable getEncodableType() throws IOException;
}
