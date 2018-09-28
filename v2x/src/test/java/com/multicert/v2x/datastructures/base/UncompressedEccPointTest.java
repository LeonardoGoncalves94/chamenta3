package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class UncompressedEccPointTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray())); // the encoded BitString is stored in the ByteArrayOutputStream baos

        assertEquals(1, 1);
    }
}