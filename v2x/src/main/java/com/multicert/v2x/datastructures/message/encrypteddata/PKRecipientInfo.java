package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.HashedId8;

import java.io.IOException;

/**
 * This data structure contains the hash of the container for the encryption public key (recipientId) and the encrypted public key itself (encKey)
 *
 */
public class PKRecipientInfo extends COERSequence
{
	private static final int SEQUENCE_VALUES = 2;
	
	private static final int RECIPIENTID = 0;
	private static final int ENCKEY = 1;

	/**
	 * Constructor used when decoding
	 */
	public PKRecipientInfo(){
		super(SEQUENCE_VALUES);
		createSequence();
	}
	
	/**
	 * Constructor used when encoding
	 */
	public PKRecipientInfo(HashedId8 recipientId, EncryptedDataEncryptionKey encKey) throws IOException
    {
		super(SEQUENCE_VALUES);
		createSequence();
		setComponentValue(RECIPIENTID, recipientId);
		setComponentValue(ENCKEY, encKey);
	
	}

	/**
	 * 
	 * @return recipientId
	 */
	public HashedId8 getRecipientId()
    {
		return (HashedId8) getComponentValue(RECIPIENTID);
	}
	
	/**
	 * 
	 * @return encKey
	 */
	public EncryptedDataEncryptionKey getEncKey()
    {
		return (EncryptedDataEncryptionKey) getComponentValue(ENCKEY);
	}
	

	
	private void createSequence()
    {
		addComponent(RECIPIENTID, false, new HashedId8(), null);
		addComponent(ENCKEY, false, new EncryptedDataEncryptionKey(), null);
	}

	@Override
	public String toString() {
		return "PKRecipientInfo [recipientId=" + getRecipientId().toString().replace("HashedId8 ", "") + ", encKey=" + getEncKey().toString().replace("EncryptedDataEncryptionKey ", "") + "]";
	}

}
