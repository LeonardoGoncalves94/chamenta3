package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COEREnumerationType;

/**
 * This enumeration indicates whether a certificate is explicit or implicit. (only explicit certificates are supported by ETSI103 097)
 */
public enum  CertificateType implements COEREnumerationType
{
    explicit,
    //implicit
}

