package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COEROctetString;


public class SubjectAssurance extends COEROctetString
{
    private Integer subjectAssurance = null;

    private static final int OCTET_STRING_LENGTH = 1;

    /**
     * Constructor used when encoding
     */
    public SubjectAssurance(int assuranceLevel, int confidenceLevel) throws IllegalArgumentException
    {
        super(OCTET_STRING_LENGTH, OCTET_STRING_LENGTH);

        if(assuranceLevel < 0 || confidenceLevel > 7)
        {
            throw new IllegalArgumentException("Error encoding SubjectAssurance: assurance level must be between 0 and 7");
        }
        if(assuranceLevel < 0 || confidenceLevel > 3)
        {
            throw new IllegalArgumentException("Error encoding SubjectAssurance: confidence level must be between 0 and 3");
        }

        int shiftedAssuranceLevel = assuranceLevel << 5;
        subjectAssurance = shiftedAssuranceLevel | confidenceLevel;
        data = new byte[OCTET_STRING_LENGTH];
        data[0] = subjectAssurance.byteValue();
    }

    /**
     * Constructor used when decoding
     */
    public SubjectAssurance()
    {
        super(OCTET_STRING_LENGTH, OCTET_STRING_LENGTH);
    }

    private Integer getSubjectAssurance()
    {
        if(subjectAssurance == null)
        {
           int aux = data[0] & 0xff; // we apply a mask to read byte as an unsigned
           subjectAssurance = new Integer(aux);

        }
        return subjectAssurance;
    }

    public int getAssuranceLevel()
    {
        getSubjectAssurance();
        return subjectAssurance >> 5;
    }

    public int getConfidenceLevel()
    {
        getSubjectAssurance();
        return subjectAssurance & 3;
    }

    @Override
    public String toString() {
        return "SubjectAssurance [subjectAssurance=" + getSubjectAssurance() + " (assuranceLevel=" + getAssuranceLevel() + ", confidenceLevel= " + getConfidenceLevel() +" )]";
    }

}
