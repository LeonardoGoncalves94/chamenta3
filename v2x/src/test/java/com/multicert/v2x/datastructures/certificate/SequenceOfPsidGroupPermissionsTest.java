package com.multicert.v2x.datastructures.certificate;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SequenceOfPsidGroupPermissionsTest
{
    @Test
    public void testEncodeAndDecode() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        SubjectPermissions subjectPermissions = new SubjectPermissions(SubjectPermissions.SubjectPermissionsTypes.ALL, null);
        PsidGroupPermissions psidGroupPermissions =  new PsidGroupPermissions(subjectPermissions, 3, -1, new EndEntityType(true, true));
        PsidGroupPermissions psidGroupPermissions2 =  new PsidGroupPermissions(subjectPermissions, 2, -1, new EndEntityType(false, true));

        SequenceOfPsidGroupPermissions certIssuePermissions = new SequenceOfPsidGroupPermissions(new PsidGroupPermissions[] {psidGroupPermissions, psidGroupPermissions2});
        certIssuePermissions.encode(dos);


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        SequenceOfPsidGroupPermissions decodedCertIssuerPermissions = new SequenceOfPsidGroupPermissions();
        decodedCertIssuerPermissions.decode(dis);

        System.out.println(certIssuePermissions.toString());

        assertEquals(certIssuePermissions.toString(),decodedCertIssuerPermissions.toString());

    }
}