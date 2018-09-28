package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;


public class Signature extends COERChoice
{
    public enum SignatureTypes implements COERChoiceEnumeration, AlgorithmType
    {
        ECDSA_NIST_P256_SIGNATURE,
        ECDSA_BRAINPOOL_P256R1_SIGNATURE;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            return new EcdsaP256Signature();
        }

        //contains the algorithms to be used in the signature, such as the hash algorithm
        public Algorithm getAlgorithm()
        {
            switch (this)
            {
                case ECDSA_NIST_P256_SIGNATURE:
                    return new Algorithm(null, Algorithm.Signature.ECDSA_NIST_P256, null, Algorithm.Hash.SHA_256);
                case ECDSA_BRAINPOOL_P256R1_SIGNATURE:
                    return new Algorithm(null, Algorithm.Signature.ECDSA_BRAINPOOL_P256R1, null, Algorithm.Hash.SHA_256);
                default:
                    return new Algorithm(null, Algorithm.Signature.ECDSA_NIST_P256, null, Algorithm.Hash.SHA_256);
            }
        }
    }

    /**
     * Constructor used when encoding.
     */
    public Signature(SignatureTypes type, EcdsaP256Signature value)
    {
        super(type, value);
    }

    /**
     * Constructor used when decoding.
     */
    public Signature()
    {
        super(SignatureTypes.class);
    }

    /**
     * @return the type of signature
     */
    public SignatureTypes getType()
    {
        return (SignatureTypes) choice;
    }

    @Override
    public String toString() {
        return "Signature [" + choice + "=" +  value + "]";
    }
}
