package com.multicert.v2x.datastructures.certificaterequests;

import com.multicert.v2x.asn1.coer.COERInteger;


/**
 * This class  specifies the version used for the certificate format specification. In the present standard, the certificate format shall be set to ts103097v131 (integer value 1);
 *
 */
public class CertificateFormat extends COERInteger
{
    protected static final int LOWERBOUND = 1;
    protected static final int UPPERBOUND = 255;

    /**
     * Contructor used when encoding (populates an Integer)
     */
    public CertificateFormat(int value)
    {
        super(value, LOWERBOUND, UPPERBOUND);
    }

    /**
     * Contructor used when decoding (creates and empty integer)
     */
    public CertificateFormat()
    {
        super(LOWERBOUND, UPPERBOUND);
    }
}
