package com.multicert.v2x.datastructures.message.encrypteddata;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

public class EncryptedData extends COERSequence
{

	private static final int SEQUENCE_SIZE = 2;

	private static final int RECIPIENTS = 0;
	private static final int CIPHERTEXT = 1;

	/**
	 * Constructor used when decoding
	 */
	public EncryptedData(){
		super(SEQUENCE_SIZE);
		createSequence();
	}
	
	/**
	 * Constructor used when encoding
	 */
	public EncryptedData(SequenceOfRecipientInfo recipients, SymmetricCiphertext ciphertext) throws IOException
	{
		super(SEQUENCE_SIZE);
        createSequence();

		setComponentValue(RECIPIENTS, recipients);
		setComponentValue(CIPHERTEXT, ciphertext);
	}

	/**
	 * 
	 * @return recipients
	 */
	public SequenceOfRecipientInfo getRecipients(){
		return (SequenceOfRecipientInfo) getComponentValue(RECIPIENTS);
	}
	
	/**
	 * 
	 * @return ciphertext
	 */
	public SymmetricCiphertext getCipherText(){
		return (SymmetricCiphertext) getComponentValue(CIPHERTEXT);
	}
	

	
	private void createSequence(){
		addComponent(RECIPIENTS, false, new SequenceOfRecipientInfo(), null);
		addComponent(CIPHERTEXT, false, new SymmetricCiphertext(), null);
	}

	@Override
	public String toString() {
		return "EncryptedData [\n" +
				"  recipients=" + getRecipients().toString().replace("SequenceOfRecipientInfo ", "") + ",\n" +
				"  ciphertext=" + getCipherText().toString().replace("SymmetricCiphertext ", "") + "\n]";
	}

}
