package com.multicert.project.v2x.pkimanager.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This table stores the enrollment credentials that an Enrollment Authority issued for a certain vehicle
 *
 */
@Entity
@Table(name = "enrollment_credential")
public class EnrollmentCredential {
	
	/**
	 * Constructor used when issuing an enrollment credential
	 */
	public EnrollmentCredential(byte[] hashedId, CA Ea, String subject, Boolean revoked, byte[] encoded) {
		super();
		this.hashedId = hashedId;
		this.issuer = Ea;
		this.subject = subject;
		this.revoked = revoked;
		this.encoded = encoded;
	}
	
	
	public EnrollmentCredential() {
		
	}

	@Id
	@Column(name="credential_id")
	private byte[] hashedId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="credential_issuer", nullable=false)
	private CA issuer;
	
	@Column(name = "credential_subject")
	private String subject;

	
	@Column(name = "revoked")
	private Boolean revoked;
	
	@Column(name = "credential_encoded")
	private byte[] encoded;

	public CA getIssuer() {
		return issuer;
	}


	public void setIssuer(CA Ea) {
		this.issuer = Ea;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Boolean getrevoked() {
		return revoked;
	}


	public void setrevoked(Boolean revoked) {
		this.revoked = revoked;
	}


	public byte[] getEncoded() {
		return encoded;
	}


	public void setEncoded(byte[] encoded) {
		this.encoded = encoded;
	}


	public byte[] getHashedId() {
		return hashedId;
	}


	public void setHashedId(byte[] hashedId) {
		this.hashedId = hashedId;
	}



}
