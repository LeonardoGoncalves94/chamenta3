package com.multicert.project.v2x.pkimanager.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "ca")
public class CA {
	
	public CA(String caName, String caCountry, String caType, String group) {
		super();
		this.caName = caName;
		this.caCountry = caCountry;
		this.caType = caType;
		this.caGroup = group;
	}
	
	public CA() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ca_id")
	private Long caId;
	
	@Column(name = "ca_name")
	@Length(min = 2, max = 20)
	@NotEmpty(message = "*Please provide an unique CA name")
	private String caName;	
	
	@Column(name = "ca_country")
	@NotEmpty(message = "*Please provide the CA Country location")
	private String caCountry;
	
	@Column(name = "ca_type")
	@NotEmpty(message = "*Please select the type of CA")
	private String caType;
	
	@Column(name = "ca_group")
	@NotEmpty(message = "*Please select the type of CA")
	private String caGroup;
	
	@OneToMany(mappedBy = "issuer")
	private List<Certificate> certificates;
	
	@OneToMany(mappedBy = "issuer")
	private List<EnrollmentCredential> enrollmentCerdentials;
	
	@OneToMany(mappedBy = "ca", cascade = CascadeType.ALL, 
            fetch = FetchType.LAZY)
	private List<Key> keys;
	
	@OneToOne(mappedBy = "subject", cascade = CascadeType.ALL, 
            fetch = FetchType.LAZY)
	private Certificate certificate;

	public String getCaCountry() {
		return caCountry;
	}

	public void setCaCountry(String caCountry) {
		this.caCountry = caCountry;
	}

	public List<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<Certificate> certificates) {
		this.certificates = certificates;
	}

	public Long getCaId() {
		return caId;
	}

	public void setCaId(Long caId) {
		this.caId = caId;
	}

	public String getCaType() {
		return caType;
	}

	public void setCaType(String caType) {
		this.caType = caType;
	}

	public List<Key> getKeys() {
		return keys;
	}

	public void setKeys(List<Key> keys) {
		this.keys = keys;
	}

	public String getCaName() {
		return caName;
	}

	public void setCaName(String caName) {
		this.caName = caName;
	}

	public String getGroup() {
		return caGroup;
	}

	public void setGroup(String group) {
		this.caGroup = group;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public String getCaGroup() {
		return caGroup;
	}

	public void setCaGroup(String caGroup) {
		this.caGroup = caGroup;
	}
	
	/**
	 * Help method that searches the key list and returns the signature key database object 
	 * signature key is required,return null if not yet specified
	 */
	public Key getSignatureKey() {
		for(Key k : keys)
		{
			if(k.getKeyType().equals("Signature") ) {
			 return k;		 
			}		
		}
		return null;
	}
	
	/**
	 * Help method that searches the key list of a CA and returns the encryption key database object
	 * encryption key is optional, return null if no encryption key is found)
	 */
	public Key getEncryptionKey() {
		for(Key k : keys)
		{
			if(k.getKeyType().equals("Encryption")) {
			 return k;		 
			}		
		}
		return null;
	}

}
