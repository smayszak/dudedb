package org.mayszak;

import org.mayszak.service.DB;
import org.mayszak.utils.ConsoleColors;
import org.junit.Test;
import org.mayszak.utils.SampleDataUtil;

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
        int rcdcnt = 100000;
        long file = currentTimeMillis();
        String dir = System.getProperty("user.home");
        String tempdata = dir + "/" + file;
        DB dbinstance = new DB(tempdata);
        System.out.println(ConsoleColors.WHITE);
        HashMap<Integer, String> points = SampleDataUtil.prime(dbinstance, rcdcnt);

        System.out.println("Shutting down DB to measure time to load index for " + rcdcnt + " records");
        timerstart = currentTimeMillis();
        dbinstance.close();
        dbinstance = new DB(tempdata, true);
        timerstop = currentTimeMillis();
        runtime  = timerstop - timerstart;
        System.out.println("Index load time: " + runtime + "ms");

        System.out.println("***********Running validations***************");
        for(Integer key: points.keySet()){
            timerstart = currentTimeMillis();
            String expecting = points.get(key);
            System.out.println("Validating key:" + key);
            System.out.println("Expecting value:" + expecting);
            String[] indexZero = dbinstance.get(key.toString());
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
