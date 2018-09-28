package com.multicert.v2x.datastructures.certificate;


import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COERSequenceOf;

import java.io.*;
import java.util.List;

public class SequenceOfCertificate extends COERSequenceOf
{
	/**
	 * Constructor used when decoding
	 */
	public SequenceOfCertificate() throws IOException
	{
		super(new EtsiTs103097Certificate());
	}
	
	/**
	 * Constructor used when encoding
	 */
	public SequenceOfCertificate(EtsiTs103097Certificate[] values)
	{
		super(values);
	}

	/**
	 * Constructor used when encoding
	 */
	public SequenceOfCertificate(List<EtsiTs103097Certificate> sequenceValues){
		super((EtsiTs103097Certificate[]) sequenceValues.toArray(new EtsiTs103097Certificate[sequenceValues.size()]));
	}

    /**
     * Method used for encoding a sequence of certificates
     *
     * @return the encoded SequenceOfCertificate
     */
    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

    /**
     * Constructor used for decoding a sequence of certificates (triggers the decoding of all certificate fields)
     *
     * @param encoded The encoded certificate base
     */
    public SequenceOfCertificate(byte[] encoded) throws IOException
    {
        super(new EtsiTs103097Certificate());
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encoded));
        decode(dis);
    }

    /**
     * Method that returns the certificates
     * @return
     */
    public EtsiTs103097Certificate[] getCerts()
    {
        COEREncodable[] certArray = getValues();
        int size = certArray.length;

        EtsiTs103097Certificate[] retval = new EtsiTs103097Certificate[size];

        for(int i = 0; i < size; i++)
        {
            retval [i] = (EtsiTs103097Certificate)certArray[i];
        }

        return retval;
    }
}
