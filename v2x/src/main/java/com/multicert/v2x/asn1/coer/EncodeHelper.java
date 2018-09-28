package com.multicert.v2x.asn1.coer;

import java.io.*;
import java.math.BigInteger;

public class EncodeHelper
{
    /**
     * Method that inserts zeroes at the beginning of a byte array
     *
     * @param data the byte array to transform
     * @param size the size that the array will have after transformation
     * @return the transformed array
     * @return the array of bytes
     */
    public static byte[] padWithZeroes(byte[] data, int size)
    {
        if(data == null){
            return null;
        }
        if(data.length < size){
            byte[] newData = new byte[size];
            System.arraycopy(data, 0, newData, size-data.length, data.length);
            data = newData;
        }
        return data;
    }

    public static void writeFixedFieldSizeKey(int fieldSize, OutputStream out, BigInteger keyValue) throws UnsupportedOperationException, IOException{
        byte[] valueByteArray = keyValue.toByteArray();
        if(valueByteArray.length < fieldSize){
            out.write(new byte[fieldSize - valueByteArray.length]);
        }
        if(valueByteArray.length > fieldSize){
            out.write(valueByteArray, valueByteArray.length-fieldSize, fieldSize);
        }else{
            out.write(valueByteArray);
        }
    }

    public static BigInteger readFixedFieldSizeKey(int fieldSize,  InputStream in) throws UnsupportedOperationException, IOException{
        byte[] data = new byte[fieldSize +1];
        in.read(data,1,fieldSize);
        return new BigInteger(data);
    }

    /** public static void writeFixedFieldSizeKey(int size, OutputStream out, BigInteger keyValue) throws UnsupportedOperationException, IOException
     {
         byte[] valueByteArray = keyValue.toByteArray();

         if(valueByteArray.length < size) //TODO ver melhor
         {
             out.write(new byte[size - valueByteArray.length]);
         }
         if(valueByteArray.length > size)
         {
             out.write(valueByteArray, valueByteArray.length-size, size);
         }
         else
         {
             out.write(valueByteArray);
         }
     }
     **/


    /**
     * Help method to perform java serialization of coer objects used for deep cloning.
     */
    public static byte[] serialize(COEREncodable object) throws IllegalArgumentException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream dos = new ObjectOutputStream(baos);
            dos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error serializing COER object during deep clone: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    /**
     * Help method to perform java deserialization of coer objects used for deep cloning.
     */
    public static COEREncodable deserialize(byte[] serializedData) throws IllegalArgumentException{
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedData));
            return (COEREncodable) ois.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing COER object during deep clone: " + e.getMessage());
        }
    }


}
