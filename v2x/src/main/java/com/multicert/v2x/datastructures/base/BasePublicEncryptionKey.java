package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;

import java.io.IOException;

/**
 * This class represents the type of the public encryption key and its type
 */
public class BasePublicEncryptionKey extends COERChoice
{
    public enum BasePublicEncryptionKeyTypes implements COERChoiceEnumeration, AlgorithmType
    {

        ECIES_NIST_P256,
        ECIES_BRAINPOOL_P256r1;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            return new EccP256CurvePoint();
        }

        @Override
        public Algorithm getAlgorithm()
        {
            if(this == ECIES_NIST_P256)
            {
                return new Algorithm(Algorithm.Symmetric.AES_128_CCM, Algorithm.Signature.ECDSA_NIST_P256, Algorithm.Encryption.ECIES_Nist_P256, Algorithm.Hash.SHA_256);
            }
            if(this == ECIES_BRAINPOOL_P256r1)
            {
                return new Algorithm(Algorithm.Symmetric.AES_128_CCM, Algorithm.Signature.ECDSA_BRAINPOOL_P256R1, Algorithm.Encryption.ECIES_BRAINPOOL_P256R1, Algorithm.Hash.SHA_256);
            }
            return new Algorithm(null,null,null,null);
        }
    }

    /**
     * Constructor used when encoding.
     */
    public BasePublicEncryptionKey(BasePublicEncryptionKeyTypes type, EccP256CurvePoint value) throws IllegalArgumentException{
        super(type, value);
    }

    /**
     * Constructor used when decoding.
     */
    public BasePublicEncryptionKey() {
        super(BasePublicEncryptionKeyTypes.class);
    }

    /**
     * Returns the type of key.
     */
    public BasePublicEncryptionKeyTypes getType(){
        return (BasePublicEncryptionKeyTypes) choice;
    }

    @Override
    public String toString() {
        return "BasePublicEncryptionKey [" + choice + "=" +  value.toString().replace("EccP256CurvePoint ", "") + "]";
    }

}
