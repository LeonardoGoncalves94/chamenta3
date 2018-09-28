package com.multicert.v2x.datastructures.message.encrypteddata;


import com.multicert.v2x.asn1.coer.COEROctetString;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.Opaque;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;

/**
 * This data structure contains a ciphertext for the AES-CCM symmetric algorithm.
 *
 * The plaintext resulting from the correct decryption of the ciphertext is a COER-encoded EtsiTs103097Data structure.
 */
public class AesCcmCiphertext extends COERSequence
{
	private static final int SEQUENCE_SIZE = 2;
	
	private static final int NOUNCE = 0;
	private static final int CCMCIPHERTEXT = 1;

	/**
	 * Constructor used when encoding
	 */
	public AesCcmCiphertext(byte[] nounce, byte[] ccmCipherText) throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
		if(nounce == null){
			throw new IllegalArgumentException("Error encoding aes-ccm ciphertext: nounce cannot be null in AesCcmCiphertext");
		}
		if(ccmCipherText == null){
			throw new IllegalArgumentException("Error encoding aes-ccm ciphertext: ciphertext cannot be null in AesCcmCiphertext");
		}
		setComponentValue(NOUNCE, new COEROctetString(nounce,12,12));
        setComponentValue(CCMCIPHERTEXT, new Opaque(ccmCipherText));

	}

	/**
	 * Constructor used when decoding
	 */
	public AesCcmCiphertext()
    {
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * 
	 * @return nounce
	 */
	public byte[] getNounce()
    {
		return ((COEROctetString) getComponentValue(NOUNCE)).getData();
	}
	
	/**
	 * 
	 * @return ccmCipherText
	 */
	public byte[] getCcmCipherText()
    {
		return ((Opaque) getComponentValue(CCMCIPHERTEXT)).getData();
	}
	

	private void createSequence()
    {
		addComponent(NOUNCE, false, new COEROctetString(12,12), null);
		addComponent(CCMCIPHERTEXT, false, new Opaque(), null);
	}

	@Override
	public String toString() {
		return "AesCcmCiphertext [nounce=" + new String(Hex.encode(getNounce())) + ", ccmCipherText=" + new String(Hex.encode(getCcmCipherText())) + "]";
	}
}
