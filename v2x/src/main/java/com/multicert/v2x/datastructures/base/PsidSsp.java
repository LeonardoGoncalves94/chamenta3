package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequence;

import java.io.IOException;

/**
 * This class represents the permissions that the certificate holder has (indicated by ssp, a.k.a ITS-AID) in a single application area identified by Psid
 * If the ServiceSpecificPermissions is absent, it means that the certificate holder has default permissions for that Psid
 *
 * The permissions are specific to Psid
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class PsidSsp extends COERSequence
{
    private static final int SEQUENCE_SIZE = 2;
    private static final int PSID = 0;
    private static final int SERVICE_SPECIFIC_PERMISSIONS = 1;

    /**
     * Constructor used when encoding
     */
    public PsidSsp(Psid psid, ServiceSpecificPermissions ssp) throws IOException
    {
        super(SEQUENCE_SIZE);
        createSequence();
        setComponentValue(PSID, psid);
        setComponentValue(SERVICE_SPECIFIC_PERMISSIONS, ssp);
    }

    /**
     * Constructor used when decoding
     */
    public PsidSsp()
    {
        super(SEQUENCE_SIZE);
        createSequence();
    }

    /**
     *
     * @return psid value
     */
    public Psid getPsid(){
        return (Psid) getComponentValue(PSID);
    }

    /**
     *
     * @return the service specific permissions
     */
    public ServiceSpecificPermissions getSSP(){
        return (ServiceSpecificPermissions) getComponentValue(SERVICE_SPECIFIC_PERMISSIONS);
    }

    private void createSequence()
    {
        addComponent(PSID, false, new Psid(), null);
        addComponent(SERVICE_SPECIFIC_PERMISSIONS, true, new ServiceSpecificPermissions(), null);
    }

    @Override
    public String toString() {
        return "PsidSsp [psid=" + getPsid().toString().replaceAll("Psid ", "") + ", ssp=" + (getSSP() != null ? getSSP().toString().replaceAll("ServiceSpecificPermissions ", "") : "NULL") + "]";
    }

}
