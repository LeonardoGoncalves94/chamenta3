package com.multicert.v2x.asn1.coer;


import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * COER encoding of a sequence-of following the section  17 of the ISO/IEC 8825-7:2015 standard
 *
 * The encoding of a sequence-of value shall consist of a quantity field followed by the encodings of the occurrences of the component (must be of the same type)
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COERSequenceOf implements COEREncodable
{
    protected COEREncodable[] values;
    COEREncodable emptyValue;
    protected byte[] emptyValueEncoded;


    /**
     * Constructor used when encoding
     */
    public COERSequenceOf(COEREncodable[] values)
    {
        this.values = values;
    }

    /**
     * Constructor used when decoding
     */
    public COERSequenceOf(COEREncodable emptyValue)
    {
        this.emptyValue = emptyValue;
        emptyValueEncoded =  EncodeHelper.serialize(emptyValue);
        values = null;
    }



    @Override
    public void encode(DataOutputStream out) throws IOException
    {
        COERInteger length = new COERInteger(BigInteger.valueOf(values.length), BigInteger.ZERO, null);
        length.encode(out);
        for(COEREncodable v : values)
        {
            v.encode(out);
        }
    }

    @Override
    public void decode(DataInputStream in) throws IOException
    {

        COERInteger length = new COERInteger(BigInteger.ZERO, null);
        length.decode(in);
        values = new COEREncodable[length.getValue().intValue()];

        for(int i = 0; i < (int)length.getValueAsLong(); i ++)
        {
            values[i] = EncodeHelper.deserialize(emptyValueEncoded);
            values[i].decode(in);
        }
    }

    @Override
    public String toString() {
        return "COERSequenceOf [sequenceValues="
                + Arrays.toString(values) + "]";
    }

    public COEREncodable[] getValues()
    {
        return values;
    }
}
