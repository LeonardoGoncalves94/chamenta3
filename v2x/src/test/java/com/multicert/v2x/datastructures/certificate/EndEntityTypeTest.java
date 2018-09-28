package com.multicert.v2x.datastructures.certificate;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class EndEntityTypeTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        EndEntityType endEntityType = new EndEntityType(true, true);
        endEntityType.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        EndEntityType decodedEndEntityType = new EndEntityType();
        decodedEndEntityType.decode(dis);

        assertEquals(endEntityType.toString(), decodedEndEntityType.toString());

    }
}