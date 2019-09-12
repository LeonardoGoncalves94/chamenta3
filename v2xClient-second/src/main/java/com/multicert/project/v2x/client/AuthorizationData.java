package com.multicert.project.v2x.client;

import java.security.KeyPair;
import java.util.Base64;

import org.bouncycastle.util.encoders.Hex;

/**
 * This class stores a pair of authorization ticket and its signature keys
 */
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;

public class AuthorizationData {

	private EtsiTs103097Certificate authorizationTicket = null;
	private KeyPair signatureKeys = null;
			
	public AuthorizationData(EtsiTs103097Certificate authorizationTicket,KeyPair signatureKeys)
	{
		this.authorizationTicket = authorizationTicket;
		this.signatureKeys = signatureKeys;
	}

	public EtsiTs103097Certificate getAuthorizationTicket() {
		return authorizationTicket;
	}

	public void setAuthorizationTicket(EtsiTs103097Certificate authorizationTicket) {
		this.authorizationTicket = authorizationTicket;
	}

	public KeyPair getSignatureKeys() {
		return signatureKeys;
	}

	public void setSignatureKeys(KeyPair signatureKeys) {
		this.signatureKeys = signatureKeys;
	}

	@Override
	public String toString() {
		return  "AuthorizationData [\n" +
                "  authorizationTicket=" + authorizationTicket + "\n" +
                "  PublicKey=" + encodeHex(signatureKeys.getPublic().getEncoded()) + "\n" +	
                "]";
	}
	
	/**
	 * Help method that encodes bytes into string
	 * @param bytes
	 * @return
	 */
	private String encodeHex(byte[] bytes)
	{
		return Hex.toHexString(bytes);
	}
	
	/**
	 * Help method that encodes bytes into string
	 * @param bytes
	 * @return
	 */
	private byte[] decodeHex(String string)
	{
		return Hex.decode(string);
	}
	
	
	
}
