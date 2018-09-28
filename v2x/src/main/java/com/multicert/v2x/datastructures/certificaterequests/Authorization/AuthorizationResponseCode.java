package com.multicert.v2x.datastructures.certificaterequests.Authorization;


import com.multicert.v2x.asn1.coer.COEREnumerationType;

/**
 * This class enumerates the possible outcomes of an enrollment request.
 * OK - should be used on the response if no problems where found on the request validation.
 */
public enum AuthorizationResponseCode implements COEREnumerationType
{
    OK,
    CANT_PARSE,
    BAD_CONTENT_TYPE,
    IM_NOT_THE_RECIPIENT,
    UNKNOWN_ENCRYPTION_ALGORITHM,
    DECRYPTION_FAILED,
    UNKNOWN_ITS,
    INVALID_SIGNATURE,
    INVALID_ENCRYPTION_KEY,
    BAD_ITS_STATUS,
    INCOMPLETE_REQUEST,
    DENIED_PERMISSIONS,
    INVALID_KEYS,
    DENIED_REQUEST,
    ENROLLMENT_VERIFICATION_FAILED;


}
