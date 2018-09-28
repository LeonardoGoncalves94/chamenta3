package com.multicert.v2x.datastructures.certificaterequests.Authorization;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.certificaterequests.CertificateFormat;

import java.io.*;

public class SharedAtRequest extends COERSequence
{
    private static final int OCTETSTRING_SIZE = 3;

    protected static final int SEQUENCE_SIZE = 4;

    protected static final int CURRENT_VERSION = 1; // In the present standard, the certificate format shall be set to ts103097v131 (integer value 1)

    protected static final int EA_ID = 0;
    protected static final int KEY_TAG = 1;
    protected static final int CERTIFICATE_FORMAT = 2;
    protected static final int REQUESTED_SUBJECT_ATTRIBUTES = 3;




    /**
     * Constructor used for encoding  using default current version (populates a sequence)
     */
    public SharedAtRequest(HashedId8 eaId, byte[] keyTag, CertificateSubjectAttributes requestedSubectAttributes) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(EA_ID, eaId);
        setComponentValue(KEY_TAG, new COEROctetString(keyTag, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
        setComponentValue(CERTIFICATE_FORMAT, new CertificateFormat(CURRENT_VERSION));
        setComponentValue(REQUESTED_SUBJECT_ATTRIBUTES, requestedSubectAttributes);
    }

    /**
     * Constructor used for encoding
     */
    public SharedAtRequest(HashedId8 eaId, byte[] keyTag, int version, CertificateSubjectAttributes requestedSubectAttributes) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(EA_ID, eaId);
        setComponentValue(KEY_TAG, new COEROctetString(keyTag, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
        setComponentValue(CERTIFICATE_FORMAT, new CertificateFormat(version));
        setComponentValue(REQUESTED_SUBJECT_ATTRIBUTES, requestedSubectAttributes);
    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public SharedAtRequest() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }


    public SharedAtRequest(byte[] encoded) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }



    private void createSequence() throws IOException
    {
        addComponent(EA_ID, false, new HashedId8(), null);
        addComponent(KEY_TAG, false, new COEROctetString(OCTETSTRING_SIZE,OCTETSTRING_SIZE), null);
        addComponent(CERTIFICATE_FORMAT, false, new CertificateFormat(), null);
        addComponent(REQUESTED_SUBJECT_ATTRIBUTES, false, new CertificateSubjectAttributes(), null);
    }

    /**
     * Encodes the InnerEcRequest as a byte array, to be signed.
     *
     * @return return encoded version of the InnerEcRequest as a byte[]
     * @throws IOException if encoding problems of the data occurred.
     */
    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

    public byte[] getEaId()
    {
         HashedId8 hashedId = (HashedId8) getComponentValue(EA_ID);
         return hashedId.getHashedId();
    }

    public CertificateSubjectAttributes getSubjectAttributes()
    {
        return (CertificateSubjectAttributes) getComponentValue(REQUESTED_SUBJECT_ATTRIBUTES);
    }

    @Override
    public String toString() {
        return
                "innerEcRequest [\n" +
                        "]";
    }
}
