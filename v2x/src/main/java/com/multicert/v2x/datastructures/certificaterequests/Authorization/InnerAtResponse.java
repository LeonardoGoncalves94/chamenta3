package com.multicert.v2x.datastructures.certificaterequests.Authorization;

import com.multicert.v2x.asn1.coer.COEREnumeration;
import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.EnrollmentResonseCode;

import java.io.*;

public class InnerAtResponse extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 3;


    protected static final int REQUEST_HASH = 0;
    protected static final int RESPONSE_CODE = 1;
    protected static final int CERTIFICATE = 2;


    /**
     * Constructor used for encoding  (populates a sequence)
     */
    public InnerAtResponse(COEROctetString requestHash, AuthorizationResponseCode responseCode, EtsiTs103097Certificate authorizationTicket) throws IOException
    {
        super(SEQUENCE_SIZE);
        if(responseCode.equals(AuthorizationResponseCode.OK) && authorizationTicket == null)
        {
            throw new IOException("Error generating inner enrollment credential response: Request code is OK, but no certificate is present");
        }
        if(!(responseCode.equals(AuthorizationResponseCode.OK)) && authorizationTicket != null)
        {
            throw new IOException("Error generating inner enrollment credential response: Request code is "+ responseCode +", but certificate is present");
        }
        createSequence();
        setComponentValue(REQUEST_HASH, requestHash);
        setComponentValue(RESPONSE_CODE, new COEREnumeration(responseCode));
        setComponentValue(CERTIFICATE, authorizationTicket);

    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public InnerAtResponse() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }

    private void createSequence() throws IOException
    {
        addComponent(REQUEST_HASH, false, new COEROctetString(16,16), null);
        addComponent(RESPONSE_CODE,false, new COEREnumeration(AuthorizationResponseCode.class), null);
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

    public AuthorizationResponseCode getResponseCode()
    {
        return (AuthorizationResponseCode) ((COEREnumeration) getComponentValue(RESPONSE_CODE)).getValue();
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
