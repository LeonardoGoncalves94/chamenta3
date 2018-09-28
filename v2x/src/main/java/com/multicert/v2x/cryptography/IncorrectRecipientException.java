package com.multicert.v2x.cryptography;

public class IncorrectRecipientException extends Exception
{
    /**
     * Exception thrown by the SecuredDataGenerator when decrypting a message for the wrong recipient certificate
     *
     * @param message
     */
    public IncorrectRecipientException(String message)
    {
        super(message);
    }
}
