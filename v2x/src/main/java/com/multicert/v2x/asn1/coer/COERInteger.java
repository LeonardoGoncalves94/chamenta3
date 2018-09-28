package com.multicert.v2x.asn1.coer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * COER encoding of an Integer following section 10 of the ISO/IEC 8825-7:2015 standard
 *
 * Integer may or may not need a length determinant for the encoding
 *
 * Signed integers are not yet implemented
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COERInteger implements COEREncodable
{
    private static final BigInteger UNSIGNED_UPPER_BOUND_8 = BigInteger.valueOf(255);
    private static final BigInteger UNSIGNED_UPPER_BOUND_16 = BigInteger.valueOf(65535);
    private static final BigInteger UNSIGNED_UPPER_BOUND_32 = BigInteger.valueOf(4294967295L);
    private static final BigInteger UNSIGNED_UPPER_BOUND_64 = new BigInteger("18446744073709551615");

    private static final BigInteger SIGNED_UPPER_BOUND_7 = BigInteger.valueOf(127);
    private static final BigInteger SIGNED_LOWER_BOUND_7 = BigInteger.valueOf(-128);
    private static final BigInteger SIGNED_UPPER_BOUND_15 = BigInteger.valueOf(32767);
    private static final BigInteger SIGNED_LOWER_BOUND_15 = BigInteger.valueOf(-32768);
    private static final BigInteger SIGNED_UPPER_BOUND_31 = BigInteger.valueOf(2147483647);
    private static final BigInteger SIGNED_LOWER_BOUND_31 = BigInteger.valueOf(-2147483648);
    private static final BigInteger SIGNED_UPPER_BOUND_63 = BigInteger.valueOf(9223372036854775807L);
    private static final BigInteger SIGNED_LOWER_BOUND_63 = BigInteger.valueOf(-9223372036854775808L);


    protected BigInteger value;
    protected BigInteger lowerBound = null;
    protected BigInteger upperBound = null;

    /**
     * Constructor used when encoding an integer
     *
     *@param value the integer value.
     *@param lowerBound the minimum possible value for the integer.
     *@param upperBound the maximum possible value for the integer, null if not known.
     */
    public COERInteger (BigInteger value, BigInteger lowerBound, BigInteger upperBound)throws IllegalArgumentException
    {
        if(upperBound != null && lowerBound != null) // if the boundaries are known
        {
            if (value != null && value.compareTo(upperBound) == 1)
            {
                throw new IllegalArgumentException("Error encoding COERInteger: Value is greater than the upper bound (max possible value).");
            }
            if (value != null && value.compareTo(lowerBound) == -1)
            {
                throw new IllegalArgumentException("Error encoding COERInteger: Value is lesser than the lower bound (min possible value).");
            }
        }
        this.value = value;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Constructor used when encoding an integer with bounds that fit in java long
     *
     * @param value the integer value.
     * @param lowerBound the minimal value of the integer, or null if not known.
     * @param upperBound the maximal value of the integer, or null if not known.
     */
    public COERInteger(long value, long lowerBound, long upperBound) {
        this(BigInteger.valueOf(value),BigInteger.valueOf(lowerBound),BigInteger.valueOf(upperBound));
    }

    /**
     * Constructor used when encoding an integer with unknown min or max value.
     *
     * @param value the integer value.
     */
    public COERInteger(BigInteger value)
    {
        this.value = value;
    }

    /**
     * Constructor used when encoding an integer that fits in a java long and with unknown min or max value.
     *
     * @param value the integer value.
     */
    public COERInteger(long value)
    {
        this.value = BigInteger.valueOf(value);
    }

    /**
     * Constructor used when decoding
     *
     *@param lowerBound the minimum possible value for the integer.
     *@param upperBound the maximum possible value for the integer, null if not known.
     */
    public COERInteger(BigInteger lowerBound, BigInteger upperBound)
    {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Constructor used when decoding integers with bounds that fit in java long
     *
     *@param lowerBound the minimum possible value for the integer.
     *@param upperBound the maximum possible value for the integer, null if not known.
     */
    public COERInteger(long lowerBound, long upperBound)
    {
        this.lowerBound = BigInteger.valueOf(lowerBound);
        this.upperBound = BigInteger.valueOf(upperBound);
    }

    /**
     * Constructor used when decoding an integer with unknown min or max value.
     */
    public COERInteger(){
    }

    public void encode(DataOutputStream out) throws IOException
    {
       if(lowerBound != null && lowerBound.compareTo(BigInteger.ZERO) != -1) // if the integer is unsigned
       {
           serializeUnsigned(out);
       }
       else
       {
           serializeSigned(out);
       }

    }

    public void serializeUnsigned(DataOutputStream out) throws IOException
    {
        byte[] val = value.toByteArray();
        int signOctet = 0;
        if(val[0] == 0x00 && val.length > 1)
        {
            signOctet++;
        }

        if(upperBound == null || upperBound.compareTo(UNSIGNED_UPPER_BOUND_64) == 1) // if the max possible value is unknown or greater than 2^64-1, then we need a length determinant to encode the length of the integer
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(val.length-signOctet);
            lengthDeterminant.encode(out);
            out.write(val, signOctet, val.length -signOctet);
        }
        else
        {
            byte[] buffer = new byte[getNumberOfOctets()];
            System.arraycopy(val, signOctet, buffer, buffer.length - (val.length - signOctet), val.length - signOctet);
            out.write(buffer);
        }
    }

    public void serializeSigned(DataOutputStream out) throws IOException
    {
        byte[] val = value.toByteArray();

        int signOctet = 0;
        if(val[0] == 0x00 && val.length > 1)
        {
            signOctet++;
        }

        if(lowerBound == null || lowerBound.compareTo(SIGNED_LOWER_BOUND_63) == -1 || upperBound == null || upperBound.compareTo(SIGNED_UPPER_BOUND_63) == 1) // if the value is out of bounds or has unknown bounds, then we need a length determinant to encode the length of the integer
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant(val.length-signOctet);
            lengthDeterminant.encode(out);
            out.write(val, signOctet, val.length -signOctet);
        }
        else
        {
            byte[] buffer = new byte[getNumberOfSignedOctets()];
            System.arraycopy(val, signOctet, buffer, buffer.length - (val.length - signOctet), val.length - signOctet);
            out.write(buffer);
        }
    }

    /**
     *
     *@return the number of octets needed for the Unsigned integer encoding/decoding
     */
    private Integer getNumberOfOctets()
    {
        if(upperBound.compareTo(UNSIGNED_UPPER_BOUND_8) != 1) // if the max possible value is less than or equal to 255, then the integer is encoded using 1 octet
        {
            return 1;
        }
        if(upperBound.compareTo(UNSIGNED_UPPER_BOUND_16) != 1)
        {
            return 2;
        }
        if(upperBound.compareTo(UNSIGNED_UPPER_BOUND_32) != 1)
        {
            return 4;
        }
     return 8;
    }

    /**
     *
     *@return the number of octets needed for the Signed integer encoding/decoding
     */
    private Integer getNumberOfSignedOctets()
    {
        if(lowerBound.compareTo(SIGNED_LOWER_BOUND_7) != -1 && upperBound.compareTo(SIGNED_UPPER_BOUND_7) != 1) // if the value is between -128 and 128, then the integer is encoded using 1 octet
        {
            return 1;
        }
        if(lowerBound.compareTo(SIGNED_LOWER_BOUND_15) != -1 && upperBound.compareTo(SIGNED_UPPER_BOUND_15) != 1)
        {
            return 2;
        }
        if(lowerBound.compareTo(SIGNED_LOWER_BOUND_31) != -1 && upperBound.compareTo(SIGNED_UPPER_BOUND_31) != 1)
        {
            return 4;
        }
        return 8;
    }

    public void decode(DataInputStream in) throws IOException
    {
        if(lowerBound != null && lowerBound.compareTo(BigInteger.ZERO) != -1) // if the integer is unsigned
        {
            deserializeUnsigned(in);
        }
        else
        {
            deserializeSigned(in);
        }
    }

    private void deserializeUnsigned(DataInputStream in) throws IOException
    {
        int numberOfOctets;
        if(upperBound == null || upperBound.compareTo(UNSIGNED_UPPER_BOUND_64) == 1) //if the max possible value is unknown or greater than 2^64-1, then we need to decode the length determinant to get the length of the integer
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant();
            lengthDeterminant.decode(in);
            numberOfOctets = lengthDeterminant.getLength().intValue(); //we get the number of octets from the length determinant
        }
        else
        {
            numberOfOctets = getNumberOfOctets();
        }
        byte[] buffer = new byte[numberOfOctets];
        in.read(buffer);
        value = new BigInteger(1, buffer);
    }

    private void deserializeSigned(DataInputStream in) throws IOException
    {
        int numberOfOctets;
        if(upperBound == null || upperBound.compareTo(SIGNED_UPPER_BOUND_63) == 1 || lowerBound == null || lowerBound.compareTo(SIGNED_LOWER_BOUND_63) == -1) // if the value is out of bounds or has unknown bounds, then we need to decode the length determinant to get the length of the integer
        {
            COERLengthDeterminant lengthDeterminant = new COERLengthDeterminant();
            lengthDeterminant.decode(in);
            numberOfOctets = lengthDeterminant.getLength().intValue(); //we get the number of octets from the length determinant
        }
        else
        {
            numberOfOctets = getNumberOfSignedOctets();
        }
        byte[] buffer = new byte[numberOfOctets];
        in.read(buffer);
        value = new BigInteger(readValue(buffer));
    }

    /**
     * Method used for reading signed values from a buffer
     *
     * @param buffer the input buffer to transform
     * @return the result array that contains the value
     */
    private byte[] readValue(byte[] buffer)
    {
        for(int i = 0; i < buffer.length; i ++)
        {
            if(buffer[i] != 0) // when the position that has the start of the value is found
            {
                byte[] result = new byte[buffer.length - i]; // create the result array with the same length as the value to read
                System.arraycopy(buffer, i, result, 0, buffer.length - i ); // copy the value from input buffer to result
                return result;
            }
        }
        return buffer;
    }

    /**
     *
     * @return the value of this Integer
     */
    public BigInteger getValue()
    {
        return value;
    }

    /**
     *
     * @return the value of this Integer in as a java long
     */
    public long getValueAsLong()
    {
        return value.longValue();
    }

    public BigInteger getLowerBound()
    {
        return lowerBound;
    }

    public BigInteger getUpperBound()
    {
        return upperBound;
    }

    public long getUpperBoundAsLong()
    {
        return upperBound.longValue();
    }

    public long getLowerBoundAsLong()
    {
        return lowerBound.longValue();
    }
}
