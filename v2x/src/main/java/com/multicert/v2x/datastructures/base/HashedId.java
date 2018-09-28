package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;

import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

/**
 * Base class for all HasedIdX types.
 *
 */
public class HashedId extends COEROctetString
{

    /**
     * Constructor used when constructing an HashedIdX from a full hash byte array.
     *
     * @param fullDigest byte array that contains the full digest.
     * @param hashedIdLength the length of the HashedId
     */
    public  HashedId (byte[] fullDigest, int hashedIdLength)
    {
        if(fullDigest.length < hashedIdLength){
            throw new IllegalArgumentException("Error creating HashedId"+hashedIdLength+": hash value must be at least " + hashedIdLength + " octets.");
        }

        data = copyOfRange(fullDigest, fullDigest.length - hashedIdLength , fullDigest.length);
        lowerBound = hashedIdLength;
        upperBound = hashedIdLength;
    }

    /**
     * Constructor used when decoding.
     *
     * @param hashedIdLength the length of the HashedId
     */
    public  HashedId (int hashedIdLength)
    {
        super(hashedIdLength, hashedIdLength);
    }

    /**
     * @return the hash id value as a array of bytes
     */
    public byte[] getHashedId(){
        return data;
    }
}
