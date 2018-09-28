package com.multicert.v2x.cryptography;

public class InvalidSignatureException extends Exception
{
    /**
     * Exception thrown by SecuredDataGenerator when the signature does not verify
     *
     * @param message
     */
    public InvalidSignatureException(String message) {
        super(message);
    }


}
