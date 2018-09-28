package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * COER encoding of a BitSting following section 13 of the ISO/IEC 8825-7:2015 standard
 *
 * BitString can be variable or fixed size
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COERBitString implements COEREncodable
{
    protected long bitString;
    protected Integer length;
    protected boolean isFixedSize;

    /**
     * Constructor used when encoding a bit string.
     *
     * If setFlag methods should be used, set bitString to 0 initially and call setFlag for each position (from 0 to length -1).
     * @param bitString the value of the string.
     * @param length the length of the string.
     * @param isFixedSize if the size of the string is fixed or variable.
     * @throws IllegalArgumentException if length was invalid.
     */
    public COERBitString(long bitString, Integer length, boolean isFixedSize) throws IllegalArgumentException
    {
        this.bitString = bitString;
        this.length = length;
        this.isFixedSize = isFixedSize;
    }

    /**
     * Constructor used when decoding a BitString of fixed length
     *
     */
    public COERBitString(Integer length)
    {
        this.length = length;
        this.isFixedSize = true;
    }

    /**
     * Constructor used when decoding a BitString of variable size
     */
    public COERBitString()
    {
        this.isFixedSize = false;
    }

    /**
     * Method that returns the bit at the given position
     *
     * @param position position in BitString, 1 for least significant to (length - 1) for most significant.
     * @return true if bit is set.
     * @throws IllegalArgumentException if position is out of bounds.
     */
    public boolean getFlag(int position) throws IllegalArgumentException
    {
        if(position >= length || position < 0)
        {
            throw new IllegalArgumentException("Error getting flag: position "+ position +" is out of the BitString bounds");
        }
        long mask = 1 << position;
        return (bitString & mask) > 0;
    }

    /**
     * Method that sets the bit at the given position
     *
     * BitString must be 0 before calling, it is not possible to unset a flag
     * @param position position in BitString, 0 for least significant to (length - 1) for most significant.
     * @param flag true if bit at position is to be set
     * @throws IllegalArgumentException if position is out of bounds.
     */
    public void setFlag(int position, boolean flag) throws IllegalArgumentException
    {
        if(position >= length || position < 0)
        {
            throw new IllegalArgumentException("Error setting flag: position "+ position +" is out of the BitString bounds");
        }
        if(flag)
        {
            long mask = 1 << position;
            bitString |= mask;
        }
    }

    public void encode(DataOutputStream out) throws IOException
    {
        int unusedBits = 0;
        int remainder = length % 8;
        if(remainder > 0)
        {
            unusedBits = 8 - remainder;
        }

        int numberOfBytes = (unusedBits == 0 ? length/8 : length/8 +1);
        if(isFixedSize)
        {
            serialize(out, numberOfBytes, unusedBits);
        }
        else // if the size is variable a length determinant, an initial octet, and subsequent octets are needed for the encoding
        {
            if(bitString == 0) // If the value of the BitString is 0, then it shall be encoded directly in the initial octet and there will be no subsequent octets
            {
                COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(numberOfBytes);
                lengthDeterminant.encode(out);
                out.write(0);
            }
            else
            {
                COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(1 + numberOfBytes); // one byte for the initial octet and the number of bytes needed for the BitString
                lengthDeterminant.encode(out);
                out.write(unusedBits); //initial octet (indicates how many unused bits are in the last subsequent octet)
                serialize(out, numberOfBytes, unusedBits);
            }
        }
    }

    private void serialize(DataOutputStream out, int numberOfBytes, int unusedBits) throws IOException {
        long bitStringData = bitString << unusedBits;

        byte[] val = BigInteger.valueOf(bitStringData).toByteArray();
        int signOctet = 0;
        if(val[0] == 0x00)
        {
            signOctet++;
        }

        byte[] buffer =  new byte[numberOfBytes];
        System.arraycopy(val, signOctet, buffer, buffer.length - (val.length -signOctet), val.length -signOctet);
        out.write(buffer);
    }

    public void decode(DataInputStream in) throws IOException
    {
        int notUsedBits = 0;
        int numberOfBytes; //The number of bytes occupied by the Bitstring
        if(isFixedSize)
        {
            int remainder = length % 8;
            if(remainder > 0)
            {
                notUsedBits = 8 - remainder;
            }

            numberOfBytes = (notUsedBits == 0 ? length/8 : length/8 +1);
            deserialize(in, numberOfBytes, notUsedBits);
        }
        else
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant();
            lengthDeterminant.decode(in); //holds the length of the BitString (initial octet + subsequent octets)
            numberOfBytes = (lengthDeterminant.getLength().intValue() - 1); // number of bytes occupied by the subsequent octets (-1 so that we don't count the initial octet)

            if(numberOfBytes != 0) // In case there are subsequent octets
            {
                notUsedBits = in.read();
                deserialize(in, numberOfBytes, notUsedBits);
            }
            else // In case there is no subsequent octets, the value of the BitString is 0;
            {
                bitString = 0;
            }
        }
    }

    private void deserialize(DataInputStream in, int numberOfBytes, int notUsedBits) throws IOException
    {
        byte[] value = new byte[numberOfBytes];
        in.read(value); //reads DataIputStream into the value array
        BigInteger bigInteger = new BigInteger(1,value);
        bitString = bigInteger.longValue() >>> notUsedBits;
    }

    @Override
    public String toString() {
        return "COERBitString [bitString=" + BigInteger.valueOf(bitString).toString(16) + ( length != null ?  ", length=" + length : "")
                + "]";
    }
}
