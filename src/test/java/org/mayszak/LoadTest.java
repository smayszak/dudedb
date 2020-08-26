package org.mayszak;

import org.mayszak.service.DB;
import org.mayszak.utils.ConsoleColors;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoadTest {
    @Test
    //this is an all encomposing test. it loads a million records, records performance
    //and compares results match.  so treat this like a giant integ test.
    //it will also be interesting to record the metrics from this test before major refactors
    public void loadupthedbandensurewegetrightvalues() throws IOException {
        //setup test variables
        long timerstart = 0;
        long timerstop = 0;
        long runtime = 0;
        int rcdcnt = 1000000;
        long file = currentTimeMillis();
        String dir = System.getProperty("user.home");
        String tempdata = dir + "/" + file;
        DB dbinstance = new DB(tempdata);
        WordUtil.loadWords();
        HashMap<String, String> points = new HashMap<>();

        System.out.println(ConsoleColors.WHITE);
        //generate the payloads in advance so it does not affect our results.
        System.out.println("Generating test data");
        WordUtil.generateAStrings(rcdcnt);
        System.out.println("Done generating test data");

        System.out.println("Starting write performance load test for " + rcdcnt + " records");
        timerstart = currentTimeMillis();
        float fastestavg = 0;
        float slowestavg = 0;
        float fastestbatch = 0;
        float slowestbatch = 0;
        int samplerate = 25000;
        for(int i = 1; i <= rcdcnt; i++) {
            String aword = WordUtil.getString(i);
            //periodically measure performance to see if it degrades over time
            if(i % samplerate == 0){
                points.put("" + i + "",aword);
                timerstop = currentTimeMillis();
                runtime  = timerstop - timerstart;
                System.out.println("Insert time at " + i + " records: " + runtime + "ms");
                float precisionms = (float)runtime / (float)samplerate;
                System.out.println("Avg. insert time per record: " + precisionms + "ms");
                if(precisionms > slowestavg)
                    slowestavg = precisionms;
                if(fastestavg != 0 & fastestavg > precisionms)
                    fastestavg = precisionms;

                if(runtime > slowestbatch)
                    slowestbatch = runtime;
                if(fastestbatch != 0 & fastestbatch > runtime)
                    fastestbatch = runtime;

                //reset the timer...
                timerstart = currentTimeMillis();
            }
            dbinstance.put(String.valueOf(i), aword);
        }
        System.out.println("Range:");
        System.out.println("Fastest avg insert: " + fastestavg + "ms slowest avg insert " + slowestavg + "ms");
        System.out.println("Fastest batch insert: " + fastestbatch + "ms slowest batch insert " + slowestbatch + "ms");


        System.out.println("Shutting down DB to measure time to load index for " + rcdcnt + " records");
        timerstart = currentTimeMillis();
        dbinstance.close();
        dbinstance = new DB(tempdata, true);
        timerstop = currentTimeMillis();
        runtime  = timerstop - timerstart;
        System.out.println("Index load time: " + runtime + "ms");

        System.out.println("***********Running validations***************");
        for(String key: points.keySet()){
            timerstart = currentTimeMillis();
            String[] indexZero = dbinstance.get(key);
            timerstop = currentTimeMillis();
            assertEquals(points.get(key), indexZero[1]);
            runtime  = timerstop - timerstart;
            System.out.println("Found key in " + runtime + "ms");
            System.out.println("Key had value: " + indexZero[1]);
            assertTrue(runtime < 3);
        }

        File data = new File(tempdata + ".data");
        data.delete();
        File idx = new File(tempdata + ".index");
        idx.delete();
    }
}
