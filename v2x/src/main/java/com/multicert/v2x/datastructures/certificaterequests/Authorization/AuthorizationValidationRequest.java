package com.multicert.v2x.datastructures.certificaterequests.Authorization;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.certificaterequests.EcSignature;
import com.multicert.v2x.datastructures.certificaterequests.PublicKeys;

import java.io.*;

public class AuthorizationValidationRequest extends COERSequence
{

    protected static final int SEQUENCE_SIZE = 2;

    protected static final int SHARED_AT_REQUEST = 0;
    protected static final int EC_SIGNATURE = 1;



    /**
     * Constructor used for encoding
     */
    public AuthorizationValidationRequest(SharedAtRequest sharedAtRequest, EcSignature ecSignature) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(SHARED_AT_REQUEST, sharedAtRequest);
        setComponentValue(EC_SIGNATURE, ecSignature);

    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public AuthorizationValidationRequest() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }


    public AuthorizationValidationRequest(byte[] encoded) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }



    private void createSequence() throws IOException
    {
        addComponent(SHARED_AT_REQUEST, false, new SharedAtRequest(), null);
        addComponent(EC_SIGNATURE, false, new EcSignature(), null);


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


    public SharedAtRequest getSharedAtRequest()
    {
        return (SharedAtRequest) getComponentValue(SHARED_AT_REQUEST);
    }

    public EcSignature getEcSignature()
    {
        return (EcSignature) getComponentValue(EC_SIGNATURE);
    }

    @Override
    public String toString() {
        return
                "innerEcRequest [\n" +
                        "  sharedAtRequest=" + getSharedAtRequest()+ "\n" +
                        "  ecSignature=" + getEcSignature()+ "\n" +
                        "]";
    }
}
