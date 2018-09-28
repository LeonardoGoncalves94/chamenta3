package com.multicert.v2x.datastructures.certificaterequests.Enrollment;

import com.multicert.v2x.asn1.coer.COERIA5String;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.CertificateSubjectAttributes;
import com.multicert.v2x.datastructures.certificaterequests.CertificateFormat;
import com.multicert.v2x.datastructures.certificaterequests.PublicKeys;

import java.io.*;

/**
 * This class encodes the InnerEcRequest of the enrollment certificate request. It contains:
 * itsId - The identifier of the requesting ITS-S. This should be set to the ITS-S' canonical identifier in case of the initial certificate request;
 * or the the HashedID8 of the current valid Enrolment Credential in the case or re-enrollment;
 * certificateFormat - which specifies the version used for the certificate format specification. (integer value 1 in the present document);
 * publicKeys - containing the the enrollment certificate verification public key;
 * requestedSubectAttributes - containing the desired subject attributes (the attribute certIssuepermissions must be always ABSENT);
 */
public class InnerEcRequest extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 4;

    protected static final int CURRENT_VERSION = 1; // In the present standard, the certificate format shall be set to ts103097v131 (integer value 1)
    protected static final int itsIDSize = 9; // placeholder for the size of the ITS identifier

    protected static final int ITS_ID = 0;
    protected static final int CERTIFICATE_FORMAT = 1;
    protected static final int PUBLIC_KEYS = 2;
    protected static final int REQUESTED_SUBJECT_ATTRIBUTES = 3;


    /**
     * Constructor used for encoding  using default current version (populates a sequence)
     */
    public InnerEcRequest(String itsID, PublicKeys publicKeys, CertificateSubjectAttributes requestedSubectAttributes) throws IOException
    {
        super(SEQUENCE_SIZE);

        if(itsID.length() != itsIDSize)
        {
            throw new IllegalArgumentException("Error encoding enrollment certificate request: invalid ITS ID size. String should be "+ itsIDSize + "chars long");
        }
        createSequence();
        setComponentValue(ITS_ID, new COERIA5String(itsID, itsIDSize));
        setComponentValue(CERTIFICATE_FORMAT, new CertificateFormat(CURRENT_VERSION));
        setComponentValue(PUBLIC_KEYS, publicKeys);
        setComponentValue(REQUESTED_SUBJECT_ATTRIBUTES, requestedSubectAttributes);

    }

    /**
     * Constructor used for encoding
     */
    public InnerEcRequest(String itsID, int version ,PublicKeys publicKeys, CertificateSubjectAttributes requestedSubectAttributes) throws IOException
    {
        super(SEQUENCE_SIZE);

        if(itsID.length() != itsIDSize)
        {
            throw new IllegalArgumentException("Error encoding enrollment certificate request: invalid ITS ID size. String should be "+ itsIDSize + "chars long");
        }
        createSequence();
        setComponentValue(ITS_ID, new COERIA5String(itsID, itsIDSize));
        setComponentValue(CERTIFICATE_FORMAT, new CertificateFormat(version));
        setComponentValue(PUBLIC_KEYS, publicKeys);
        setComponentValue(REQUESTED_SUBJECT_ATTRIBUTES, requestedSubectAttributes);

    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public InnerEcRequest() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }


    public InnerEcRequest(byte[] encoded) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }



    private void createSequence() throws IOException
    {
        addComponent(ITS_ID, false, new COERIA5String(itsIDSize), null);
        addComponent(CERTIFICATE_FORMAT,false, new CertificateFormat(), null);
        addComponent(PUBLIC_KEYS, false, new PublicKeys(), null);
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
    
    public CertificateFormat getCertificateFormat()
    {
        return (CertificateFormat) getComponentValue(CERTIFICATE_FORMAT);
    }

    public PublicKeys getPublicKeys()
    {
        return (PublicKeys) getComponentValue(PUBLIC_KEYS);
    }

    public CertificateSubjectAttributes getRequestedSubjectAttributes()
    {
        return (CertificateSubjectAttributes) getComponentValue(REQUESTED_SUBJECT_ATTRIBUTES);
    }

    public String getItsId()
    {
         COERIA5String ia5String = (COERIA5String) getComponentValue(ITS_ID);
         return ia5String.getString();
    }

    @Override
    public String toString() {
        return
                "innerEcRequest [\n" +
                        "  id=" + getItsId() + "\n" +
                        "  certificateFormat=" + getCertificateFormat()+ "\n" +
                        "  publicKeys=" + getPublicKeys()+ "\n" +
                        "  requestedSubjectAttributes=" + getRequestedSubjectAttributes()+ "\n" +
                        "]";
    }
}
