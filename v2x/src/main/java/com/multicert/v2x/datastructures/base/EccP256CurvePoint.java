package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.*;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;

/**
 * This class represents a point in an elliptic curve. This encompasses both NIST p256 as defined in FIPS 186-4 and Brainpool p256r1 as defined in RFC 5639.
 * The point is composed of one or more Octec Strings created with the elliptic curve point encoding and decoding methods defined in IEEE Std 1363-2000 clause 5.5.6.
 * The x-coordinate is encoded as an unsigned integer of length 32 octets in network byte order for all values of the CHOICE; the encoding
 * of the y-coordinate y depends on whether the point is x-only, compressed, or uncompressed. If the point is x-only, y is omitted. If the point is compressed, the value of
 * type depends on the LSB of y: if the LSB of y is 0, type takes the value compressed-y-0, and if the LSB of y is 1, type takes the value compressed-y-1. If the point is
 * uncompressed, y is encoded explicitly as an unsigned integer of length 32 octets in network byte order.
 *
 */
public class EccP256CurvePoint extends COERChoice
{
    private static final int OCTETSTRING_SIZE = 32;

    public enum EccP256CurvePointTypes implements COERChoiceEnumeration
    {
        X_ONLY,
        FILL, // Not used
        COMPRESSED_Y_0,
        COMPRESSED_Y_1,
        UNCOMPRESSED;

       public int myOrdinal()
       {
           return this.ordinal();
       }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case UNCOMPRESSED:
                    return new UncompressedEccPoint();
                default:
                    return new COEROctetString(OCTETSTRING_SIZE,OCTETSTRING_SIZE);
            }
        }
    }

    /**
     * Constructor used when encoding of type xonly as BigInteger
     */
    public EccP256CurvePoint(BigInteger x)
    {
        super(EccP256CurvePointTypes.X_ONLY, new COEROctetString(EncodeHelper.padWithZeroes(integerToBytes(x),OCTETSTRING_SIZE),OCTETSTRING_SIZE,OCTETSTRING_SIZE)); // The BigInteger is transformed into array and padding is added to the beginning
    }


    /**
     * Constructor used when encoding a compressedy0 or compressedy1, or encoded uncompressed
     */
    public EccP256CurvePoint(byte[] encodedPoint) throws IOException
    {
        super(EccP256CurvePointTypes.class);
        EccP256CurvePointTypes type = readType(encodedPoint); //read the type of point from the encoded point
        choice = type;
        if(type == EccP256CurvePointTypes.UNCOMPRESSED) //if the point is uncompressed we need to create a UncompressedEccPoint structure to encode it as the value of COERChoice
        {
            byte[] x = new byte[OCTETSTRING_SIZE];
            System.arraycopy(encodedPoint, 1, x, 0, OCTETSTRING_SIZE); //we decode the x coordinate from the encoded point
            byte[] y = new byte[OCTETSTRING_SIZE];
            System.arraycopy(encodedPoint, OCTETSTRING_SIZE+1, y, 0, OCTETSTRING_SIZE); //we decode the y coordinate from the encoded point
            value = new UncompressedEccPoint(x,y);
        }
        else
        {
            value = new COEROctetString(EncodeHelper.padWithZeroes(removeFirstByte(encodedPoint),OCTETSTRING_SIZE),OCTETSTRING_SIZE, OCTETSTRING_SIZE); //we remove the first byte from the encoded point, and then add padding to the beginning
        }
    }

    /**
     * Constructor used for encoding of type uncompressed
     */
    public EccP256CurvePoint(byte[] uncompressed_x, byte[] uncompressed_y) throws IOException
    {
        super(EccP256CurvePointTypes.UNCOMPRESSED, new UncompressedEccPoint(uncompressed_x, uncompressed_y));
    }

    /**
     * Constructor used for encoding of type uncompressed //
     */
    public EccP256CurvePoint(BigInteger uncompressed_x, BigInteger uncompressed_y) throws IOException
    {
        super(EccP256CurvePointTypes.UNCOMPRESSED, new UncompressedEccPoint(integerToBytes(uncompressed_x), integerToBytes(uncompressed_y)));
    }

    /**
     * Constructor used for decoding.
     */
    public EccP256CurvePoint()
    {
        super(EccP256CurvePointTypes.class);
    }

    /**
     * Returns the type of point.
     */
    public EccP256CurvePointTypes getType(){
        return (EccP256CurvePointTypes) choice;
    }


    /**
     * Method that removes the first byte from a byte array
     *
     */
    private static byte[] removeFirstByte(byte[] compressedEncoding)
    {
        if(compressedEncoding == null || compressedEncoding.length < 1)
        {
            throw new IllegalArgumentException("Error encoding ECC point: Invalid compressed encoding of EccP256CurvePoint");
        }
        byte[] result = new byte[compressedEncoding.length -1];
        System.arraycopy(compressedEncoding, 1, result, 0, result.length);
        return result;
    }

    /**
     * Method used for transforming a BigInteger into a byte array
     *
     */
    private static byte[] integerToBytes(BigInteger data)
    {
        byte[] result = data.toByteArray();
        if(result.length > OCTETSTRING_SIZE)
        {
            byte[] aux = new byte[OCTETSTRING_SIZE];
            System.arraycopy(result, result.length - OCTETSTRING_SIZE, aux, 0, OCTETSTRING_SIZE);
            result = aux;
        }
        return result;
    }

    private static EccP256CurvePointTypes readType(byte[] encodedPoint) {
        if(encodedPoint[0] == 0x02)
        {
            return EccP256CurvePointTypes.COMPRESSED_Y_0;
        }
        if(encodedPoint[0] == 0x03)
        {
            return EccP256CurvePointTypes.COMPRESSED_Y_1;
        }
        if(encodedPoint[0] == 0x04)
        {
            return EccP256CurvePointTypes.UNCOMPRESSED;
        }
        throw new IllegalArgumentException("Error in encoding ECC point: Invalid compress encoding");
    }

    @Override
    public String toString() {
        if(choice == EccP256CurvePointTypes.UNCOMPRESSED){
            return "EccP256CurvePoint [" + choice + "=" +  value.toString().replace("UncompressedEccPoint ", "") + "]";
        }
        return "EccP256CurvePoint [" + choice + "=" +  new String(Hex.encode(((COEROctetString) value).getData())) + "]";
    }

}
