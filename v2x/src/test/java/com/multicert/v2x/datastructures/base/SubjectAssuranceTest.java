package com.multicert.v2x.datastructures.base;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SubjectAssuranceTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        SubjectAssurance subjectAssurance = new SubjectAssurance(7,3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        subjectAssurance.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SubjectAssurance subjectAssuranceDecoded = new SubjectAssurance();
        subjectAssuranceDecoded.decode(dis);

        assertEquals(subjectAssurance.toString(), subjectAssuranceDecoded.toString());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() throws IOException
    {
        SubjectAssurance subjectAssurance = new SubjectAssurance(20,30);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        subjectAssurance.encode(daos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SubjectAssurance subjectAssuranceDecoded = new SubjectAssurance();
        subjectAssuranceDecoded.decode(dis);
    }
}