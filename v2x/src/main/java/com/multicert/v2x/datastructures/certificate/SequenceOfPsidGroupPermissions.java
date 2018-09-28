package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfPsidGroupPermissions extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
    public SequenceOfPsidGroupPermissions (PsidGroupPermissions[] values)
    {
        super(values);
    }

    /**
     * Constructor used when decoding
     *
     */
    public SequenceOfPsidGroupPermissions()
    {
        super(new PsidGroupPermissions());
    }

    @Override
    public String toString() {
        String retval = "SequenceOfPsidGroupPermissions [";
        if(values != null){
            for(int i=0; i< values.length -1;i++){
                retval += values[i].toString().replace("PsidGroupPermissions ", "") + ",";
            }
            if(values.length > 0){
                retval += values[values.length-1].toString().replace("PsidGroupPermissions ", "");
            }
        }
        retval += "]";
        return retval;
    }
}
