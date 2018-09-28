package com.multicert.v2x.datastructures.message.secureddata;

import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.Uint8;


import java.io.*;


/**
 * This class represents the Etsi ts 103 097 secured data format, more information present in the ETSI TS 103 096 standard Section 5
 */
public class EtsiTs103097Data extends COERSequence
{
	
	public static final int SEQUENCE_SIZE = 2;
    public static final int CURRENT_VERSION = 3;
	
	private static final int PROTOCOLVERSION = 0;
	private static final int CONTENT = 1;

    /**
     * Constructor used when encoding
     */
    public EtsiTs103097Data(int protocolVersion, EtsiTs103097Content content) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(PROTOCOLVERSION, new Uint8(protocolVersion));
        setComponentValue(CONTENT, content);
    }

	/**
	 * Constructor used when encoding using default protocol version (3)
	 */
	public EtsiTs103097Data(EtsiTs103097Content content) throws IOException
	{
		this(CURRENT_VERSION, content);
	}

	/**
	 * Constructor used when decoding
	 */
	public EtsiTs103097Data(){
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * Constructor decoding a EtsiTs103097Data from an encoded byte array.
	 * @param encodedData the encoded EtsiTs103097Data
	 * @throws IOException   if communication problems occurred during serialization.
	 */
	public EtsiTs103097Data(byte[] encodedData) throws IOException{
		super(SEQUENCE_SIZE);
		createSequence();
		
		DataInputStream dis = new DataInputStream(new  ByteArrayInputStream(encodedData));
		decode(dis);
	}

	/**
	 * Encodes the EtsiTs103097Data as a byte array to be used for signing.
	 *
	 */
	public byte[] getEncoded() throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		encode(dos);
		return baos.toByteArray();		
	}
	
	private void createSequence(){
		addComponent(PROTOCOLVERSION, false, new Uint8(), null);
		addComponent(CONTENT, false, new EtsiTs103097Content(), null);
	}

    /**
     *
     * @return protocolVersion
     */
    public int getProtocolVersion(){
        return (int) ((Uint8) getComponentValue(PROTOCOLVERSION)).getValueAsLong();
    }

    /**
     *
     * @return content
     */
    public EtsiTs103097Content getContent(){
        return (EtsiTs103097Content) getComponentValue(CONTENT);
    }

	@Override
	public String toString() {
		return "EtsiTs103097Data [\n" +
				"  protocolVersion=" + getProtocolVersion() + ",\n" +
				"  content=" + getContent().toString().replace("EtsiTs103097Content ", "").replaceAll("\n", "\n  ")  +
				"\n]";
	}

}
