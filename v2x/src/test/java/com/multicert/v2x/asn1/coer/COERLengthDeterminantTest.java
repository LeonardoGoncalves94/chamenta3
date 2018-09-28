package com.multicert.v2x.asn1.coer;

import org.junit.Test;

import java.io.*;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class COERLengthDeterminantTest
{
    @Test
    public void testShortFormEncodeAndDecode() throws IOException //short form holds a length <= than 127
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(BigInteger.valueOf(50));
        lengthDeterminant.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERLengthDeterminant lengthDeterminantDecoded = new COERLengthDeterminant();
        lengthDeterminantDecoded.decode(dis);

        assertEquals(BigInteger.valueOf(50), lengthDeterminantDecoded.getLength());
    }

    @Test
    public void testLongFormEncodeAndDecode() throws IOException // long form holds a length >= 128
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(BigInteger.valueOf(500));
        lengthDeterminant.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERLengthDeterminant lengthDeterminantDecoded = new COERLengthDeterminant();
        lengthDeterminantDecoded.decode(dis);

        assertEquals(BigInteger.valueOf(500), lengthDeterminantDecoded.getLength());
    }
}