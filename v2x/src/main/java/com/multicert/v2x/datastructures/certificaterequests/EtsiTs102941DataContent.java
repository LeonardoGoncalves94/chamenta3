package com.multicert.v2x.datastructures.certificaterequests;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtRequest;
import com.multicert.v2x.datastructures.certificaterequests.Authorization.InnerAtResponse;
import com.multicert.v2x.datastructures.certificaterequests.Enrollment.InnerEcResponse;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

import java.io.IOException;

import static com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent.EtsiTs102941DataContentTypes.AUTHORIZATION_REQUEST;
import static com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent.EtsiTs102941DataContentTypes.AUTHORIZATION_RESPONSE;
import static com.multicert.v2x.datastructures.certificaterequests.EtsiTs102941DataContent.EtsiTs102941DataContentTypes.ENROLLMENT_RESPONSE;


public class EtsiTs102941DataContent extends COERChoice
{
    public enum EtsiTs102941DataContentTypes implements COERChoiceEnumeration
    {
        ENROLLMENT_REQUEST,
        ENROLLMENT_RESPONSE,
        AUTHORIZATION_REQUEST,
        AUTHORIZATION_RESPONSE;

        public int myOrdinal()
        {
            return this.ordinal();
        }

        @Override
        public COEREncodable getEncodableType() throws IOException
        {

            switch(this)
            {
                case ENROLLMENT_REQUEST:
                    return new EtsiTs103097Data(); //corresponds to the InnerEcRequestSignedForPOP
                case AUTHORIZATION_REQUEST:
                    return new InnerAtRequest();
                case AUTHORIZATION_RESPONSE:
                    return new InnerAtResponse();
                default:
                    return new InnerEcResponse();
            }
        }
    }


    /**
     * Constructor used when encoding the type ENROLLMENT_REQUEST
     */
    public EtsiTs102941DataContent(EtsiTs102941DataContentTypes type, EtsiTs103097Data InnerECRequestSignedForPOP)
    {
        super(type, InnerECRequestSignedForPOP);
    }

    /**
     * Constructor used when encoding the type ENROLLMENT_RESPONSE
     */
    public EtsiTs102941DataContent(InnerEcResponse innerEcResponse)
    {
        super(ENROLLMENT_RESPONSE, innerEcResponse);
    }

    /**
     * Constructor used when encoding the type AUTHORIZATION_REQUEST
     */
    public EtsiTs102941DataContent( InnerAtRequest innerAtRequest)
    {
        super(AUTHORIZATION_REQUEST, innerAtRequest);
    }

    /**
     * Constructor used when encoding the type AUTHORIZATION_RESPONSE
     */
    public EtsiTs102941DataContent(InnerAtResponse innerAtResponse)
    {
        super(AUTHORIZATION_RESPONSE, innerAtResponse);
    }


    /**
     * Constructor used when decoding.
     */
    public EtsiTs102941DataContent()
    {
        super(EtsiTs102941DataContentTypes.class);
    }

    /**
     * Returns the unit of the Duration, which is an item of DurationChoices
     */
    public EtsiTs102941DataContentTypes getType()
    {
        return (EtsiTs102941DataContentTypes) choice;
    }

}
