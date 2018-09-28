package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEREncodable;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SequenceOfPsidSspTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        PsidSsp permissions1 = new PsidSsp(new Psid(36), null);
        PsidSsp permissions2 = new PsidSsp(new Psid(369), null);
        PsidSsp[] values = {permissions1, permissions2};
        SequenceOfPsidSsp appPermissions = new SequenceOfPsidSsp(values);
        appPermissions.encode(dos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SequenceOfPsidSsp decodedapppermissions = new SequenceOfPsidSsp();
        decodedapppermissions.decode(dis);

        assertEquals(appPermissions.toString(), decodedapppermissions.toString());


    }
}