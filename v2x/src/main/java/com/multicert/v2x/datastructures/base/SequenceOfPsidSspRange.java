package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfPsidSspRange extends COERSequenceOf
{
    /**
     * Contructor used when encoding
     */
    public SequenceOfPsidSspRange(PsidSspRange[] values)
    {
        super(values);
    }

    /**
     * Contructor used when decoding
     */
    public SequenceOfPsidSspRange()
    {
        super(new PsidSspRange());
    }

    @Override
    public String toString() {
        String retval = "SequenceOfPsidSspRange [";
        if(values != null){
            for(int i=0; i< values.length -1;i++){
                retval += values[i].toString().replace("PsidSspRange ", "") + ",";
            }
            if(values.length > 0){
                retval += values[values.length-1].toString().replace("PsidSspRange ", "");
            }
        }
        retval += "]";
        return retval;
    }
}
