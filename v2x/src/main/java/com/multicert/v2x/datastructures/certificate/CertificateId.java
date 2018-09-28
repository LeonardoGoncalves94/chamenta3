package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.*;
import com.multicert.v2x.datastructures.base.Hostname;
import com.multicert.v2x.datastructures.base.IdentifiedRegion;
import org.bouncycastle.util.encoders.Hex;

public class CertificateId extends COERChoice
{
    //TODO Missing the components linkageData and binaryId. ETSI certificates have those always ABSENT

    public enum CertificateIdTypes implements COERChoiceEnumeration
    {
        NAME,
        NONE;

        public int myOrdinal() {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            switch(this)
            {
                case NAME:
                    return new Hostname();
                default:
                    return new COERNull();

            }
        }
    }

    /**
     * Constructor used when encoding the type NAME
     */
    public CertificateId(Hostname name) throws IllegalArgumentException
    {
        super(CertificateIdTypes.NAME, name);
    }

    /**
     * Constructor used when encoding and decoding  the type NONE
     */
    public CertificateId() throws IllegalArgumentException
    {
        super(CertificateIdTypes.NONE, new COERNull());
        this.choiceEnum = CertificateIdTypes.class;
    }

    /**
     * Returns the type of id.
     */
    public CertificateIdTypes getType(){
        return (CertificateIdTypes) choice;
    }

    /**
     *
     * @return the choice of CertificateId, which is an item of CertificateIdChoices
     */
    public CertificateIdTypes getChoice()
    {
        return (CertificateIdTypes) choice;
    }

    @Override
    public String toString() {
        switch(getType()){
            case NONE:
                return "CertificateId [" + choice +"]";
            default:
                return "CertificateId [" + choice + "=" + value.toString().replace("Hostname ", "") +"]";

        }
    }
}
