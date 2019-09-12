package com.multicert.project.v2x.pkimanager.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
/**
 * This table stores the response to vehicle enrollment
 * The response contains the enrollment certificate
 */
@Entity
@Table(name = "response")
public class Response {
	
	/**
	 * Constructor for the response
	 * @param responseOrigin the name of the EA 
	 * @param responseDestination the name of the vehicle
	 * @param responseMessage a message for the vehicle that summarizes why the EA was not able to build an encoded enrollment response
	 * @param responseEncoded if present contains the enrollment response for the vehicle, encoded Etsi103097Data
	 * @param isSuccess, true if the responseEcoded is present
	 */
	public Response(String responseOrigin, String responseDestination, String responseMessage, String responseEncoded, Boolean isSuccess) {
		super();
		this.responseOrigin = responseOrigin;
		this.responseDestination = responseDestination;
		this.responseMessage = responseMessage;
		this.responseEncoded = responseEncoded;
		this.isSuccess = isSuccess;

	}
	
	public Response() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "res_id")
	private Long responseId;
	
	@Column(name = "res_origin")
	@NotEmpty(message = "*Please provide a valid origin")
	private String responseOrigin;
	
	@Column(name = "res_destination")
	@NotEmpty(message = "*Please provide a valid destination")
	private String responseDestination;	
	
	@Column(name = "req_encoded")
	private String responseEncoded;
	
	@Column(name = "res_message")
	@NotEmpty(message = "*Please provide a response messge")
	private String responseMessage;
	
	@Column(name = "res_succsess")
	private Boolean isSuccess;

	public Long getRequestId() {
		return responseId;
	}

	public void setRequestId(Long requestId) {
		this.responseId = requestId;
	}

	public String getRequestEncoded() {
		return responseEncoded;
	}

	public void setRequestEncoded(String requestEncoded) {
		this.responseEncoded = requestEncoded;
	}

	public String getRequestOrigin() {
		return responseOrigin;
	}

	public void setRequestOrigin(String requestOrigin) {
		this.responseOrigin = requestOrigin;
	}

	public String getRequestDestination() {
		return responseDestination;
	}

	public void setRequestDestination(String requestDestination) {
		this.responseDestination = requestDestination;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}

	public String getResponseOrigin() {
		return responseOrigin;
	}

	public void setResponseOrigin(String responseOrigin) {
		this.responseOrigin = responseOrigin;
	}

	public String getResponseDestination() {
		return responseDestination;
	}

	public void setResponseDestination(String responseDestination) {
		this.responseDestination = responseDestination;
	}

	public String getResponseEncoded() {
		return responseEncoded;
	}

	public void setResponseEncoded(String responseEncoded) {
		this.responseEncoded = responseEncoded;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	



}
