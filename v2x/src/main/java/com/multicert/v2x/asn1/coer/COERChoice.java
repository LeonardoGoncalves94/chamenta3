package com.multicert.v2x.asn1.coer;


/**
 * COER encoding of a Choice following the section 20 of the ISO/IEC 8825-7:2015 standard
 *
 * The encoding of a COER choice consists of the encoding of a COERTag (indicates the type) followed by the encoding of a value
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class COERChoice implements COEREncodable
{
    protected COERChoiceEnumeration choice;
    protected COEREncodable value;
    protected Class<?> choiceEnum;

    /**
     * Constructor used when encoding a COERChoice.
     *
     * @param choice the item of an enumeration implementing COERChoiceEnumeration
     * @param value the value of the choice
     */
    public COERChoice(COERChoiceEnumeration choice, COEREncodable value){
        this.choice = choice;
        this.value = value;
    }

    /**
     * Constructor used when decoding a COERChoice.
     *
     * @param choiceEnum the class of the enum that implements COERChoiceEnumeration
     */
    public COERChoice(Class<?> choiceEnum){
        this.choiceEnum = choiceEnum;
    }

    public void encode(DataOutputStream out) throws IOException
    {
        COERTag tag = new COERTag(COERTag.CONTEXT_SPECIFIC_TAG_CLASS, choice.myOrdinal());
        tag.encode(out);
        value.encode(out);
    }

    public void decode(DataInputStream in) throws IOException
    {
        COERTag tag = new COERTag();
        tag.decode(in);
        choice = (COERChoiceEnumeration) choiceEnum.getEnumConstants()[(int) tag.getTagNumber()];
        value = choice.getEncodableType();
        value.decode(in);
    }

    public COEREncodable getValue()
    {
        return value;
    }

    @Override
    public String toString() {
        return "COERChoice [choice=" + choice + ", value=" + value + "]";
    }
}
