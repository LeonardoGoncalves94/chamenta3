package com.multicert.v2x.asn1.coer;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * COER encoding of a Sequence following the section 16 of the ISO/IEC 8825-7:2015 standard
 *
 * It is possible to create with a given size, add more Components to the sequence, and set values of Components at a given position.
 *
 * @author Leonardo Gonçalves, leonardo.goncalves@multicert.com
 */
public class COERSequence implements COEREncodable
{

    List<Component> sequenceValues;

    /**
     * Constructor used when encoding and decoding a COERSequence
     *
     * @param size the size of the final sequence, (i.e number of fields, both required and optional)
     */
    public COERSequence(int size)
    {
        sequenceValues = new ArrayList<Component>(size);
    }

    /**
    * Adds a field to the sequence structure when the value is  known.
    *
    * @param position the position of the component.
    * @param value the COER encodable value to set.
    * @param optional true if this field is optional or required
    * @param emptyValue empty version of the COEREncodable
    * @param defaultValue default value if fields is optional but no value is set.
    * */
    public void addComponent(int position, boolean optional, COEREncodable value, COEREncodable emptyValue, COEREncodable defaultValue)
    {
        Component newComponent = new Component(optional, value, emptyValue,  defaultValue);
        sequenceValues.add(position, newComponent);
    }

    /**
     * Adds a field to the sequence structure when the value is not known.
     *
     * @param position the position of the component.
     * @param optional true if this field is optional or required
     * @param emptyValue empty version of the COEREncodable
     * @param defaultValue default value if fields is optional but no value is set.
     * */
    public void addComponent(int position, boolean optional, COEREncodable emptyValue, COEREncodable defaultValue)
    {
        Component newComponent = new Component(optional, null, emptyValue,  defaultValue);
        sequenceValues.add(position, newComponent);
    }

    /**
     *
     * @return the size of this sequence.
     */
    public int size(){
        return sequenceValues.size();
    }


    /**
     * Method to set the value at a given position in the COER Sequence.
     *
     * @param position position to set the value at.
     * @param value the value to set.
     */
    public void setComponentValue(int position, COEREncodable value) throws IOException
    {
        Component c = sequenceValues.get(position);
        if(c == null)
        {
            throw new IOException("Error: No component in the COER sequence at the position: "+ position);
        }
        if(value == null && !c.optional)
        {
            throw new IOException("Error: The component at position "+position+" can not be null because it is not marked as optional");
        }
        c.value = value;
    }

    /**
     * Method that retrieves the value at a given position, if optional and default value is set it will be returned if no value exists at given position, otherwise null.
     */
    public COEREncodable getComponentValue(int position){
        Component c = sequenceValues.get(position);
        if(c.value == null){
            return c.defaultValue;
        }
        return c.value;
    }

    public void encode(DataOutputStream out) throws IOException
    {
        writePreamble(out);
        for(Component c : sequenceValues){
            if(c.value != null){
                c.value.encode(out);
            }else{
                if(!c.optional){
                    throw new IOException("Error encoding COER Sequence: one non optional field was null");
                }
            }
        }
    }

    /**
     * Method to encode the preamble, which indicates the presence of the optional fields in the sequence
     *
     * @param out the Data Output Stream to write the preamble on
     */
    private void writePreamble(DataOutputStream out) throws IOException
    {
        List<Component> optionalComponents = getOptionalComponents();
        if(optionalComponents.size() > 0)
        {
            long preamble = 0;

            for(int i = 0; i < optionalComponents.size()-1; i++)
            {
                if(optionalComponents.get(i).value != null)
                {
                    preamble++;
                }
                preamble = preamble << 1;
            }
            if(optionalComponents.size() > 0 && optionalComponents.get(optionalComponents.size()-1).value != null)
            {
                preamble++;
            }
            COERBitString bitString = new COERBitString(preamble,optionalComponents.size(),true);
            bitString.encode(out);
        }
    }

    @Override
    public void decode(DataInputStream in) throws IOException {
        List<Component> optionalFields = getOptionalComponents();
        long preAmple = readPreamble(in, optionalFields.size());
        for(int i=optionalFields.size()-1; i>=0; i--){
            Component f = optionalFields.get(i);
            f.present = (preAmple % 2 == 1);
            preAmple = preAmple >>> 1;
        }

        for(Component f : sequenceValues){
            if(!f.optional || f.present){
                f.emptyValue.decode(in);
                f.value = f.emptyValue;
            }
        }


    }

    /**
     * Method to decode the preamble, which indicates the presence of the optional fields in the COERSequence
     *
     * @param in the Data Input Stream to read from
     * @param length the number of optional fields on the sequence
     */
    private long readPreamble(DataInputStream in, Integer length) throws IOException
    {
        if(length > 0)
        {
            COERBitString bitString = new COERBitString(length);
            bitString.decode(in);
            return bitString.bitString;
        }
        return 0;
    }

    private List<Component> getOptionalComponents()
    {
        List<Component> optionalComponents = new ArrayList<Component>();
        for (Component c : sequenceValues)
        {
            if(c.optional)
            {
                optionalComponents.add(c);
            }
        }
        return optionalComponents;
    }

    private class Component implements Serializable
    {
        protected boolean optional;
        protected boolean present;
        protected COEREncodable value;
        protected COEREncodable emptyValue;
        protected COEREncodable defaultValue;


        public Component(boolean optional, COEREncodable value, COEREncodable emptyValue, COEREncodable defaultValue)
        {
            this.optional = optional;
            this.value = value;
            this.defaultValue = defaultValue;
            this.emptyValue = emptyValue;
        }
    }

    @Override
    public String toString() {
        String sequenceValueString = "";
        for(int i=0;i<size();i++){
            if(getComponentValue(i) != null){
                sequenceValueString += getComponentValue(i).toString() + (i== size()-1?"":", ");
            }else{
                sequenceValueString += (i== size()-1?"NULL":"NULL, ");
            }
        }

        return "COERSequence [" + sequenceValueString + "]]";
    }


}
