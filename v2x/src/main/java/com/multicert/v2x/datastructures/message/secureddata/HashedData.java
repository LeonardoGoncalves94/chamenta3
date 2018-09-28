package com.multicert.v2x.datastructures.message.secureddata;

import com.multicert.v2x.asn1.coer.*;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import org.bouncycastle.util.encoders.Hex;


public class HashedData extends COERChoice
{

	public enum HashedDataTypes implements COERChoiceEnumeration
	{
		SHA256_HASHED_DATA;

		public int myOrdinal()
		{
			return this.ordinal();
		}

		@Override
		public COEREncodable getEncodableType()
		{
			return new COEROctetString(32,32);
		}
	}
	
	/**
	 * Constructor used when encoding the type SHA256_HASHED_DATA
	 */
	public HashedData(HashedDataTypes type, byte[] hash) throws IllegalArgumentException
    {
		super(type, new COEROctetString(hash, 32, 32));
	}
	

	/**
	 * Constructor used when decoding.
	 */
	public HashedData()
    {
		super(HashedDataTypes.class);
	}
		
	/**
	 * Returns the type of id.
	 */
	public HashedDataTypes getType()
    {
		return (HashedDataTypes) choice;
	}

	@Override
	public String toString() {
		return "HashedData [" + choice + "=" + new String(Hex.encode(((COEROctetString)value).getData())) + "]";
	}


}
