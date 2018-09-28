package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;
import com.multicert.v2x.datastructures.base.EciesP256EncryptedKey;

/**
 * This data structure contains an encrypted data encryption key and the algorithm used for its encryption .
 *
 */
public class EncryptedDataEncryptionKey extends COERChoice
{

	
	public enum EncryptedDataEncryptionKeyTypes implements COERChoiceEnumeration, AlgorithmType
    {
        ECIES_NIST_P256,
        ECIES_BRAINPOOL_P256R1;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            return new EciesP256EncryptedKey();
        }

        public Algorithm getAlgorithm()
        {
            switch (this)
            {
                case ECIES_NIST_P256:
                    return new Algorithm(Algorithm.Symmetric.AES_128_CCM, Algorithm.Signature.ECDSA_NIST_P256, Algorithm.Encryption.ECIES_Nist_P256, Algorithm.Hash.SHA_256);
                case ECIES_BRAINPOOL_P256R1:
                default:
                    return new Algorithm(Algorithm.Symmetric.AES_128_CCM, Algorithm.Signature.ECDSA_BRAINPOOL_P256R1, Algorithm.Encryption.ECIES_BRAINPOOL_P256R1, Algorithm.Hash.SHA_256);
            }
        }

        public int getVLength()
        {
            switch (this)
            {
                case ECIES_NIST_P256:
                case ECIES_BRAINPOOL_P256R1:
                default:
                    return 33;
            }
        }

        public int getOutputTagLength()
        {
            switch (this)
            {
                case ECIES_NIST_P256:
                case ECIES_BRAINPOOL_P256R1:
                default:
                    return 16;
            }
        }
    }

	/**
	 * Constructor used when encoding the type aes128ccm
	 */
	public EncryptedDataEncryptionKey(EncryptedDataEncryptionKeyTypes type, EciesP256EncryptedKey key) throws IllegalArgumentException
    {
		super(type, key);
	}
	

	/**
	 * Constructor used when decoding.
	 */
	public EncryptedDataEncryptionKey()
    {
		super(EncryptedDataEncryptionKeyTypes.class);
	}
		
	/**
	 * Returns the type of id.
	 */
	public EncryptedDataEncryptionKeyTypes getType()
    {
		return (EncryptedDataEncryptionKeyTypes) choice;
	}

    @Override
    public String toString() {
        return "EncryptedDataEncryptionKey [" + choice + "=" + value.toString().replace("EciesP256EncryptedKey ", "") +"]";
    }
	
}
