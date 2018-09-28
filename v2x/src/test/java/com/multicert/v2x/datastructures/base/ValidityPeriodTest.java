package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;
import java.util.Date;

import static org.junit.Assert.*;

public class ValidityPeriodTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        ValidityPeriod validityPeriod = new ValidityPeriod(new Date(), Duration.DurationTypes.MINUTES, 6);
        validityPeriod.encode(dos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ValidityPeriod validityPeriodDecoded = new ValidityPeriod();
        validityPeriodDecoded.decode(dis);

        assertEquals(validityPeriod.toString(), validityPeriodDecoded.toString());

    }
}