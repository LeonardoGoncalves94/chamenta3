package com.multicert.v2x.cryptography;

public class Algorithm
{
    /**
     * Help class that enumerates the supported key algorithms in one place
     * The user specifies algorithms from this structure, then the cryptohelper transforms in the standard algorithm data structures (e.g Signature.Signaturechoices, SymmetricAlgorithm, etc)
     *
     * Used to simplify the verifications on the CryptoHelper
     *
     */

    public enum Symmetric{
        AES_128_CCM(16,12);


        private Symmetric(int keyLength, int nounceLength)
        {
            this.keyLength = keyLength;
            this.nounceLength = nounceLength;
        }
        private int keyLength;
        private int nounceLength;

        /**
         *
         * @return the length of symmetric key length
         */
        public int getKeyLength(){
            return keyLength;
        }

        /**
         *
         * @return the length of symmetric key length
         */
        public int getNounceLength(){
            return nounceLength;
        }
    }

    public enum Signature
    {
        ECDSA_NIST_P256,
        ECDSA_BRAINPOOL_P256R1;

        public static final int size = 32;

    }

    public enum Encryption
    {
        ECIES_Nist_P256,
        ECIES_BRAINPOOL_P256R1

    }

    public enum Hash
    {
        SHA_256
    }
    private Symmetric symmetric;
    private Signature signature;
    private Encryption encryption;
    private Hash hash;

    public Algorithm(Symmetric symmetric, Signature signature,
                     Encryption encryption, Hash hash)
    {
        this.symmetric = symmetric;
        this.signature = signature;
        this.encryption = encryption;
        this.hash = hash;
    }

    /**
     * @return the related symmetric algorithm
     */
    public Symmetric getSymmetric()
    {
        return symmetric;
    }

    /**
     * @return the related signature algorithm
     */
    public Signature getSignature()
    {
        return signature;
    }

    /**
     * @return the related encryption algorithm
     */
    public Encryption getEncryption()
    {
        return encryption;
    }

    /**
     * @return the related hash algorithm
     */
    public Hash getHash()
    {
        return hash;
    }

}
