package com.multicert.v2x.datastructures.certificaterequests;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Content;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

import java.io.*;


public class EcSignature extends COERChoice
{
    public enum EcSignatureTypes implements COERChoiceEnumeration
    {
        ENCRYPTED_EC_SIGNATURE,
        EC_SIGNATURE;
        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
           return new EtsiTs103097Data();
        }
    }

    /**
     * Constructor used when encoding
     *
     * @param type an item of the EcSignatureTypes
     * @param data the value for that choice
     */
    public EcSignature(EcSignatureTypes type, EtsiTs103097Data data) throws IOException
    {
        super(null, null);
        if(type.equals(EcSignatureTypes.ENCRYPTED_EC_SIGNATURE))
        {
            if(data.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.ENCRYPTED_DATA)
            {
                throw new IOException("Error creating encrypted EcSignature: EtsiTs103097Data must be encrypted");
            }
            else
            {
                choice = type;
                value = data;
            }
        }
        else if(type.equals(EcSignatureTypes.EC_SIGNATURE))
        {
            if(data.getContent().getType() != EtsiTs103097Content.EtsiTs103097ContentChoices.SIGNED_DATA)
            {
                throw new IOException("Error creating encrypted EcSignature: EtsiTs103097Data must be encrypted");
            }
            else
            {
                choice = type;
                value = data;
            }
        }
    }

    /**
     * Constructor used when decoding
     */
    public EcSignature()
    {
        super(EcSignatureTypes.class);
    }

    /**
     * Constructor decoding a certificate from an encoded byte array.
     * @param encodedEcSignature encoding of the enrollment credential signature
     * @throws IOException   if communication problems occurred during serialization.
     */
    public EcSignature(byte[] encodedEcSignature) throws IOException{
        super(EcSignatureTypes.class);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(encodedEcSignature));
        decode(dis);
    }

    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

    public EcSignatureTypes getChoice()
    {
        return (EcSignatureTypes) choice;
    }

    @Override
    public String toString() {
        return "Choice:"+ choice + " EcSignature:" + ((EtsiTs103097Data) value);
    }
}
