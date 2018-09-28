package com.multicert.v2x.datastructures.message.secureddata;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COERNull;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.certificate.SequenceOfCertificate;

import java.io.IOException;


public class SignerIdentifier extends COERChoice
{
	public enum SignerIdentifierChoices implements COERChoiceEnumeration
	{
        DIGEST,
        CERTIFICATE,
        SELF;

		public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType() throws IOException
        {
            switch(this)
            {
                case DIGEST:
                    return new HashedId8();
                case CERTIFICATE:
                    return new SequenceOfCertificate();
                default:
                    return new COERNull();
            }
        }
    }

	/**
	 * Constructor used when encoding the type DIGEST
	 */
	public SignerIdentifier(HashedId8 digest) throws IllegalArgumentException
    {
		super(SignerIdentifierChoices.DIGEST, digest);
	}
	
	/**
	 * Constructor used when encoding of type CERTIFICATE
	 */
	public SignerIdentifier(SequenceOfCertificate certificates) throws IllegalArgumentException
    {
		super(SignerIdentifierChoices.CERTIFICATE, certificates);
	}
	

	/**
	 * Constructor used when encoding the code self or, used when decoding.
	 */
	public SignerIdentifier()
    {
		super(SignerIdentifierChoices.SELF,new COERNull());
		this.choiceEnum = SignerIdentifierChoices.class;
	}
		
	/**
	 * Returns the type of id.
	 */
	public SignerIdentifierChoices getType()
    {
		return (SignerIdentifierChoices) choice;
	}

	
}
