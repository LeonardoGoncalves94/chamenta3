package com.multicert.project.v2x.pkimanager.model;

/**
 * This class represents the response of a CA to the RA, containing the response code and the encoded response (Enrollment or Authorization)
 *
 */
public class CAresponse {
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public byte[] getEncodedResponse() {
		return encodedResponse;
	}

	public void setEncodedResponse(byte[] encodedResponse) {
		this.encodedResponse = encodedResponse;
	}

	private boolean isSuccess;
	
	private byte[] encodedResponse;
			
	public CAresponse(boolean isSuccess, byte[] encodedResponse) {
		this.isSuccess = isSuccess;
		this.encodedResponse = encodedResponse;
	}


}
