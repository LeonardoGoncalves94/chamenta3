package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;
import java.util.Date;

import static org.junit.Assert.*;

public class Time32Test
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Time32 testTime = new Time32(new Date());
        testTime.encode(dos);
        Date now = testTime.asDate();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray())); // the encoded BitString is stored in the ByteArrayOutputStream baos
        Time32 testTimeDecoded = new Time32();
        testTimeDecoded.decode(dis);
        Date nowDecoded = testTimeDecoded.asDate();
        assertEquals(now, nowDecoded);
    }

}