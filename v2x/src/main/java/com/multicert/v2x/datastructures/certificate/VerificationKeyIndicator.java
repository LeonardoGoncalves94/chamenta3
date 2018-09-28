package com.multicert.v2x.datastructures.certificate;


import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.datastructures.base.EccP256CurvePoint;
import com.multicert.v2x.datastructures.base.PublicVerificationKey;

import java.io.IOException;

public class VerificationKeyIndicator extends COERChoice
{
    public enum VerificationKeyIndicatorTypes implements COERChoiceEnumeration
    {
        VERIFICATION_KEY,
        RECONSTRUCTION_VALUE; //never chosen in ETSI 103 097

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType()
        {
            return new PublicVerificationKey();
        }
    }

    /**
     * Constructor used when encoding of type verificationKey
     *
     */
    public VerificationKeyIndicator(PublicVerificationKey value) throws IllegalArgumentException
    {
        super(VerificationKeyIndicatorTypes.VERIFICATION_KEY, value);
    }

    /**
     * Constructor used when encoding of type reconstructionValue
     *
     */
    public VerificationKeyIndicator(EccP256CurvePoint value) throws IllegalArgumentException
    {
        super(VerificationKeyIndicatorTypes.RECONSTRUCTION_VALUE, value);
    }

    /**
     * Constructor used when decoding.
     */
    public VerificationKeyIndicator() {
        super(VerificationKeyIndicatorTypes.class);
    }

    /**
     * Returns the type of key.
     */
    public VerificationKeyIndicatorTypes getType(){
        return (VerificationKeyIndicatorTypes) choice;
    }
}
