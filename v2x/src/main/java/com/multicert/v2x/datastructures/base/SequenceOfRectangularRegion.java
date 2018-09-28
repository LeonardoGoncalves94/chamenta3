package com.multicert.v2x.datastructures.base;


import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COERSequenceOf;

public class SequenceOfRectangularRegion extends COERSequenceOf
{
    /**
     * Constructor used when encoding
     */
  public SequenceOfRectangularRegion(RectangularRegion[] values)
  {
      super(values);
  }

    /**
     * Constructor used when decoding
     */
    public SequenceOfRectangularRegion()
    {
        super(new RectangularRegion());
    }
}
