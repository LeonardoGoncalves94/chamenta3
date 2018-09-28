package com.multicert.v2x.cryptography;

public class DecryptionException extends Exception
{
    /**
     * Exception thrown by the CryptoHelper when there is an error decrypting data
     *
     * @param message
     */
    public DecryptionException(String message)
    {
        super(message);
    }
}
