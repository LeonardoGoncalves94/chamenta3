package com.multicert.v2x.datastructures.base;

import org.bouncycastle.util.encoders.Hex;

/**
 * HashedId3 is an hashed based identifier for other data structures. The HashedId3 for a given data structure is calculated
 * by calculating the SHA-256 hash of the encoded data structure and then taking the three least significant bytes of
 * the digest.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class HashedId3 extends HashedId
{
    private static final int HASHED_ID_LENGTH = 3;

    /**
     * Constructor used when constructing an HashedId3 from a full hash byte array.
     *
     * @param fullDigest byte array that contains the full digest.
     */
    public HashedId3(byte[] fullDigest)
    {
        super(fullDigest, HASHED_ID_LENGTH);
    }

    /**
     * Constructor used when decoding.
     *
     */
    public HashedId3()
    {
        super(HASHED_ID_LENGTH);
    }

    @Override
    public String toString() {
        return "HashedId3 [" + new String(Hex.encode(data)) + "]";
    }
    public String getString(){return new String(Hex.encode(data)); }
}
