package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public interface COEREncodable extends Serializable
{
    /**
     * Implementation encodes the data and write it to the data output stream.
     *
     * @param out data output stream
     * @throws IOException if communication problems occurred during serialization.
     */
    public void encode(DataOutputStream out) throws IOException;

    /**
     * Implementation decodes the data and populate its properties.
     *
     * @param in data input stream to read from.
     * @throws IOException if communication problems occurred during serialization.
     */
    public void decode(DataInputStream in) throws IOException;
}

