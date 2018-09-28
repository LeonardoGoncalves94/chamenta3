package com.multicert.project.v2x.pkimanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "keys")
public class Key {
	/**
	 * Constructor to be used when creating new key
	 */
	public Key(String alias, String algorithm, CA ca, String keyType) {
		super();
		this.alias = alias;
		this.algorithm = algorithm;
		this.ca = ca;
		this.keyType = keyType;
	}
	
	
	public Key() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "key_id")
	private Long keyId;
	
	@Column(name = "alias")
	@NotEmpty(message = "*Please provide an unique key alias")
	private String alias;
	
	@Column(name = "type")
	@NotEmpty(message = "*Please specifiy the key algorithm")
	private String keyType;
	
	@Column(name = "algorithm")
	@NotEmpty(message = "*Please specifiy the key algorithm")
	private String algorithm;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="ca_id", nullable=false)
	private CA ca;
	
	public String getAlias() {
		return this.alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public Long getKeyId() {
		return this.keyId;
	}
	
	public void setKeyId(Long id) {
		this.keyId = id;
	}
	
	public CA getCa() {
		return ca;
	}

	public void setCa(CA ca) {
		this.ca = ca;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}


	public String getKeyType() {
		return keyType;
	}


	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

}
