package com.multicert.project.v2x.client;

import java.security.KeyPair;
import java.util.Base64;

import org.bouncycastle.util.encoders.Hex;

import com.multicert.v2x.datastructures.base.HashedId3;
/**
 * This class stores a pair of authorization ticket and the CAM signed by it
 */
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;
import com.multicert.v2x.datastructures.message.secureddata.EtsiTs103097Data;

public class UnknownAuthorizationData {

	private HashedId3 authorizationTicket = null;
	private EtsiTs103097Data cam = null;
			
	public UnknownAuthorizationData(HashedId3 authorizationTicket, EtsiTs103097Data cam)
	{
		this.authorizationTicket = authorizationTicket;
		this.cam = cam;
	}
	
	
	public HashedId3 getAuthorizationTicket() {
		return authorizationTicket;
	}



	public void setAuthorizationTicket(HashedId3 authorizationTicket) {
		this.authorizationTicket = authorizationTicket;
	}

	public byte[] getAuthorizationTicketAsBytes() {
		return authorizationTicket.getData();
	}
	
	public String getAuthorizationTicketAsString() {
		return encodeHex(authorizationTicket.getData());
	}

	public EtsiTs103097Data getCam() {
		return cam;
	}



	public void setCam(EtsiTs103097Data cam) {
		this.cam = cam;
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
