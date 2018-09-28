package com.multicert.v2x.datastructures.certificate;

import com.multicert.v2x.asn1.coer.COERChoice;
import com.multicert.v2x.asn1.coer.COERChoiceEnumeration;
import com.multicert.v2x.asn1.coer.COEREncodable;
import com.multicert.v2x.asn1.coer.COERNull;
import com.multicert.v2x.datastructures.base.SequenceOfPsidSspRange;
import com.multicert.v2x.datastructures.base.ServiceSpecificPermissions;

/**
 * This indicates the PSIDs and associated SSPs for which certificate issuance or request permissions are granted by a PsidGroupPermissions structure.
 * If this takes the value explicit, the enclosing PsidGroupPermissions structure grants certificate issuance or request permissions for the indicated
 * PSIDs and SSP ranges. If this takes the value all, the enclosing PsidGroupPermissions structure grants certificate issuance or request permissions
 * for all PSIDs not indicated by other PsidGroupPermissions in the same certIssuePermissions or certRequestPermissions field.
 *
 * Critical information fields:
 * If present, this is a critical information field as defined in 5.2.5. An implementation that does not recognize the indicated CHOICE when verifying
 * a signed SPDU shall indicate that the signed SPDU is invalid.
 * If present, explicit is a critical information field as defined in 5.2.5. An implementation that does not support the number of PsidSspRange in explicit
 * when verifying a signed SPDU shall indicate that the signed SPDU is invalid. A compliant implementation shall support explicit fields
 * containing at least eight entries.
 *
 */
public class SubjectPermissions extends COERChoice
{
  public enum SubjectPermissionsTypes implements COERChoiceEnumeration
  {
      EXPLICIT,
      ALL;

      @Override
      public int myOrdinal()
      {
          return this.ordinal();
      }

      @Override
      public COEREncodable getEncodableType()
      {
          switch (this)
          {
              case EXPLICIT:
                  return new SequenceOfPsidSspRange();
                  default:
                      return new COERNull();
          }
      }
  }

    /**
     * Constructor used when encoding.
     *
     * @param type the type of SubjectPermissions
     * @param value set if type is explicit otherwise null.
     */
    public SubjectPermissions(SubjectPermissionsTypes type, SequenceOfPsidSspRange value) throws IllegalArgumentException
    {
        super(type, (type == SubjectPermissionsTypes.ALL ? new COERNull(): value));
    }

    /**
     * Constructor used when decoding.
     */
    public SubjectPermissions()
    {
        super(SubjectPermissionsTypes.class);
    }

    @Override
    public String toString() {
        if(choice == SubjectPermissionsTypes.ALL){
            return "SubjectPermissions [" + choice +"]";
        }
        return "SubjectPermissions [" + choice + "=" +  value.toString().replace("SequenceOfPsidSspRange ", "") + "]";
    }

}
