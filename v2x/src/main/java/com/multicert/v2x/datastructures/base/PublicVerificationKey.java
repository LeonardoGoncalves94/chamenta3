package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.cryptography.Algorithm;
import com.multicert.v2x.cryptography.AlgorithmType;

import java.io.*;

/**
 * This class contains a public key to be used as a verification key
 */
public class PublicVerificationKey extends COERChoice
{
    public enum PublicVerificationKeyTypes implements COERChoiceEnumeration, AlgorithmType
    {
        ECDSA_NIST_P256,
        ECDSA_BRAINPOOL_P256r1;

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
          if(this == ECDSA_NIST_P256)
          {
              return new Algorithm(null,Algorithm.Signature.ECDSA_NIST_P256,null, Algorithm.Hash.SHA_256);
          }
          if(this == ECDSA_BRAINPOOL_P256r1)
          {
              return new Algorithm(null, Algorithm.Signature.ECDSA_BRAINPOOL_P256R1, null, Algorithm.Hash.SHA_256);
          }
          return new Algorithm(null,null,null,null);
      }
    }

    /**
     * Constructor used when encoding.
     */
    public PublicVerificationKey(PublicVerificationKeyTypes choice, EccP256CurvePoint value) throws IllegalArgumentException{
        super(choice, value);
        if(value.getType() == EccP256CurvePoint.EccP256CurvePointTypes.X_ONLY){
            throw new IllegalArgumentException("EccP256CurvePoint of type xonly is invalid for structure PublicVerificationKey");
        }
    }

    /**
     * Constructor used when decoding.
     */
    public PublicVerificationKey() {
        super(PublicVerificationKeyTypes.class);
    }

    /**
     * Constructor decoding a public verification key from an encoded byte array.
     * @param encodedPublicKey encoding of the public verification key
     * @throws IOException   if communication problems occurred during serialization.
     */
    public PublicVerificationKey(byte[] encodedPublicKey) throws IOException{
        super(PublicVerificationKeyTypes.class);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encodedPublicKey));
        decode(dis);
    }

    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }


    /**
     * @return the type of key.
     */
    public PublicVerificationKeyTypes getType()
    {
        return (PublicVerificationKeyTypes) choice;
    }

    @Override
    public String toString() {
        return "PublicVerificationKey [" + choice + "=" +  value.toString().replace("EccP256CurvePoint ", "") + "]";
    }
}
