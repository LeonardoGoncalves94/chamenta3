package com.multicert.v2x.datastructures.message.secureddata;


import com.multicert.v2x.asn1.coer.COEREnumeration;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.Signature;

import java.io.IOException;

public class SignedData extends COERSequence
{
	private static final int SEQUENCE_SIZE = 4;
	
	private static final int HASHID = 0;
	private static final int TBSDATA = 1;
	private static final int SIGNER = 2;
	private static final int SIGNATURE = 3;


	/**
	 * Constructor used when encoding
	 */
	public SignedData(HashAlgorithm hashAlgorithm, ToBeSignedData tbsData, SignerIdentifier signer, Signature signature) throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();

		if(hashAlgorithm == null)
		{
			throw new IllegalArgumentException("Error argument hashAlgorithm cannot be null for SignedData.");
		}
		setComponentValue(HASHID, new COEREnumeration(hashAlgorithm));
		setComponentValue(TBSDATA, tbsData);
		setComponentValue(SIGNER, signer);
		setComponentValue(SIGNATURE, signature);
	}

	/**
	 * Constructor used when decoding
	 */
	public SignedData() throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * 
	 * @return hashAlgorithm
	 */
	public HashAlgorithm getHashAlgorithm()
    {
		return (HashAlgorithm) ((COEREnumeration) getComponentValue(HASHID)).getValue();
	}
	
	/**
	 * 
	 * @return tbsData
	 */
	public ToBeSignedData getTbsData()
    {
		return (ToBeSignedData) getComponentValue(TBSDATA);
	}
	
	/**
	 * 
	 * @return signer
	 */
	public SignerIdentifier getSigner()
    {
		return (SignerIdentifier) getComponentValue(SIGNER);
	}
	
	/**
	 * 
	 * @return signature
	 */
	public Signature getSignature()
    {
		return (Signature) getComponentValue(SIGNATURE);
	}
	
	private void createSequence() throws IOException
	{
		addComponent(HASHID, false, new COEREnumeration(HashAlgorithm.class), null);
		addComponent(TBSDATA, false, new ToBeSignedData(), null);
		addComponent(SIGNER, false, new SignerIdentifier(), null);
		addComponent(SIGNATURE, false, new Signature(), null);
	}
}
