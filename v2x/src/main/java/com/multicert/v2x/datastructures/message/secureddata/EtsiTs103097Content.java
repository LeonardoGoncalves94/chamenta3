package com.multicert.v2x.datastructures.message.secureddata;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.datastructures.base.Opaque;
import com.multicert.v2x.datastructures.message.encrypteddata.EncryptedData;

import java.io.IOException;

/**
 * This class specifies the types of secured data to be transferred. Consult ETSI TS 103 097 standard  to see how it maps to EtsiTs103097 Data
 *
 * unsecuredData - Indicates that the content is an OCTET STRING to be consumed outside the SDS.
 * signedData - Indicates that the content has been signed according to this standard.
 * encryptedData - Indicates that the content has been encrypted according to this standard.
 **/
public class EtsiTs103097Content extends COERChoice
{

	
	public enum EtsiTs103097ContentChoices implements COERChoiceEnumeration
    {
		UNSECURED_DATA,
		SIGNED_DATA,
		ENCRYPTED_DATA;

		public int myOrdinal()
        {
            return this.ordinal();
        }

		@Override
		public COEREncodable getEncodableType() throws IOException
		{
			switch(this){
				case SIGNED_DATA:
					return new SignedData();
				case ENCRYPTED_DATA:
					return new EncryptedData();
				case UNSECURED_DATA:
				default:
					return new Opaque();
			}
		}
	}
	
	/**
	 * Constructor used when encoding the types UNSECURED_DATA
	 */
	public EtsiTs103097Content(EtsiTs103097ContentChoices type, Opaque data) throws IllegalArgumentException{
		super(type, data);
	}
	
	/**
	 * Constructor used when encoding the type SIGNED_DATA
	 */
	public EtsiTs103097Content(SignedData data) throws IllegalArgumentException{
		super(EtsiTs103097ContentChoices.SIGNED_DATA, data);
	}

	/**
	 * Constructor used when encoding the type ENCRYPTED_DATA
	 */
	public EtsiTs103097Content(EncryptedData data) throws IllegalArgumentException{
		super(EtsiTs103097ContentChoices.ENCRYPTED_DATA, data);
	}

	/**
	 * Constructor used when decoding.
	 */
	public EtsiTs103097Content() {
		super(EtsiTs103097ContentChoices.class);
	}
		
	/**
	 * Returns the type.
	 */
	public EtsiTs103097ContentChoices getType(){
		return (EtsiTs103097ContentChoices) choice;
	}

	@Override
	public String toString() {
		return "EtsiTs103097Content [\n  " + choice + "=" + value.toString().replace("Opaque ", "").replace("SignedData ", "").replace("EncryptedData ", "").replaceAll("\n", "\n  ")+ "\n]";
	}
	
}
