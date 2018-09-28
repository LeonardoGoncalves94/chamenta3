package com.multicert.v2x.datastructures.certificaterequests;

import com.multicert.v2x.asn1.coer.COERInteger;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Content;

import java.io.*;

public class EtsiTs102941Data extends COERSequence
{
    private static final int CURRENT_VERSION = 1;
    private static final int SEQUENCE_SIZE = 2;
    private static final int VERSION = 0;
    private static final int CONTENT = 1;

    /**
     * Constructor used when encoding a EtsiTs102941Data structure with default version (v1)
     */
    public EtsiTs102941Data(EtsiTs102941DataContent dataContent) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(VERSION, new COERInteger(CURRENT_VERSION));
        setComponentValue(CONTENT, dataContent);
    }

    /**
     * Constructor used when encoding
     */
    public EtsiTs102941Data(int version, EtsiTs102941DataContent dataContent) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(VERSION, new COERInteger(version));
        setComponentValue(CONTENT, dataContent);
    }

    /**
     * Constructor used when decoding
     */
    public EtsiTs102941Data()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

    /**
     * Constructor used when decoding an EtsiTs102941Data given the encoded value
     * @param encoded
     * @throws IOException
     */
    public EtsiTs102941Data(byte[] encoded) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }

    /**
     *
     * @return content
     */
    public EtsiTs102941DataContent getContent(){
        return (EtsiTs102941DataContent) getComponentValue(CONTENT);
    }


    public void createSequence()
    {
        addComponent(VERSION, false, new COERInteger(), null);
        addComponent(CONTENT, false, new EtsiTs102941DataContent(), null);
    }
}
