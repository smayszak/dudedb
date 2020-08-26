package org.mayszak.io;

import java.io.IOException;

public class PartitionManager {
    //this version of partionmanager doesnt really do anything.
    //it is implemented as a singleton (not very well BTW), but it holds the disk
    //and assumes there is one disk file. this is a class inserted because at some point
    //we will do interesting things in this area.

    private Partition diskPartition = null;
    private static PartitionManager instance = null;

    public PartitionManager(String filePath) throws IOException {
        diskPartition = new Partition(filePath + ".data", filePath + ".index");
    }

    public static PartitionManager getPartitionManager() throws IOException {
        String dir = System.getProperty("user.home");
        return getPartitionManager(dir + "/dudedb");
    }

    public static PartitionManager getPartitionManager(String filePath) throws IOException {
        if(instance==null){
            instance = new PartitionManager(filePath);
        }
        return instance;
    }

    public Partition getPartition(){
        return diskPartition;
    }

    public void close(){
        diskPartition.close();
        diskPartition = null;
        instance = null;
    }
}
