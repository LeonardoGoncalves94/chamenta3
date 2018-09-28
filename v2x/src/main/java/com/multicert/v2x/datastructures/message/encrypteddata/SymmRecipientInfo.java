package com.multicert.v2x.datastructures.message.encrypteddata;

import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.HashedId8;

import java.io.IOException;


/**
 * This class encodes the hash of the symmetric encryption key, which may be used to decrypt the data encryption key (recipientId).
 * In addition, it also constrains the encrypted data encryption key itself (encKey).
 *
 */
public class SymmRecipientInfo extends COERSequence
{
	

	private static final int SEQUENCE_SIZE = 2;
	
	private static final int RECIPIENT_ID = 0;
	private static final int ENC_KEY = 1;

    /**
     * Constructor used when encoding
     */
    public SymmRecipientInfo(HashedId8 recipientId, SymmetricCiphertext encKey) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(RECIPIENT_ID, recipientId);
        setComponentValue(ENC_KEY, encKey);
    }

	/**
	 * Constructor used when decoding
	 */
	public SymmRecipientInfo(){
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * 
	 * @return recipientId
	 */
	public HashedId8 getRecipientId()
	{
		return (HashedId8) getComponentValue(RECIPIENT_ID);
	}
	
	/**
	 * 
	 * @return encKey
	 */
	public SymmetricCiphertext getEncKey()
	{
		return (SymmetricCiphertext) getComponentValue(ENC_KEY);
	}

	private void createSequence(){
		addComponent(RECIPIENT_ID, false, new HashedId8(), null);
		addComponent(ENC_KEY, false, new SymmetricCiphertext(), null);
	}
}
