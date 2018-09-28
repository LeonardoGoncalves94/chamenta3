package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;

/**
 * This class is used to transfer a 16-byte symmetric key encrypted using ECIES.
 * It contains 3 fields:
 * v is the the sender’s ephemeral public key.
 * c is the encrypted symmetric key, which has its generation algorithm identified by the CHOICE indicated in the following SymmetricCiphertext.
 * t is the authentication tag
 *
 * Encryption and decryption are carried out as specified in IEEE Std 1363a-2004, section 5.3.5.
 *
 */
public class EciesP256EncryptedKey extends COERSequence
{
    private static final int OCTETSTRING_SIZE = 16;

    private static final int SEQUENCE_SIZE= 3;

    private static final int V = 0;
    private static final int C = 1;
    private static final int T = 2;

    /**
     * Constructor used when decoding
     */
    public EciesP256EncryptedKey(){
        super(SEQUENCE_SIZE);
        createSequence();
    }

    /**
     * Constructor used when encoding
     */
    public EciesP256EncryptedKey(EccP256CurvePoint v, byte[] c, byte[] t) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        if(c == null){
            throw new IllegalArgumentException("Error encoding encrypted key: c cannot be null for EciesP256EncryptedKey");
        }
        if(t == null){
            throw new IllegalArgumentException("Error encoding encrypted key: t cannot be null for EciesP256EncryptedKey");
        }
        setComponentValue(V,v);
        setComponentValue(C, new COEROctetString(c, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
        setComponentValue(T, new COEROctetString(t, OCTETSTRING_SIZE, OCTETSTRING_SIZE));
    }

    /**
     *
     * @return v value
     */
    public EccP256CurvePoint getV()
    {
        return (EccP256CurvePoint) getComponentValue(V);
    }

    /**
     *
     * @return the 16 byte c value
     */
    public byte[] getC()
    {
        return ((COEROctetString) getComponentValue(C)).getData();
    }

    /**
     *
     * @return the 16 byte t value
     */
    public byte[] getT()
    {
        return ((COEROctetString) getComponentValue(T)).getData();
    }

    private void createSequence(){
        addComponent(V, false, new EccP256CurvePoint(), null);
        addComponent(C, false, new COEROctetString(OCTETSTRING_SIZE, OCTETSTRING_SIZE), null);
        addComponent(T, false, new COEROctetString(OCTETSTRING_SIZE, OCTETSTRING_SIZE), null);
    }


    @Override
    public String toString() {
        return "EciesP256EncryptedKey [v=" + getV().toString().replaceAll("EccP256CurvePoint ", "") + ", s=" + new String(Hex.encode(getC())) + ", t=" + new String(Hex.encode(getT()))+ "]";
    }

}
