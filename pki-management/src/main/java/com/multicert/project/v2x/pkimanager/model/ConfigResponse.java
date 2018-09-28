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
 * This table stores a response to a vehicle configuration request
 * The response contains information that will allow the vehicle to request enrollment/authorization certificates and validate incoming v2x messages.
 * In a real case such information should be established in the vehicle using a physically secure process
 *
 */
@Entity
@Table(name = "config_response")
public class ConfigResponse {
	/**
	 * Main constructor for the response
	 * @param responseOrigin, the name of the RA that constructs this response
	 * @param responseDestination, the name of the destination vehicle (itsId) 
	 * @param isSuccess, flags the configuration of the vehicle as successful or failed
	 * @param responseMessage, a message that summarizes the result of the configuration
	 * @param trustAnchor, the certificate of the root CA that is the trust anchor for the sub CA certificates
	 * @param trustedAA, the certificates that of all trusted Authorization Authorities (will be used by the vehicle to verify incoming v2x messages)
	 * @param EAcert, the certificate of the Enrollment Authority that will enroll the destination vehicle
	 * @param AAcert, the certificate of the Authorization Authority that will issue Authorization Tickets for the destination vehicle
	 */
	public ConfigResponse(String responseOrigin, String responseDestination, Boolean isSuccess, String responseMessage, String trustAnchor, String trustedAA, String eaCert, String aaCert) {
		super();
		this.responseOrigin = responseOrigin;
		this.responseDestination = responseDestination;
		this.isSuccess = isSuccess;
		this.responseMessage = responseMessage;
		this.trustAnchor = trustAnchor;
		this.trustedAA = trustedAA;
		this.eaCert = eaCert;
		this.aaCert = aaCert;
	}
	
	public ConfigResponse() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "conf_res_id")
	private Long responseId;
	
	@Column(name = "conf_res_origin")
	@NotEmpty(message = "*Please provide a valid origin")
	private String responseOrigin;
	
	@Column(name = "conf_res_destination")
	@NotEmpty(message = "*Please provide a valid destination")
	private String responseDestination;	
	
	@Column(name = "conf_res_trustAnchor")
	private String trustAnchor;
	
	@Column(name = "conf_res_trustedAA")
	private String trustedAA;
	
	@Column(name = "conf_res_EAcert")
	private String eaCert;
	
	@Column(name = "conf_res_AAcert")
	private String aaCert;
	
	@Column(name = "conf_res_message")
	@NotEmpty(message = "*Please provide a response messge")
	private String responseMessage;
	
	@Column(name = "conf_res_succsess")
	private Boolean isSuccess;

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

	public String getTrustAnchor() {
		return trustAnchor;
	}

	public void setTrustAnchor(String trustAnchor) {
		this.trustAnchor = trustAnchor;
	}

	public String getTrustedAA() {
		return trustedAA;
	}

	public void setTrustedAA(String trustedAA) {
		this.trustedAA = trustedAA;
	}

	public String getEaCert() {
		return eaCert;
	}

	public void setEaCert(String eaCert) {
		this.eaCert = eaCert;
	}

	public String getAaCert() {
		return aaCert;
	}

	public void setAaCert(String aaCert) {
		this.aaCert = aaCert;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	


}
