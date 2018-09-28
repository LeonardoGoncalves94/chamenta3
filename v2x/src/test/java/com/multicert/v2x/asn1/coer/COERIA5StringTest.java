package com.multicert.v2x.asn1.coer;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class COERIA5StringTest
{
    String testString = "IDString";
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERIA5String ia5String = new COERIA5String(testString, testString.length()); // constructor for encoding "IDString"
        ia5String.encode(dos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray())); // the encoded BitString is stored in the ByteArrayOutputStream baos
        COERIA5String ia5StringDecoded = new COERIA5String(8); // constructor for encoding "IDString"
        ia5StringDecoded.decode(dis);

        assertEquals("IDString",ia5StringDecoded.getString());

    }
}