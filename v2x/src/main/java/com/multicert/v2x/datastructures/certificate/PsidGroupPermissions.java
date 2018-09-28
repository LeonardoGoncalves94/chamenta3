package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERInteger;
import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;
import java.math.BigInteger;

/**
 * This class indicates the permissions that a certificate holder has with respect to issuing and requesting certificates for a particular set of PSIDs.
 *
 * subjectPermissions: indicates PSIDs and SSP ranges covered by this field.
 * minChainDepth and chainDepthRange: indicate how long the certificate chain from this certificate to the
 * end-entity certificate is permitted to be. The length of the certificate chain is measured from the certificate
 * issued by this certificate to the end-entity certificate in the case of certIssuePermissions and from the certificate
 * requested by this certificate to the end-entity certificate in the case of certRequestPermissions;
 * a length of 0 therefore indicates that the certificate issued or requested is an end-entity certificate. The length
 * is permitted to be (a) greater than or equal to minChainDepth certificates and (b) less than or equal to
 * minChainDepth + chainDepthRange certificates. The value -1 for chainDepthRange is a special case: if the
 * value of chainDepthRange is -1 that indicates that the certificate chain may be any length equal to or
 * greater than minChainDepth.
 * eeType: takes one or more of the values app and enrol and indicates the type of certificates or requests that this
 * instance of PsidGroupPermissions in the certificate is entitled to authorize. If this field indicates app, the chain ends
 * in an authorization certificate, i.e. a certficate in which these permissions appear in an appPermissions field.
 * If this field indicates enrol, the chain ends in an enrolment certificate, i.e. a certificate in which these permissions
 * appear in a certReqPermissions permissions field), or both. Different instances of PsidGroupPermissions within a
 * ToBeSignedCertificate may have different values for eeType.
 **/
public class PsidGroupPermissions extends COERSequence
{
    private static final int SEQUENCE_SIZE = 4;
    private static final int SUBJECT_PERMISSIONS = 0;
    private static final int MIN_CHAIN_LENGTH = 1;
    private static final int CHAIN_LENGTH_RANGE = 2;
    private static final int EE_TYPE = 3;

    /**
     * Constructor used when encoding
     */
    public PsidGroupPermissions(SubjectPermissions subjectPermissions, Integer minChainLength, Integer chainLengthRange, EndEntityType endEntityType) throws IOException
    {
       super(SEQUENCE_SIZE);
       createSequence();
       setComponentValue(SUBJECT_PERMISSIONS, subjectPermissions);
       setComponentValue(MIN_CHAIN_LENGTH, minChainLength == null ? null : new COERInteger(BigInteger.valueOf(minChainLength),BigInteger.ZERO,null));
       setComponentValue(CHAIN_LENGTH_RANGE, chainLengthRange == null ? null : new COERInteger(chainLengthRange));
       setComponentValue(EE_TYPE, endEntityType);
    }

    /**
     * Constructor used when decoding
     *
     */
    public PsidGroupPermissions()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    public void createSequence()
    {
        addComponent(SUBJECT_PERMISSIONS, false, new SubjectPermissions(), null);
        addComponent(MIN_CHAIN_LENGTH, true, new COERInteger(), new COERInteger(1));
        addComponent(CHAIN_LENGTH_RANGE, true, new COERInteger(), new COERInteger(0));
        addComponent(EE_TYPE, false, new EndEntityType(), null);
    }

    /**
     *
     * @return appPermissions
     */
    public SubjectPermissions getAppPermissions(){
        return (SubjectPermissions) getComponentValue(SUBJECT_PERMISSIONS);
    }

    /**
     *
     * @return minChainDepth
     */
    public int getMinChainDepth(){
        return (int) ((COERInteger) getComponentValue(MIN_CHAIN_LENGTH)).getValueAsLong();
    }

    /**
     *
     * @return chainDepthRange
     */
    public int getChainDepthRange(){
        return (int) ((COERInteger) getComponentValue(CHAIN_LENGTH_RANGE)).getValueAsLong();
    }

    /**
     *
     * @return eeType
     */
    public EndEntityType getEEType(){
        return (EndEntityType) getComponentValue(EE_TYPE);
    }

    @Override
    public String toString() {
        return "PsidGroupPermissions [appPermissions=" + getAppPermissions().toString().replaceAll("SubjectPermissions ", "") + ", minChainDepth=" + getMinChainDepth()
                + ", chainDepthRange=" + getChainDepthRange() + ", eeType=" + getEEType().toString().replaceAll("EndEntityType ", "")  + "]";
    }
}
