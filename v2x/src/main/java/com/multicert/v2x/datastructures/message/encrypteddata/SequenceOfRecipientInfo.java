package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfRecipientInfo extends COERSequenceOf
{
	/**
	 * Constructor used when encoding
	 */
	public SequenceOfRecipientInfo(RecipientInfo[] values)
    {
		super(values);
	}

	/**
	 * Constructor used when decoding
	 */
	public SequenceOfRecipientInfo()
    {
		super(new RecipientInfo());
	}

	@Override
	public String toString() {

		String retval = "SequenceOfRecipientInfo [";
		if(values != null){
			for(int i=0; i< values.length -1;i++){
				retval += values[i].toString().replace("RecipientInfo ", "") + ",";
			}
			if(values.length > 0){
				retval += values[values.length-1].toString().replace("RecipientInfo ", "");
			}
		}
		retval += "]";
		return retval;
	}
}
