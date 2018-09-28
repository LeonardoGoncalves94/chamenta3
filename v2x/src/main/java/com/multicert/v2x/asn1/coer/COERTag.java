package com.multicert.v2x.asn1.coer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Encoding of tags
 *
 * COER Tags are encoded only as part of the encoding of a choice type, where the tag indicates which alternative of the choice type is the chosen alternative
 *
 */
public class COERTag implements COEREncodable
{
    private int tagClass;
    private long tagNumber;

    protected static final int UNIVERSIAL_TAG_CLASS = 0x00;
    protected static final int APPLICATION_TAG_CLASS = 0x40;
    protected static final int CONTEXT_SPECIFIC_TAG_CLASS = 0x80;
    protected static final int PRIVATE_TAG_CLASS = 0xc0;


    /**
     * Constructor used when encoding a COER Tag.
     */
    public COERTag(int tagClass, long tagNumber) throws IOException
    {
        this.tagClass = tagClass;
        this.tagNumber = tagNumber;
    }

    /**
     * Constructor used when decoding a COER Tag.
     */
    public COERTag(){}

    /**
     * @return tag number
     */
    public long getTagNumber() { return tagNumber; }

    /**
     * @return tag class
     */
    public int getTagClass() { return tagClass; }

    /**
     * @return encoded tag
     */
    private byte[] getEncoded()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (tagNumber < 63)
        {
            baos.write(tagClass | (int) tagNumber);
        }
        else
        {
            baos.write(tagClass | 0x3F); // tagClass OR 0011 1111. Meaning that the tagClass occupies the two first bytes
            if (tagNumber < 128)
            {
                baos.write((int) tagNumber); // writes in the next byte the tag number
            }
            else
            {
                byte[] buffer = new byte[9];
                int index = buffer.length;

                buffer[--index] = (byte)(tagNumber & 0x7F);

                do
                {
                    tagNumber >>= 7;
                    buffer[--index] = (byte)(tagNumber & 0x7F | 0x80);
                }
                while (tagNumber > 127);

                baos.write(buffer, index, buffer.length - index);
            }
        }
        return baos.toByteArray();
    }

    public void encode(DataOutputStream out) throws IOException
    {
        out.write(getEncoded());
    }

    public void decode(DataInputStream in) throws IOException
    {
        int firstByte = in.read();
        tagClass = firstByte & 0xc0;

        firstByte = firstByte & 0x3F;
        if(firstByte < 63){
            tagNumber = firstByte;
        }else{
            tagNumber=0;
            int nextByte = in.read();

            if ((nextByte & 0x7f) == 0)
            {
                throw new IOException("Error decoding COER tag: Tag byte cannot be zero.");
            }
            while ((nextByte >= 0) && ((nextByte & 0x80) != 0))
            {
                tagNumber |= (nextByte & 0x7f);
                tagNumber <<= 7;
                nextByte = in.read();
            }

            if (nextByte < 0)
            {
                throw new EOFException("Error decoding COER tag: EOF found inside tag value.");
            }

            tagNumber |= (nextByte & 0x7f);

        }
    }



}
