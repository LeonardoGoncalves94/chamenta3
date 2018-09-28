package com.multicert.v2x.asn1.coer;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * COER encoding of a Length Determinant following the section 16 of the ISO/IEC 8825-7:2015 standard
 *
 * A length determinant occurs at the beginning of the encoding of many types, it shall indicate the number of octets (zero or more) occupied by the remainder of the encoding of that type.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COERLengthDeterminant implements COEREncodable
{
   private BigInteger length;
   private static final BigInteger BIG_127 = BigInteger.valueOf(127);

    /**
     * Constructor used when encoding
     *
     */
    public COERLengthDeterminant(BigInteger length)
    {
        this.length = length;
    }

    /**
     * Constructor used when encoding when the length fits in a java long
     *
     */
    public COERLengthDeterminant(long length)
    {
        this(BigInteger.valueOf(length));
    }

    /**
     * Constructor used when decoding
     *
     */
    public COERLengthDeterminant()
    {

    }

    public BigInteger getLength()
    {
        return length;
    }

    public long getLengthAsLong()
    {
        return length.longValue();
    }

    public void encode(DataOutputStream out) throws IOException
    {
        byte[] lengthAsBytes = length.toByteArray();

        if(length.compareTo(BigInteger.ZERO) == -1)
        {
            throw new IOException("Error encoding COER length determinant: String length must be a positive value.");
        }
        if(lengthAsBytes.length > 127) // no máximo podem ser utilizados 127 octetos para codificar o length determinant de uma string. Isto corresponde a uma string de tamanho 2^(127*8) = 2^1016 octetos
        {
            throw new IOException("Error encoding COER length determinant: String length must be less than 2^1016 octets.");
        }
        if(length.compareTo(BigInteger.ZERO) != -1 && length.compareTo(BIG_127) != 1) //short form
        {
            out.write(lengthAsBytes);
            return;
        }
        // long form
        int signOctet = 0;
        if(lengthAsBytes[0] == 0x00 && lengthAsBytes.length > 1){
            signOctet++;
        }

        int lengthIndicator = (lengthAsBytes.length - signOctet) | 0x80;
        out.write(lengthIndicator);
        out.write(lengthAsBytes, signOctet, lengthAsBytes.length -signOctet);
    }

    public void decode(DataInputStream in) throws IOException
    {
        int firstVal = in.read(); // reads first byte from DaraInputStream
        if(firstVal <= 127)
        {
            length = BigInteger.valueOf(firstVal);
        }
        else
        {
            int lengthIndicator = firstVal & 0x7f;
            byte[] lengthValue = new byte[lengthIndicator + 1];
            in.read(lengthValue, 1, lengthIndicator);
            length = new BigInteger(1, lengthValue);
        }
    }
}
