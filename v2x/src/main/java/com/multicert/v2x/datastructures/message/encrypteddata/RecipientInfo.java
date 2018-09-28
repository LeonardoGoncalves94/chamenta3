package com.multicert.v2x.datastructures.message.encrypteddata;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;

/**
 * This data structure is used to transfer the data encryption key to an individual recipient of an EncryptedData
 *
 */
public class RecipientInfo extends COERChoice
{

	
	public enum RecipientInfoChoices implements COERChoiceEnumeration
	{
        PSK_RECIP_INFO,
        SYMM_RECIP_INFO,
        CERT_RECIP_INFO,
        SIGNED_DATA_RECIP_INFO,
        REK_RECIP_INFO;

		public int myOrdinal()
		{
			return this.ordinal();
		}

		@Override
		public COEREncodable getEncodableType()
		{
			switch(this)
			{
				case PSK_RECIP_INFO:
					return new PreSharedKeyRecipientInfo();
                case SYMM_RECIP_INFO:
                    return new SymmRecipientInfo();
                default:
                    return new PKRecipientInfo();
			}
		}
	}
	
	/**
	 * Constructor used when encoding of type pskRecipInfo
	 */
	public RecipientInfo(PreSharedKeyRecipientInfo keyInfo) throws IllegalArgumentException{
		super(RecipientInfoChoices.PSK_RECIP_INFO, keyInfo);
	}
	
	/**
	 * Constructor used when encoding of type symmRecipInfo
	 */
	public RecipientInfo(SymmRecipientInfo keyInfo) throws IllegalArgumentException{
		super(RecipientInfoChoices.SYMM_RECIP_INFO, keyInfo);
	}
	
	/**
	 * Constructor used when encoding of types certRecipInfo, signedDataRecipInfo, rekRecipInfo
	 */
	public RecipientInfo(RecipientInfoChoices type, PKRecipientInfo keyInfo) throws IllegalArgumentException{
		super(type, keyInfo);
	}
	

	/**
	 * Constructor used when decoding.
	 */
	public RecipientInfo() {
		super(RecipientInfoChoices.class);
	}
		
	/**
	 * Returns the type of key id.
	 */
	public RecipientInfoChoices getType(){
		return (RecipientInfoChoices) choice;
	}

	@Override
	public String toString() {
		return "RecipientInfo [" + choice + "=" + value.toString().replace("PreSharedKeyRecipientInfo ", "").replace("SymmRecipientInfo ", "").replace("PKRecipientInfo ", "") +"]";
	}
	
}
