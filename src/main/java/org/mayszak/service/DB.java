package org.mayszak.service;

import org.mayszak.io.Partition;
import org.mayszak.io.PartitionManager;
import org.mayszak.utils.ConsoleColors;

import java.io.IOException;
import java.util.List;

public class DB {

    PartitionManager partitionManager;

    public DB() throws IOException {
        printWelcome();
        partitionManager = PartitionManager.getPartitionManager();
    }
    public DB(String dataFilePath) throws IOException {
        printWelcome();
        partitionManager = PartitionManager.getPartitionManager(dataFilePath);
    }
    public DB(String dataFilePath, boolean spalsh) throws IOException {
        if(!spalsh)
            printWelcome();
        partitionManager = PartitionManager.getPartitionManager(dataFilePath);
    }


    ///simple key value put API
    public boolean put(String id, String value) throws IOException {
        Partition disk = partitionManager.getPartition();
        return disk.write(id, value);
    }

    //get by a specific id API
    public String[] get(String id) throws IOException {
        Partition disk = partitionManager.getPartition();
        String[] data = disk.read(id);
        return data;
    }

    //this is like select *
    public List<String[]> getAll(int limit) throws IOException {
        Partition disk = partitionManager.getPartition();
        return disk.read(limit);
    }

    //hook to safely close the db.
    public void close(){
        partitionManager.close();
        partitionManager = null;
    }


    //spash screen message on db start.
    private void printWelcome() {
        System.out.println(ConsoleColors.BLUE_BACKGROUND_BRIGHT);
        System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT);
        System.out.println("------------ Welcome to  dudedb! ------------");
        System.out.println("It's not much, but at least it's housebroken.");
        System.out.println();
        System.out.println("_____________________________________________");
        System.out.println("  Commands list:");
        System.out.println("               put       key     value");
        System.out.println("               get       key");
        System.out.println("               fetch     limit");
        System.out.println("               prime     recordcount");
        System.out.println("               exit");
        System.out.println();
        System.out.println("______________________________________________");
        System.out.println("   examples:");
        System.out.println("           cmd: put 1 \"<json data>\"");
        System.out.println("       output: ok");
        System.out.println("           cmd: get 1 ");
        System.out.println("       output: \"<json data>\"");
        System.out.println("           cmd: fetch 1000");
        System.out.println("       output: prints 1000 key/val records");
        System.out.println("           cmd: prime 1000");
        System.out.println("       output: adds 1000 key/val records to the db start at index 1 ending at 1000");
        System.out.println("           cmd: exit");
        System.out.println("       output: safe shutdown");
        System.out.println();
        System.out.println("-----------    let's roll !!      -------------");
    }

}
