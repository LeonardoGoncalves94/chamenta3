package com.multicert.v2x.datastructures.certificaterequests.Enrollment;

import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.CertificateId;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.CertificateSubjectAttributes;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.*;

public class CertificateSubjectAttributesTest
{
    @Test
    public void testEncodeAndDecodeRoot() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        CertificateId certificateId = new CertificateId(new Hostname("someId"));
        SubjectAssurance subjectAssurance = new SubjectAssurance(7,3);
        PsidSsp[] appPermissionsValues = new PsidSsp[1];
        appPermissionsValues[0] = new PsidSsp(new Psid(6), null); // Insert proper app permissions here.
        SequenceOfPsidSsp appPermissions = new SequenceOfPsidSsp(appPermissionsValues);

        CertificateSubjectAttributes subjectAttributes = new CertificateSubjectAttributes(certificateId,null,null,subjectAssurance,appPermissions,null);
        subjectAttributes.encode(dos);

        System.out.println(subjectAttributes.toString());


        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        CertificateSubjectAttributes decodedSubjectAttributes = new CertificateSubjectAttributes();
        decodedSubjectAttributes.decode(dis);

        assertEquals(subjectAttributes.toString(),decodedSubjectAttributes.toString());

    }
}