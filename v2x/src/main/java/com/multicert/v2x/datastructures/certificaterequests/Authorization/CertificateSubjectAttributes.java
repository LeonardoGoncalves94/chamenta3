package com.multicert.v2x.datastructures.certificaterequests.Authorization;

import com.multicert.v2x.asn1.coer.COERSequence;
import com.multicert.v2x.datastructures.base.GeographicRegion;
import com.multicert.v2x.datastructures.base.SubjectAssurance;
import com.multicert.v2x.datastructures.base.ValidityPeriod;
import com.multicert.v2x.datastructures.certificate.CertificateId;
import com.multicert.v2x.datastructures.certificate.SequenceOfPsidGroupPermissions;
import com.multicert.v2x.datastructures.base.SequenceOfPsidSsp;

import java.io.IOException;


public class CertificateSubjectAttributes extends COERSequence
{
    protected static final int SEQUENCE_SIZE = 6;

    protected static final int ID = 0;
    protected static final int VALIDITY_PERIOD = 1;
    protected static final int REGION = 2;
    protected static final int ASSURANCE_LEVEL = 3;
    protected static final int APP_PERMISSIONS = 4;
    protected static final int CERT_ISSUE_PERMISSIONS = 5;

    /**
     * Constructor used when encoding (populates a sequence)
     */
    public CertificateSubjectAttributes(CertificateId id, ValidityPeriod validity, GeographicRegion region, SubjectAssurance assurance, SequenceOfPsidSsp app, SequenceOfPsidGroupPermissions issue) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(ID, id);
        setComponentValue(VALIDITY_PERIOD, validity);
        setComponentValue(REGION, region);
        setComponentValue(ASSURANCE_LEVEL, assurance);
        setComponentValue(APP_PERMISSIONS, app);
        setComponentValue(CERT_ISSUE_PERMISSIONS, issue);
    }

    /**
     * Constructor used when decoding (creates empty sequence)
     */
    public CertificateSubjectAttributes() throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public void createSequence() throws IOException
    {
        addComponent(ID, true, new CertificateId(), null);
        addComponent(VALIDITY_PERIOD, true, new ValidityPeriod(), null);
        addComponent(REGION, true, new GeographicRegion(), null);
        addComponent(ASSURANCE_LEVEL, true, new SubjectAssurance(), null);
        addComponent(APP_PERMISSIONS, true, new SequenceOfPsidSsp(), null);
        addComponent(CERT_ISSUE_PERMISSIONS, true, new SequenceOfPsidGroupPermissions(), null);

    }

    /**
     * @return the id, required
     */
    public CertificateId getId()
    {
        return (CertificateId) getComponentValue(ID);
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

    @Override
    public String toString() {
        return
                "CertificateSubjectAttributes [\n" +
                        "  id=" + ( getId() != null ? getId().toString().replaceAll("CertificateId ", "") : "NONE") + "\n" +
                        "  validity=" + ( getValidityPeriod() != null ? getValidityPeriod().toString().replaceAll("ValidityPeriod ", "") : "NONE") + "\n" +
                        "  region=" + ( getRegion() != null ? getRegion().toString().replaceAll("GeographicRegion ", "") : "NONE") + "\n" +
                        "  assurance=" + ( getAssuranceLevel() != null ? getAssuranceLevel().toString().replaceAll("SubjectAssurance ", "") : "NONE") + "\n" +
                        "  appPermissions=" + ( getAppPermissions() != null ? getAppPermissions().toString().replaceAll("SequenceOfPsidSsp ", "") : "NONE") + "\n" +
                        "  issuePermissions=" + ( getCertIssuePermissions() != null ? getCertIssuePermissions().toString().replaceAll("SequenceOfPsidGroupPermissions ", "") : "NONE") + "\n" +
                        "]";
    }
}

