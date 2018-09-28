package com.multicert.v2x.datastructures.certificaterequests.Enrollment;

import com.multicert.v2x.asn1.coer.COEREnumeration;
import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class encodes the InnerEcResponse of the enrollment certificate response. It contains:
 * requestHash - Is the left-most 16 octets of the SHA256 digest of the EtsiTs103097Data-Signed structure received in the request, REQUIRED;
 * responseCode - Indicates the result of the request, REQUIRED
 * enrollmentCredential - The enrollment credential (only included if the response code is set to OK), OPTIONAL
 */
public class InnerEcResponse extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 3;


    protected static final int REQUEST_HASH = 0;
    protected static final int RESPONSE_CODE = 1;
    protected static final int CERTIFICATE = 2;


    /**
     * Constructor used for encoding  (populates a sequence)
     */
    public InnerEcResponse(COEROctetString requestHash, EnrollmentResonseCode responseCode, EtsiTs103097Certificate enrollmentCredential) throws IOException
    {
        super(SEQUENCE_SIZE);
        if(responseCode.equals(EnrollmentResonseCode.OK) && enrollmentCredential == null)
        {
            throw new IOException("Error generating inner enrollment credential response: Request code is OK, but no certificate is present");
        }
        if(!responseCode.equals(EnrollmentResonseCode.OK) && enrollmentCredential != null)
        {
            throw new IOException("Error generating inner enrollment credential response: Request code is indicates error, but certificate is present");
        }
        createSequence();
        setComponentValue(REQUEST_HASH, requestHash);
        setComponentValue(RESPONSE_CODE, new COEREnumeration(responseCode));
        setComponentValue(CERTIFICATE, enrollmentCredential);

    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public InnerEcResponse() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }

    private void createSequence() throws IOException
    {
        addComponent(REQUEST_HASH, false, new COEROctetString(16,16), null);
        addComponent(RESPONSE_CODE,false, new COEREnumeration(EnrollmentResonseCode.class), null);
        addComponent(CERTIFICATE, true, new EtsiTs103097Certificate(), null);

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

    public COEROctetString getRequestHash()
    {
        return (COEROctetString) getComponentValue(REQUEST_HASH);
    }

    public EnrollmentResonseCode getResponseCode()
    {
        return (EnrollmentResonseCode) ((COEREnumeration) getComponentValue(RESPONSE_CODE)).getValue();
    }

    public EtsiTs103097Certificate getCertificate()
    {
        return (EtsiTs103097Certificate) getComponentValue(CERTIFICATE);
    }


    @Override
    public String toString() {
        return
                "innerEcResponse [\n" +
                        "  requestHash=" + getRequestHash() + "\n" +
                        "  responseCode=" + getResponseCode()+ "\n" +
                        "  enrollmentCredential=" + ( getCertificate() != null ? getCertificate().toString().replaceAll("Certificate ", "") : "NONE") + "\n" +
                        "]";
    }
}
