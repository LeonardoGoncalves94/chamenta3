package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class COERNull implements COEREncodable
{

    /**
     * Constructor used for encoding/decoding a COER Null value.
     */
    public COERNull()
    {

    }

    public void encode(DataOutputStream out) throws IOException
    {

    }

    public void decode(DataInputStream in) throws IOException
    {

    }

    @Override
    public String toString() {
        return "COERNull []";
    }

}
