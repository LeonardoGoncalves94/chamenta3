package com.multicert.v2x.datastructures.base;

import com.multicert.v2x.asn1.coer.COERSequenceOf;
import com.multicert.v2x.datastructures.base.PsidSsp;

/**
 * A sequence of PsidSsp
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 *
 */
public class SequenceOfPsidSsp extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     *
     */
    public SequenceOfPsidSsp(PsidSsp[] values)
    {
        super(values);
    }

    /**
     * Constructor used when decoding
     *
     */
    public SequenceOfPsidSsp()
    {
        super(new PsidSsp());
    }


    @Override
    public String toString() {
        String retval = "SequenceOfPsidSsp [";
        if(values != null){
            for(int i=0; i< values.length -1;i++){
                retval += values[i].toString().replace("PsidSsp ", "") + ",";
            }
            if(values.length > 0){
                retval += values[values.length-1].toString().replace("PsidSsp ", "");
            }
        }
        retval += "]";
        return retval;
    }
}
