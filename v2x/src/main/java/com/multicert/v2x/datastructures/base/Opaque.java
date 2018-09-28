package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;
import org.bouncycastle.util.encoders.Hex;

/**
 * This class represents an unknown byte array with no lower or upper bounds.
 */
public class Opaque extends COEROctetString
{
    /**
     * Constructor used when encoding
     */
    public Opaque(byte[] data)
    {
        super(data);
    }

    /**
     * Constructor used when decoding
     */
    public Opaque()
    {
        super();
    }

    @Override
    public String toString() {
        return "Opaque [data=" + new String(Hex.encode(data)) + "]";
    }

}
