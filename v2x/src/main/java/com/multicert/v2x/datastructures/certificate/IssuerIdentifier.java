package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COEREnumeration;
import com.multicert.v2x.datastructures.base.HashAlgorithm;
import com.multicert.v2x.datastructures.base.HashedId8;
import com.multicert.v2x.datastructures.base.Hostname;

public class IssuerIdentifier extends COERChoice
{
    public enum IssuerIdentifiertypes implements COERChoiceEnumeration
    {
        SHA_256_AND_DIGEST,
        SELF;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case SHA_256_AND_DIGEST:
                    return new HashedId8();
                default:
                    return new COEREnumeration(HashAlgorithm.class);
            }
        }
    }

    /**
     * Constructor used when encoding the type SHA_256_AND_DIGEST
     */
    public IssuerIdentifier(HashedId8 hashId8) throws IllegalArgumentException
    {
        super(IssuerIdentifiertypes.SHA_256_AND_DIGEST, hashId8); //posso passar o int ordinal em vez do item da enum? super(CertificateIdChoices.NAME.myOrdinal(), name);
    }

    /**
     * Constructor used when encoding the type SELF
     */
    public IssuerIdentifier(HashAlgorithm hashAlgorithm) throws IllegalArgumentException
    {
        super(IssuerIdentifiertypes.SELF, new COEREnumeration(hashAlgorithm));
    }

    /**
     * Returns the type of id.
     */
    public IssuerIdentifiertypes getType(){
        return (IssuerIdentifiertypes) choice;
    }

    /**
     * Constructor used when decoding
     */
    public IssuerIdentifier()
    {
        super(IssuerIdentifiertypes.class);
    }

    public HashAlgorithm getHashAlgoritm(){
        if(getType() == IssuerIdentifiertypes.SELF){
            return (HashAlgorithm) ((COEREnumeration) getValue()).getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        switch(getType()){
            case SHA_256_AND_DIGEST:
                return "IssuerIdentifier [" + choice + "=" + value.toString().replace("HashedId8 ", "") +"]";
            case SELF:
            default:
                return "IssuerIdentifier [" + choice + "=" + getHashAlgoritm().toString() +"]";
        }
    }
}
