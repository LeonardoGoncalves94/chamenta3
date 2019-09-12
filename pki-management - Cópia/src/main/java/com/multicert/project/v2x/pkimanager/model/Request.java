package com.multicert.project.v2x.pkimanager.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "request")
public class Request {
	
	public Request(String requestOrigin, String requestDestination, String requestEncoded) {
		super();
		this.requestOrigin = requestOrigin;
		this.requestDestination = requestDestination;
		this.requestEncoded = requestEncoded;

	}
	
	public Request() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "req_id")
	private Long requestId;
	
	@Column(name = "req_origin")
	@NotEmpty(message = "*Please provide a valid origin")
	private String requestOrigin;
	
	@Column(name = "req_destination")
	@NotEmpty(message = "*Please provide a valid destination")
	private String requestDestination;
	
	
	@Column(name = "req_encoded")
	@NotEmpty(message = "*Please provide encoded request")
	private String requestEncoded;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getRequestEncoded() {
		return requestEncoded;
	}

	public void setRequestEncoded(String requestEncoded) {
		this.requestEncoded = requestEncoded;
	}

	public String getRequestOrigin() {
		return requestOrigin;
	}

	public void setRequestOrigin(String requestOrigin) {
		this.requestOrigin = requestOrigin;
	}

	public String getRequestDestination() {
		return requestDestination;
	}

	public void setRequestDestination(String requestDestination) {
		this.requestDestination = requestDestination;
	}
	



}
