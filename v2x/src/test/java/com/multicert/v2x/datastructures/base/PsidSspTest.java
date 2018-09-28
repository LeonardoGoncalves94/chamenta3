package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class PsidSspTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        PsidSsp permissions = new PsidSsp(new Psid(36), null);
        permissions.encode(dos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        PsidSsp decodedpermissions = new PsidSsp();
        decodedpermissions.decode(dis);

        System.out.println(decodedpermissions.toString());
        assertEquals(permissions.toString(), decodedpermissions.toString());

    }
}