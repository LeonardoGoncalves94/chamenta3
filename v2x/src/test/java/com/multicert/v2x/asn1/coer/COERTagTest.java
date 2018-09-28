package com.multicert.v2x.asn1.coer;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class COERTagTest
{
    @Test
    public void testEncodeAndDecodeShorForm() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERTag tag = new COERTag(128,60); //tagClass = 0x80 tagNumber  = 60
        tag.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERTag tagDecoded = new COERTag(); // empty tag
        tagDecoded.decode(dis);

        assertEquals(128, tagDecoded.getTagClass());
        assertEquals(60, tagDecoded.getTagNumber());
    }

    @Test
    public void testEncodeAndDecodelongForm() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERTag tag = new COERTag(0,130);  // tagClass = 0x00 tagNumber  = 60
        tag.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERTag tagDecoded = new COERTag(); // empty tag
        tagDecoded.decode(dis);

        assertEquals(0, tagDecoded.getTagClass());
        assertEquals(130, tagDecoded.getTagNumber());
    }
}