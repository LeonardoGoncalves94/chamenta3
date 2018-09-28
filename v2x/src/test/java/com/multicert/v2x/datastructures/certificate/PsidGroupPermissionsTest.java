package com.multicert.v2x.datastructures.certificate;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class PsidGroupPermissionsTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        SubjectPermissions subjectPermissions = new SubjectPermissions(SubjectPermissions.SubjectPermissionsTypes.ALL, null);
        PsidGroupPermissions psidGroupPermissions =  new PsidGroupPermissions(subjectPermissions, 3, -1, new EndEntityType(true, true));
        psidGroupPermissions.encode(dos);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        PsidGroupPermissions decodedPsidGroupPermissions =  new PsidGroupPermissions();
        decodedPsidGroupPermissions.decode(dis);

        System.out.println(psidGroupPermissions.toString());

        assertEquals(psidGroupPermissions.toString(),decodedPsidGroupPermissions.toString());

    }
}