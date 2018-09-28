package com.multicert.v2x.asn1.coer;


import org.junit.Test;
import java.io.*;
import java.math.BigInteger;
import static org.junit.Assert.*;

public class COERIntegerTest
{
    @Test
    public void testEncodeAndDecodeInt3() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(6, 0,7); // value, lower bound, upper bound
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(0,7);
        integerDecoded.decode(dis);

        assertEquals(6, integerDecoded.getValueAsLong()); // assert value
        assertEquals(0, integerDecoded.getLowerBoundAsLong()); // assert lower bound
        assertEquals(7, integerDecoded.getUpperBoundAsLong()); // assert upper bound
    }

    @Test
    public void testEncodeAndDecodeInt8() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(10, 0,255);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(0,255);
        integerDecoded.decode(dis);

        assertEquals(10, integerDecoded.getValueAsLong());
        assertEquals(0, integerDecoded.getLowerBoundAsLong());
        assertEquals(255, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeInt16() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(10, 0,65535);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(0, 65535);
        integerDecoded.decode(dis);

        assertEquals(10, integerDecoded.getValueAsLong());
        assertEquals(0, integerDecoded.getLowerBoundAsLong());
        assertEquals(65535, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeInt32() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(10, 0,4294967295L);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(0, 4294967295L);
        integerDecoded.decode(dis);

        assertEquals(10, integerDecoded.getValueAsLong());
        assertEquals(0, integerDecoded.getLowerBoundAsLong());
        assertEquals(4294967295L, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeInt64() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(BigInteger.valueOf(10), BigInteger.ZERO,new BigInteger("18446744073709551615"));
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(BigInteger.ZERO,new BigInteger("18446744073709551615"));
        integerDecoded.decode(dis);

        assertEquals(BigInteger.valueOf(10), integerDecoded.value);
        assertEquals(BigInteger.ZERO, integerDecoded.lowerBound);
        assertEquals(new BigInteger("18446744073709551615"), integerDecoded.upperBound);
    }

    @Test
    public void testEncodeAndDecodeInt64Plus() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(BigInteger.valueOf(10), BigInteger.ZERO,new BigInteger("18446744073709551616"));
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(BigInteger.ZERO,new BigInteger("18446744073709551616"));
        integerDecoded.decode(dis);

        assertEquals(BigInteger.valueOf(10), integerDecoded.value);
        assertEquals(BigInteger.ZERO, integerDecoded.lowerBound);
        assertEquals(new BigInteger("18446744073709551616"), integerDecoded.upperBound);
    }

    @Test
    public void testEncodeAndDecodeIntUnknownUpperBound() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(BigInteger.valueOf(10), BigInteger.ZERO, null);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(BigInteger.ZERO, null);
        integerDecoded.decode(dis);

        assertEquals(BigInteger.valueOf(10), integerDecoded.value);
    }

    @Test
    public void testEncodeAndDecodeSingedInt7() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(-10,-128,127);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(-128,127);
        integerDecoded.decode(dis);

        assertEquals(-10, integerDecoded.getValueAsLong());
        assertEquals(-128, integerDecoded.getLowerBoundAsLong());
        assertEquals(127, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeSingedInt15() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(-10,-32767,32767);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(-32767, 32767);
        integerDecoded.decode(dis);

        assertEquals(-10, integerDecoded.getValueAsLong());
        assertEquals(-32767, integerDecoded.getLowerBoundAsLong());
        assertEquals(32767, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeSingedInt31() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(-2147483647,-2147483648,2147483647);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(-2147483648, 2147483647);
        integerDecoded.decode(dis);

        assertEquals(-2147483647, integerDecoded.getValueAsLong());
        assertEquals(-2147483648, integerDecoded.getLowerBoundAsLong());
        assertEquals(2147483647, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeSingedInt63() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(-10,-9223372036854775808L,9223372036854775807L);
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(-9223372036854775808L, 9223372036854775807L);
        integerDecoded.decode(dis);

        assertEquals(-10,integerDecoded.getValueAsLong());
        assertEquals(-9223372036854775808L, integerDecoded.getLowerBoundAsLong());
        assertEquals(9223372036854775807L, integerDecoded.getUpperBoundAsLong());
    }

    @Test
    public void testEncodeAndDecodeSingedIntOutOfBounds() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        COERInteger integer = new COERInteger(BigInteger.valueOf(-10), new BigInteger("-9223372036854775807"), new BigInteger("9223372036854775808"));
        integer.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        COERInteger integerDecoded = new COERInteger(new BigInteger("-9223372036854775807"), new BigInteger("9223372036854775808"));
        integerDecoded.decode(dis);

        assertEquals(-10,integerDecoded.getValueAsLong());
        assertEquals(new BigInteger("9223372036854775808"), integerDecoded.upperBound);
        assertEquals(new BigInteger("-9223372036854775807"), integerDecoded.lowerBound);
    }

}