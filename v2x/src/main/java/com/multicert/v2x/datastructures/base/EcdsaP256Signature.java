package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;

public class EcdsaP256Signature extends COERSequence
{
    private static final int OCTETSTRING_SIZE = 32;
    private static final int SEQUENCE_SIZE = 2;
    private static final int R = 0;
    private static final int S = 1;

    /**
     * Constructor used when encoding
     *
     */
    public EcdsaP256Signature(EccP256CurvePoint r, byte[] s) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        if(s == null){
            throw new IllegalArgumentException("Error encoding signature: s value cannot be null in EcdsaP256Signature");
        }
        setComponentValue(R, r);
        setComponentValue(S, new COEROctetString(s, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
    }

    /**
     * Constructor used when decoding
     *
     */
    public EcdsaP256Signature()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }


    /**
     *
     * @return r value
     */
    public EccP256CurvePoint getR(){
        return (EccP256CurvePoint) getComponentValue(R);
    }

    /**
     *
     * @return the 32 byte s value
     */
    public byte[] getS(){
        return ((COEROctetString) getComponentValue(S)).getData();
    }

    private void createSequence(){
        addComponent(R, false, new EccP256CurvePoint(), null);
        addComponent(S, false, new COEROctetString(OCTETSTRING_SIZE, OCTETSTRING_SIZE), null);
    }

    @Override
    public String toString() {
        return "EcdsaP256Signature [r=" + getR().toString().replaceAll("EccP256CurvePoint ", "") + ", s=" + new String(Hex.encode(getS())) + "]";
    }
}
