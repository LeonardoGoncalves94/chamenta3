package com.multicert.v2x.datastructures.message.secureddata;

import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.*;
import com.multicert.v2x.datastructures.certificate.EtsiTs103097Certificate;


import java.io.IOException;

public class HeaderInfo extends COERSequence
{

	private static final int SEQUENCE_SIZE = 9;
	
	private static final int PSID = 0;
	private static final int GENERATION_TIME = 1;
	private static final int EXPIRY_TIME = 2;
	private static final int GENERATION_LOCATION = 3;
	private static final int P2PCD_LEARNING_REQUEST = 4; //always ABSENT in ETSI TS 103 097
	private static final int MISSING_CRL_IDENTIFIER = 5; //always ABSENT in ETSI TS 103 097
	private static final int ENCRYPTION_KEY = 6;
	private static final int INLINE_P2PCD_REQUEST = 7;
	private static final int REQUESTED_CERTIFICATE = 8;

	/**
	 * Constructor used when encoding
	 */
	public HeaderInfo(Psid psid, Time64 generationTime, Time64 expiryTime, ThreeDLocation generationLocation, EncryptionKey encryptionKey, SequenceOfHashedId3 inlineP2pcdRequest, EtsiTs103097Certificate requestedCertificate) throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
		setComponentValue(PSID, psid);
		setComponentValue(GENERATION_TIME, generationTime);
		setComponentValue(EXPIRY_TIME, expiryTime);
		setComponentValue(GENERATION_LOCATION, generationLocation);
		setComponentValue(P2PCD_LEARNING_REQUEST, null); //ABSENT
		setComponentValue(MISSING_CRL_IDENTIFIER, null); //ABSENT
		setComponentValue(ENCRYPTION_KEY, encryptionKey);
		setComponentValue(INLINE_P2PCD_REQUEST, inlineP2pcdRequest);
		setComponentValue(REQUESTED_CERTIFICATE,requestedCertificate);
	}

	/**
	 * Constructor used when decoding
	 */
	public HeaderInfo() throws IOException
	{
		super(SEQUENCE_SIZE);
		createSequence();
	}

	/**
	 * 
	 * @return psid, required
	 */
	public Psid getPsid()
    {
		return (Psid) getComponentValue(PSID);
	}
	
	/**
	 * 
	 * @return generationTime, optional, null if not set
	 */
	public Time64 getGenerationTime()
    {
		return (Time64) getComponentValue(GENERATION_TIME);
	}
	
	/**
	 * 
	 * @return expiryTime, optional, null if not set
	 */
	public Time64 getExpiryTime()
    {
		return (Time64) getComponentValue(EXPIRY_TIME);
	}
	
	/**
	 * 
	 * @return generationLocation, optional, null if not set
	 */
	public ThreeDLocation getGenerationLocation()
    {
		return (ThreeDLocation) getComponentValue(GENERATION_LOCATION);
	}
	
	/**
	 * 
	 * @return encryptionKey, optional, null if not set
	 */
	public EncryptionKey getEncryptionKey()
    {
		return (EncryptionKey) getComponentValue(ENCRYPTION_KEY);
	}

	public SequenceOfHashedId3 getInlineP2pcdRequest()
	{
		return (SequenceOfHashedId3) getComponentValue(INLINE_P2PCD_REQUEST);
	}

	public EtsiTs103097Certificate getRequestedCertificate()
	{
		return (EtsiTs103097Certificate) getComponentValue(REQUESTED_CERTIFICATE);
	}


	private void createSequence() throws IOException
	{
		addComponent(PSID, false, new Psid(), null);
		addComponent(GENERATION_TIME, true, new Time64(), null);
		addComponent(EXPIRY_TIME, true, new Time64(), null);
		addComponent(GENERATION_LOCATION, true, new ThreeDLocation(), null);
		addComponent(P2PCD_LEARNING_REQUEST, true, new HashedId3(), null);
		addComponent(MISSING_CRL_IDENTIFIER, true, new MissingCrlIdentifier(), null);
		addComponent(ENCRYPTION_KEY, true, new EncryptionKey(), null);
		addComponent(INLINE_P2PCD_REQUEST, true, new SequenceOfHashedId3(), null);
		addComponent(REQUESTED_CERTIFICATE, true, new EtsiTs103097Certificate(), null);
	}

	@Override
	public String toString() {
		String retval = "HeaderInfo [\n"+
				"  psid=" + getPsid().toString().replace("Psid ", "") +  ",\n"+
				(getGenerationTime() != null ? "  generationTime=" + getGenerationTime().toString().replace("Time64 ", "")   +  ",\n" : "") +
				(getExpiryTime() != null ? "  expiryTime=" + getExpiryTime().toString().replace("Time64 ", "")   +  ",\n" : "") +
				(getGenerationLocation() != null ? "  generationLocation=" + getGenerationLocation().toString().replace("ThreeDLocation ", "")   +  ",\n" : "")+
				(getEncryptionKey() != null ? "  encryptionKey=" + getEncryptionKey().toString().replace("EncryptionKey ", "")   +  "\n" : "")+
				(getInlineP2pcdRequest() != null ? "  InlineP2pcdRequest=" + getInlineP2pcdRequest().toString().replace("COERSequenceOf ", "")   +  "\n" : "")+
				(getRequestedCertificate() != null ? "  requestedCertificate=" + getRequestedCertificate().toString().replace("Certificate ", "")   +  "\n" : "")+
				"]";
		if(retval.endsWith(",\n]")){
			retval = retval.substring(0, retval.length()-3) + "\n]";
		}
		return retval;
	}

}
