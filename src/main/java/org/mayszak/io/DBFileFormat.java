package org.mayszak.io;

import java.io.UnsupportedEncodingException;

public class DBFileFormat {

    public static int KEY_LEN_BITS= 32; //key is 32 bits
    public static int VAL_LEN_BITS = 256; //val can handle 256 bits
    public static int DATA_LEN = KEY_LEN_BITS + VAL_LEN_BITS;
    public static int KEY_OFFSET = 0;
    public static int VAL_OFFSET = (KEY_OFFSET + KEY_LEN_BITS);

    //writes data in a rigid format. 32 bits for the key, 256 bit for the value.
    public static byte[] serialize(String key, String value) throws UnsupportedEncodingException {
        byte[] keysrc = key.getBytes("utf-8");
        byte[] valsrc = value.getBytes("utf-8");
        if(keysrc.length > KEY_LEN_BITS){
            throw new UnsupportedEncodingException("Key precision will be lost");
        }else if(valsrc.length > VAL_LEN_BITS){
            throw new UnsupportedEncodingException("Data precision will be lost");
        }

        //now, copy into a fixed size data block
        byte[] datablock = new byte[DATA_LEN];
        System.arraycopy(keysrc, 0, datablock, KEY_OFFSET, keysrc.length);
        System.arraycopy(valsrc, 0, datablock, VAL_OFFSET, valsrc.length);

        return datablock;
    }

    //reconstruct byte arrays into the source values. Unused space is filled with NUL, so dont read those in.
    public static String[] deserialize(byte[] keyBytes, byte[] valBytes){
        int keyNUL = 0;
        for(int i = 0; i < keyBytes.length; i++){
            if(keyBytes[i] == 0){
                keyNUL = i;
                break;
            }
            keyNUL++;
        }
        int valNUL = 0;
        for(int i = 0; i < valBytes.length; i++){
            if(valBytes[i] == 0){
                valNUL = i;
                break;
            }
            valNUL++;
        }
        //convert only up to the NUL character.
        String key = new String(keyBytes, 0, keyNUL);
        String val = new String(valBytes, 0, valNUL);
        return new String[]{key, val};
    }

    public static String[] deserialize(byte[] serializedBlock) {
        byte[] keyBytes = new byte[KEY_LEN_BITS];
        System.arraycopy(serializedBlock, 0, keyBytes, 0, KEY_LEN_BITS);

        byte[] valBytes = new byte[VAL_LEN_BITS];
        System.arraycopy(serializedBlock, KEY_LEN_BITS, valBytes, 0, VAL_LEN_BITS);

        return deserialize(keyBytes, valBytes);
    }
}
