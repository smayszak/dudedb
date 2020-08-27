package org.mayszak;

import org.mayszak.service.DB;
import org.mayszak.utils.ConsoleColors;
import org.mayszak.utils.SampleDataUtil;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws IOException {
        //main used to create a command line loop to accept input.
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        DB dbinstance = new DB();
        boolean run = true;
        while(run){
            String cmd = scanner.next();
            switch(cmd){
                case "put" :
                    String key = scanner.next();
                    String val = scanner.nextLine();
                    boolean success = dbinstance.put(key, val);
                    if(success)
                        System.out.println("ok");
                    else{
                        System.out.println("err");
                    }
                    continue;
                case "get" :
                    String searchkey = scanner.next();
                    String[] readdata = dbinstance.get(searchkey);
                    System.out.println(readdata[1]);
                    continue;
                case "fetch" :
                    int limit = Integer.parseInt(scanner.next());
                    List<String[]> all = dbinstance.getAll(limit);
                    for(String[] item: all){
                        System.out.println("Key:" + item[0] + " val:" + item[1]);
                    }
                    continue;
                case "prime" :
                    int recordcount = Integer.parseInt(scanner.next());
                    SampleDataUtil.prime(dbinstance, recordcount);
                    continue;
                case "exit":
                    run = false;
                    dbinstance.close();
            }
        }
    }
}
