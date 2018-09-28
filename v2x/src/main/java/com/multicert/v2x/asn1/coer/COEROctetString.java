package com.multicert.v2x.asn1.coer;

import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class COEROctetString implements COEREncodable
{
    protected byte[] data;
    protected Integer lowerBound = null;
    protected Integer upperBound= null;

    /**
     * Constructor used when encoding an octet string with known lower or upper bounds.
     *
     * @param data the octed stream to encode.
     * @param lowerBound the minimum possible value for the octet string length, null when unknown.
     * @param upperBound the maximum possible value for the octet string length, null when unknown.
     */
    public COEROctetString(byte[] data, Integer lowerBound, Integer upperBound)
    {
        if(data != null && lowerBound != null && data.length > upperBound)
        {
            throw new IllegalArgumentException("Error: Given data to octet string is larger than maximal value of " + upperBound);
        }

        if(data != null && upperBound != null && data.length < lowerBound)
        {
            throw new IllegalArgumentException("Error: Given data to octet string is larger than minimum value of " + lowerBound);
        }

        this.data = data;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    /**
     * Constructor used when encoding an octet string with unknown lower or upper bounds.
     *
     * @param data the octet string to be encoded
     */
    public COEROctetString(byte[] data)
    {
        this.data = data;

    }

    /**
     * Constructor used when decoding an octet string with known lower of upper bounds
     *
     * @param lowerBound the minimum possible value for the octet string length, null when unknown.
     * @param upperBound the maximum possible value for the octet string length, null when unknown.
     */
    public COEROctetString(Integer lowerBound, Integer upperBound)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Constructor used when decoding an octet string with unknown lower or upper bounds
     */
    public COEROctetString() {}

    /**
     *
     * @return the octed stream data.
     */
    public byte[] getData() {
        return data;
    }

    public void encode(DataOutputStream out) throws IOException
    {
        if (data == null)
        {
            throw new IOException("Null data in the octet string.");
        }

        if(lowerBound != upperBound || lowerBound == null || upperBound == null) // if the exact length of the string is not known (upperBound != lowerBound) we need to encode a length determinant that expresses that string's length
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(BigInteger.valueOf(data.length));
            lengthDeterminant.encode(out);
        }
        out.write(data);
    }

    public void decode(DataInputStream in) throws IOException
    {

        if(lowerBound != upperBound || lowerBound == null || upperBound == null) // if there is a length determinant
        {
          COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant();
          lengthDeterminant.decode(in);
          data = new byte[lengthDeterminant.getLength().intValue()];
          in.read(data);
        }
        else
        {
            data = new byte[upperBound];
            in.read(data);
        }

    }

    @Override
    public String toString() {
        return "COEROctetStream [data=" + new String(Hex.encode(data)) + "]";
    }

}
