package org.mayszak.io;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Partition {
    //handles to the data and index file.
    File data;
    File index;
    //this is a simple way of keeping track of where we should write the next block.
    long nextKeyPosition = 0;
    //this is a pattern used to store data in the index file.
    String indexPattern = "<^>";
    //in mem map of a primary key to a positional offset in the data file.
    private HashMap<String, Long> keyToOffset  = new HashMap<>();

    //requires a path to a data file and index file.
    public Partition(String path, String indexpath) throws FileNotFoundException, IOException {

        data = new File(path);
        index = new File(indexpath);

        if(!data.exists())
        {
            boolean newFile = data.createNewFile();
            if(!newFile){
                throw new IOException();
            }
        }

        if(!index.exists())
        {
            boolean newFile = index.createNewFile();
            if(!newFile){
                throw new IOException();
            }
        }else{
            readIndex();
        }


    }

    //write opens a stream to the output data file, writes a block of data and then updates the index
    public boolean write(String key, String value) throws IOException {
        //run some simple validations.
        if(value == indexPattern)
        {
            System.out.println("<^> is an internal reserved encoding");
            return false;
        }
        //do some simple boundry checks because initial version has hard limits on storage blocks.
        if(key.length() > DBFileFormat.KEY_LEN_BITS){
            System.out.println("ID's can not exceed " +  DBFileFormat.KEY_LEN_BITS + " characters");
            return false;
        }

        if (value.length() > DBFileFormat.VAL_LEN_BITS) {
            System.out.println("Values can not exceed " +  DBFileFormat.VAL_LEN_BITS + " characters");
            return false;
        }

        //validations passed so try to write to file.
        try {
            FileOutputStream outStream = new FileOutputStream(data, true);
            byte[] serializedData = DBFileFormat.serialize(key, value);
            //write to file.
            outStream.write(serializedData);
            //save the position in the index
            updateIndex(key, nextKeyPosition);
            //record where we should put the next block.
            nextKeyPosition = outStream.getChannel().position();
            //close out the stream.
            outStream.flush();
            outStream.close();
        }
        catch(Exception err){
            System.out.println(err);
            return false;
        }
        return true;
    }

    public List<String[]> read() throws IOException {
        List<String[]> results = new ArrayList();
        for(String key: keyToOffset.keySet()){
            results.add(read(key));
        }
         return results;
    }

    //read looks up the index of the key and then reads that block from the data file
    public String[] read(String key) throws IOException {
        FileInputStream inputStream = new FileInputStream(data);
        if(!keyToOffset.containsKey(key)){
            return new String[]{key, "not found"};
        }
        long offset = keyToOffset.get(key);
        inputStream.getChannel().position(offset);
        byte[] byteBlock = new byte[DBFileFormat.DATA_LEN];
        //the keyoffset is not a representation of the data file but rather of its in memory partition.
        // When you seek or scan you move the memory buffer to that position, so if you want to read two parts of a block,
        // after reading the first part the second will be at index 0 because the bufer advances as you read.
        //in other words, you have to open a file at an offset if you want to be able to reference a random offset.
        inputStream.read(byteBlock, 0, DBFileFormat.DATA_LEN);
        inputStream.close();
        //convert to original format
        return DBFileFormat.deserialize(byteBlock);
    }

    //writes the index to a file so it can be retrieved on startup.
    private void updateIndex(String key, long offset) {
        String data = key + "<^>" + offset;
        ///consider this a wal, it is used to reconstruct everything and we should not move forward without it being saved.
        FileWriter fr = null;
        BufferedWriter br = null;
        String dataWithNewLine = data + System.getProperty("line.separator");
        try {
            fr = new FileWriter(index, true);
            br = new BufferedWriter(fr);
            br.write(dataWithNewLine);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            keyToOffset.put(key, offset);
        }
    }

    //reserving close in case we have to clear out in memmory buffers
    public void close(){

    }

    //reads the index file in from disk on service startup.
    private void readIndex() throws IOException {
        FileReader fr = new FileReader(index);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("[<^>]");
            keyToOffset.put(parts[0], Long.valueOf(parts[3]));
            if(Long.valueOf(parts[3]) > nextKeyPosition);{
                nextKeyPosition = Long.valueOf(parts[3]);
            }
        }
        nextKeyPosition += DBFileFormat.DATA_LEN;
        fr.close();
    }
}

