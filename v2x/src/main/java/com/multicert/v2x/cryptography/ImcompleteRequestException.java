package com.multicert.v2x.cryptography;

public class ImcompleteRequestException extends Exception
{
    /**
     * Exception thrown by the SecuredDataGenerator when the received request is incomplete
     * @param message
     */
    public ImcompleteRequestException(String message)
    {
        super(message);
    }
}
