package com.multicert.v2x.datastructures.certificaterequests.Authorization;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.certificaterequests.EcSignature;
import com.multicert.v2x.datastructures.certificaterequests.PublicKeys;

import java.io.*;

public class InnerAtRequest extends COERSequence
{
    private static final int OCTETSTRING_SIZE = 3;

    protected static final int SEQUENCE_SIZE = 4;

    protected static final int PUBLIC_KEYS = 0;
    protected static final int HMAC_KEY = 1;
    protected static final int SHARED_AT_REQUEST = 2;
    protected static final int EC_SIGNATURE = 3;



    /**
     * Constructor used for encoding  using default current version (populates a sequence)
     */
    public InnerAtRequest(PublicKeys publicKeys, byte[] hmacKey, SharedAtRequest sharedAtRequest, EcSignature ecSignature) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(PUBLIC_KEYS, publicKeys);
        setComponentValue(HMAC_KEY, new COEROctetString(hmacKey, OCTETSTRING_SIZE,OCTETSTRING_SIZE));
        setComponentValue(SHARED_AT_REQUEST, sharedAtRequest);
        setComponentValue(EC_SIGNATURE, ecSignature);



    }

    /**
     * Constructor used for decoding (creates an empty sequence)
     */
    public InnerAtRequest() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

    }


    public InnerAtRequest(byte[] encoded) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }



    private void createSequence() throws IOException
    {
        addComponent(PUBLIC_KEYS, false, new PublicKeys(), null);
        addComponent(HMAC_KEY, false, new COEROctetString(OCTETSTRING_SIZE, OCTETSTRING_SIZE), null);
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


    public PublicKeys getPublicKeys()
    {
        return (PublicKeys) getComponentValue(PUBLIC_KEYS);
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
                        "  publicKeys=" + getPublicKeys()+ "\n" +
                        "  sharedAtRequest=" + getSharedAtRequest()+ "\n" +
                        "  ecSignature=" + getEcSignature()+ "\n" +
                        "]";
    }
}
