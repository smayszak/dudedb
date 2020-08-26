package org.mayszak;

import org.junit.Test;
import org.mayszak.io.DBFileFormat;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class SerializerTest
{
    @Test
    public void serializerCanRetriveValues() throws UnsupportedEncodingException {
        String key = "1234";
        String data = "<object a attr=blag; attb=blog;/>";
        byte[] serializedBlock = DBFileFormat.serialize(key, data);
        String[] desizzled = DBFileFormat.deserialize(serializedBlock);

        assertEquals( key, desizzled[0] );
        assertEquals( data, desizzled[1] );
    }
}
