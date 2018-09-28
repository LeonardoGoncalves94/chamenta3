package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERNull;
import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.*;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;

/**
 *  this class represents the certificate's to be signed data according to the ETSI 103 097 standard
 */
public class ToBeSignedCertificate extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 12;

    protected static final int ID = 0;
    protected static final int CARCA_ID = 1;
    protected static final int CRL_SERIES = 2;
    protected static final int VALIDITY_PERIOD = 3;
    protected static final int REGION = 4;
    protected static final int ASSURANCE_LEVEL = 5;
    protected static final int APP_PERMISSIONS = 6;
    protected static final int CERT_ISSUE_PERMISSIONS = 7;
    protected static final int CERT_REQUEST_PERMISSIONS = 8; // this component is always absent
    protected static final int CAN_REQUEST_ROLLOVER = 9; // this component is always absent
    protected static final int ECRYPTION_KEY = 10;
    protected static final int VERIFY_KEY_INDICATOR = 11;


    /**
     * Constructor used when encoding
     */
    public ToBeSignedCertificate(CertificateId id,
                                 ValidityPeriod validityPeriod, GeographicRegion region, SubjectAssurance assuranceLevel,
                                 SequenceOfPsidSsp appPermissions, SequenceOfPsidGroupPermissions certIssuePermissions,
                                 PublicEncryptionKey encryptionKey, VerificationKeyIndicator verifyKeyIndicator) throws IOException{
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(ID, id);
        setComponentValue(CARCA_ID, new HashedId3(Hex.decode("000000")));
        setComponentValue(CRL_SERIES,  new CrlSeries(0));
        setComponentValue(VALIDITY_PERIOD, validityPeriod);
        setComponentValue(REGION, region);
        setComponentValue(ASSURANCE_LEVEL, assuranceLevel);
        setComponentValue(APP_PERMISSIONS, appPermissions);
        setComponentValue(CERT_ISSUE_PERMISSIONS, certIssuePermissions);
        setComponentValue(CERT_REQUEST_PERMISSIONS, null); //ABSENT
        setComponentValue(CAN_REQUEST_ROLLOVER, null); //ABSENT
        setComponentValue(ECRYPTION_KEY, encryptionKey);
        setComponentValue(VERIFY_KEY_INDICATOR, verifyKeyIndicator);
    }


    /**
     * Constructor used when decoding
     */
    public ToBeSignedCertificate()throws IOException
    {
        super(12);
        createSequence();
    }

    private void createSequence() throws IOException
    {
        addComponent(ID, false, new CertificateId(), null);
        addComponent(CARCA_ID, false, new HashedId3(), null);
        addComponent(CRL_SERIES, false, new CrlSeries(), null);
        addComponent(VALIDITY_PERIOD, false, new ValidityPeriod(), null);
        addComponent(REGION, true, new GeographicRegion(), null);
        addComponent(ASSURANCE_LEVEL, true, new SubjectAssurance(), null);
        addComponent(APP_PERMISSIONS, true, new SequenceOfPsidSsp(), null);
        addComponent(CERT_ISSUE_PERMISSIONS, true, new SequenceOfPsidGroupPermissions(), null);
        addComponent(CERT_REQUEST_PERMISSIONS, true, new SequenceOfPsidGroupPermissions(), null);
        addComponent(CAN_REQUEST_ROLLOVER, true, new COERNull(), null);
        addComponent(ECRYPTION_KEY, true, new PublicEncryptionKey(), null);
        addComponent(VERIFY_KEY_INDICATOR, false, new VerificationKeyIndicator(), null);

    }

    /**
     * Encodes the ToBeSignedCertificate in a byte array (to be used in the signature process).
     *
     * @return return the encoded ToBeSignedCertificate as a byte[]
     * @throws IOException if encoding problems of the data occurred.
     */
    public byte[] getEncoded() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        encode(dos);
        return baos.toByteArray();
    }

    /**
     * @return the id, required
     */
    public CertificateId getId()
    {
        return (CertificateId) getComponentValue(ID);
    }

    /**
     * @return the cracaId, required
     */
    public HashedId3 getCracaId()
    {
        return (HashedId3) getComponentValue(CARCA_ID);
    }

    /**
     * @return the crlSeries, required
     */
    public CrlSeries getCrlSeries()
    {
        return (CrlSeries) getComponentValue(CRL_SERIES);
    }

    /**
     * @return the validityPeriod, required
     */
    public ValidityPeriod getValidityPeriod()
    {
        return (ValidityPeriod) getComponentValue(VALIDITY_PERIOD);
    }

    /**
     * @return the region, optional
     */
    public GeographicRegion getRegion()
    {
        return (GeographicRegion) getComponentValue(REGION);
    }

    /**
     * @return the assuranceLevel, optional
     */
    public SubjectAssurance getAssuranceLevel()
    {
        return (SubjectAssurance) getComponentValue(ASSURANCE_LEVEL);
    }

    /**
     * @return the appPermissions, optional
     */
    public SequenceOfPsidSsp getAppPermissions()
    {
        return (SequenceOfPsidSsp) getComponentValue(APP_PERMISSIONS);
    }

    /**
     * @return the certIssuePermissions, optional
     */
    public SequenceOfPsidGroupPermissions getCertIssuePermissions()
    {
        return (SequenceOfPsidGroupPermissions) getComponentValue(CERT_ISSUE_PERMISSIONS);
    }

    /**
     * @return the encryptionKey, optional
     */
    public PublicEncryptionKey getEncryptionKey()
    {
        return (PublicEncryptionKey) getComponentValue(ECRYPTION_KEY);
    }

    /**
     * @return the verifyKeyIndicator, required
     */
    public VerificationKeyIndicator getVerifyKeyIndicator()
    {
        return (VerificationKeyIndicator) getComponentValue(VERIFY_KEY_INDICATOR);
    }


    @Override
    public String toString() {
        return
                "ToBeSignedCertificate [\n" +
                        "  id=" + getId().toString().replaceAll("CertificateId ", "") + "\n" +
                        "  cracaId=" + getCracaId().toString().replaceAll("HashedId3 ", "") + "\n" +
                        "  crlSeries=" + getCrlSeries().toString().replaceAll("CrlSeries ", "") + "\n" +
                        "  validityPeriod=" + getValidityPeriod().toString().replaceAll("ValidityPeriod ", "") + "\n" +
                        "  region=" + ( getRegion() != null ? getRegion().toString().replaceAll("GeographicRegion ", "") : "NONE") + "\n" +
                        "  assuranceLevel=" + ( getAssuranceLevel() != null ? getAssuranceLevel().toString().replaceAll("SubjectAssurance ", "") : "NONE") + "\n" +
                        "  appPermissions=" + ( getAppPermissions() != null ? getAppPermissions().toString().replaceAll("SequenceOfPsidSsp ", "") : "NONE") + "\n" +
                        "  certIssuePermissions=" + ( getCertIssuePermissions() != null ? getCertIssuePermissions().toString().replaceAll("SequenceOfPsidGroupPermissions ", "") : "NONE") + "\n" +
                        "  encryptionKey=" + ( getEncryptionKey() != null ? getEncryptionKey().toString().replaceAll("PublicEncryptionKey ", "") : "NONE") + "\n" +
                        "  verifyKeyIndicator=" + getVerifyKeyIndicator().toString().replace("VerificationKeyIndicator ", "") + "\n" +
                        "]";

    }

}
