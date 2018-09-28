package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.datastructures.base.HashedId8;
import org.bouncycastle.util.encoders.Hex;

/**
 * This class is used to indicate a pre shared symmetric key (HashedId8) that may be used to decrypt a SymmetricCiphertext.
 *
 */
public class PreSharedKeyRecipientInfo extends HashedId8
{
    /**
     * Constructor used when encoding
     * @param recipientInfo The hashed symmetric key
     */
    public PreSharedKeyRecipientInfo(byte[] recipientInfo)
    {
        super(recipientInfo);
    }
	
	/**
	 * Constructor used when decoding
	 */
	public PreSharedKeyRecipientInfo(){
	}
}
