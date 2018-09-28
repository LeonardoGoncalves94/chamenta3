package com.multicert.v2x.datastructures.message.secureddata;


import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class SignedDataPayload extends COERSequence
{
	private static final int SEQUENCE_SIZE = 2;
	
	private static final int DATA = 0;
	private static final int EXTDATAHASH = 1;

	/**
	 * Constructor used when encoding
	 */
	public SignedDataPayload(EtsiTs103097Data data, HashedData extDataHash) throws IllegalArgumentException, IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
		if(data == null && extDataHash == null){
			throw new IllegalArgumentException("Error i SignedDataPayload one of data or extDataHash must be set.");
		}
		setComponentValue(DATA, data);
		setComponentValue(EXTDATAHASH, extDataHash);
	}

	/**
	 * Constructor used when decoding
	 */
	public SignedDataPayload(){
		super(SEQUENCE_SIZE);
		createSequence();
	}
	

	/**
	 * 
	 * @return data
	 */
	public EtsiTs103097Data getData(){
		return (EtsiTs103097Data) getComponentValue(DATA);
	}
	
	/**
	 * 
	 * @return extDataHash
	 */
	public HashedData getExtDataHash(){
		return (HashedData) getComponentValue(EXTDATAHASH);
	}
	
	private void createSequence(){
		addComponent(DATA, true, new EtsiTs103097Data(), null);
		addComponent(EXTDATAHASH, true, new HashedData(), null);
	}

	@Override
	public String toString() {
		return "SignedDataPayload [\n" +
				(getData() != null ? "  data=" + getData().toString().replace("EtsiTs103097Data ", "").replaceAll("\n", "\n  ")  + (getExtDataHash() != null ? ",\n" : "") : "") +
				(getExtDataHash() != null ? "  extDataHash=" + getExtDataHash().toString().replace("HashedData ", "").replaceAll("\n", "\n  ")   : "") +
				"\n]";
	}
	
}
