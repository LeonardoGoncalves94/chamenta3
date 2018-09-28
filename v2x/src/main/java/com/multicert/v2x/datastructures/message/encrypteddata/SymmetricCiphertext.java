package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;

/**
 * This data structure contains a ciphertext generated with an approved symmetric algorithm (only aes128ccm as of now).
 *
 */
public class SymmetricCiphertext extends COERChoice
{
	
	public enum SymmetricCiphertextChoices implements COERChoiceEnumeration, AlgorithmType{
		AES_128_CCM;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        public Algorithm getAlgorithm()
		{
            return new Algorithm(Algorithm.Symmetric.AES_128_CCM, null, null, null);
        }

		@Override
		public COEREncodable getEncodableType()
		{
			return new AesCcmCiphertext();
		}
	}
	
	/**
	 * Constructor used when encoding the type AES_128_CCM
	 */
	public SymmetricCiphertext(AesCcmCiphertext cipherText) throws IllegalArgumentException
    {
		super(SymmetricCiphertextChoices.AES_128_CCM, cipherText);
	}

	/**
	 * Constructor used when decoding.
	 */
	public SymmetricCiphertext()
    {
		super(SymmetricCiphertextChoices.class);
	}
		
	/**
	 * Returns the type of id.
	 */
	public SymmetricCiphertextChoices getType()
    {
		return (SymmetricCiphertextChoices) choice;
	}

	@Override
	public String toString() {
		return "SymmetricCiphertext [" + choice + "=" + value.toString().replace("AesCcmCiphertext ", "") +"]";
	}

}
