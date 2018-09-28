package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;


import static org.junit.Assert.*;

public class DurationTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Duration duration = new Duration(Duration.DurationTypes.MINUTES, 120);
        duration.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Duration durationDecoded = new Duration();
        durationDecoded.decode(dis);

        assertEquals(duration.toString(), durationDecoded.toString());

    }
}