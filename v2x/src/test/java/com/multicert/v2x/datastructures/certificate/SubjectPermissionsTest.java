package com.multicert.v2x.datastructures.certificate;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SubjectPermissionsTest
{

    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        SubjectPermissions subjectPermissions = new SubjectPermissions(SubjectPermissions.SubjectPermissionsTypes.ALL, null);
        subjectPermissions.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SubjectPermissions decodedSubjectPermissions = new SubjectPermissions();
        decodedSubjectPermissions.decode(dis);

        assertEquals(subjectPermissions.toString(), decodedSubjectPermissions.toString());

    }

}