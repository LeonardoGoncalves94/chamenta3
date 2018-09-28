package com.multicert.v2x.asn1.coer;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class COEROctetStringTest
{
    @Test
    public void testEncodeAndDecodeKnownBounds()throws IOException
    {
        byte[] data = {1,2,3};
        COEROctetString octetString = new COEROctetString(data, 3,3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        octetString.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COEROctetString octetStringDecoded = new COEROctetString(3,3);
        octetStringDecoded.decode(dis);

        assertEquals(toString(data), toString(octetStringDecoded.data));
    }

    @Test
    public void testEncodeAndDecodeUnknownBounds()throws IOException
    {
        byte[] data = {1,2,3};
        COEROctetString octetString = new COEROctetString(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        octetString.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COEROctetString octetStringDecoded = new COEROctetString();
        octetStringDecoded.decode(dis);

        assertEquals(toString(data), toString(octetStringDecoded.data));
    }

    public String toString(byte[] data)
    {
        String result= "";
        for(int i = 0; i < data.length; i ++)
        {
            result += data[i] +" ";
        }
        return result;
    }
}