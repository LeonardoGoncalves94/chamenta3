package com.multicert.v2x.datastructures.message.secureddata;


import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.*;

public class ToBeSignedData extends COERSequence
{

	private static final int SEQUENCE_SIZE = 2;

	private static final int PAYLOAD = 0;
	private static final int HEADERINFO = 1;


	/**
	 * Constructor used when encoding
	 */
	public ToBeSignedData(SignedDataPayload payload, HeaderInfo headerInfo) throws IOException
    {
		super(SEQUENCE_SIZE);
		createSequence();
		setComponentValue(PAYLOAD, payload);
		setComponentValue(HEADERINFO, headerInfo);
	}

    /**
     * Encodes the ToBeSignedData as a byte array, to be used in the signature.
     *
     * @return return encoded version of the ToBeSignedData as a byte[]
     * @throws IOException if encoding problems of the data occurred.
     */
    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

	/**
	 * Constructor used when decoding
	 */
	public ToBeSignedData() throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
	}
	
	/**
	 * Constructor decoding a ToBeSignedData from an encoded byte array.
	 */
	public ToBeSignedData(byte[] encodedData) throws IOException{

		super(SEQUENCE_SIZE);
		createSequence();
		
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encodedData));
		decode(dis);
	}

	/**
	 *
	 * @return payload
	 */
	public SignedDataPayload getPayload()
	{
		return (SignedDataPayload) getComponentValue(PAYLOAD);
	}
	
	/**
	 * 
	 * @return headerInfo
	 */
	public HeaderInfo getHeaderInfo()
	{
		return (HeaderInfo) getComponentValue(HEADERINFO);
	}

	
	private void createSequence() throws IOException
	{
		addComponent(PAYLOAD, false, new SignedDataPayload(), null);
		addComponent(HEADERINFO, false, new HeaderInfo(), null);
	}

	
}
