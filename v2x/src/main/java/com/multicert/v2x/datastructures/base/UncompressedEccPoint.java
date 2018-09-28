package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.asn1.coer.EncodeHelper;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;

/**
 * This class represents the x and y coordinates of a ECC Point
 *
 */
public class UncompressedEccPoint extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int OCTETSTRING_SIZE = 32;

    /**
     * Constructor used when encoding
     *
     * @param x 32 byte coordinate
     * @param y 32 byte coordinate
     */
    public UncompressedEccPoint(byte[] x, byte[] y)throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        x = EncodeHelper.padWithZeroes(x, OCTETSTRING_SIZE);
        y = EncodeHelper.padWithZeroes(y, OCTETSTRING_SIZE);
        setComponentValue(X, new COEROctetString(x ,OCTETSTRING_SIZE, OCTETSTRING_SIZE));
        setComponentValue(Y, new COEROctetString(y, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
    }

    /**
     * Constructor used when decoding
     */
    public UncompressedEccPoint()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }


    public void createSequence()
    {
        addComponent(X, false, new COEROctetString(OCTETSTRING_SIZE,OCTETSTRING_SIZE), null);
        addComponent(Y, false, new COEROctetString(OCTETSTRING_SIZE,OCTETSTRING_SIZE), null);
    }

    /**
     * @return x  coordinate
     */
    public byte[] getX(){
        return ((COEROctetString) getComponentValue(X)).getData();
    }

    /**
     * @return y  coordinate
     */
    public byte[] getY(){
        return ((COEROctetString) getComponentValue(Y)).getData();
    }

    @Override
    public String toString() {
        return "UncompressedEccPoint [x=" + new String(Hex.encode(getX())) + ", y=" + new String(Hex.encode(getY())) + "]";
    }

}
