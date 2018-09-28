package com.multicert.v2x.asn1.coer;

import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class COERBitStringTest
{
    @Test
    public void testGetAndSet() throws IOException
    {
        COERBitString test = new COERBitString(5,3, true); // 0b101
        assertTrue(test.getFlag(0)); // assert if bit in position 0 is set to true
        assertFalse(test.getFlag(1)); // assert if bit in position 1 is set to false
        assertTrue(test.getFlag(2));

        COERBitString test2 = new COERBitString(0,3, true); // 0b101
        test2.setFlag(0,true); // sets bit in position 0 to true
        assertTrue(test2.getFlag(0));
        test2.setFlag(1,true); // sets bit in position 1 to true
        assertTrue(test2.getFlag(1));
        test2.setFlag(2,true); // sets bit in position 2 to true
        assertTrue(test2.getFlag(2));
    }

    @Test
    public void testFixedLengthEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERBitString bitString = new COERBitString(5,3, true); // constructor for encoding 0b101
        bitString.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray())); // the encoded BitString is stored in the ByteArrayOutputStream baos
        COERBitString bitStringDecoded = new COERBitString(3); // constructor do decoding a BitString of fixed length
        bitStringDecoded.decode(dis);

        assertEquals(5,bitStringDecoded.bitString);
        assertEquals(3,bitStringDecoded.length.intValue());
        assertTrue(bitStringDecoded.isFixedSize);
    }

    @Test
    public void tesVariableLengthEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERBitString bitString = new COERBitString(5,3, false); // constructor for encoding 0b101
        bitString.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray())); // the encoded BitString is stored in the ByteArrayOutputStream baos
        COERBitString bitStringDecoded = new COERBitString(); // constructor do decoding a BitString of variable length
        bitStringDecoded.decode(dis);

        assertEquals(5,bitStringDecoded.bitString);

    }
}