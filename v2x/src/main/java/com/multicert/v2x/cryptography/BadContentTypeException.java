package com.multicert.v2x.cryptography;

public class BadContentTypeException extends Exception
{
    /**
     * Exception thrown by SecuredDataGenerator when a received data type is not expected
     *
     * @param message
     */
    public BadContentTypeException(String message) {
        super(message);
    }


}
