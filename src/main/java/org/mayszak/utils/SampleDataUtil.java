package org.mayszak.utils;

import org.mayszak.service.DB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class SampleDataUtil {
    private static HashMap<Integer, String> distinct = new HashMap<>();
    private static int distCount = 0;
    private static HashMap<Integer, String> common = new HashMap<>();
    private static int commCount = 0;
    public static void loadWords(){
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("abunchofwords");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            java.lang.String line = null;
            while ((line = reader.readLine()) != null) {
                java.lang.String[] someWords = line.split(" ");
                for(java.lang.String word : someWords){
                    if(distinct.containsValue(word)){
                        if(!common.containsValue(word)){
                            common.put(commCount, word);
                            commCount++;
                        }
                    }else{
                        distinct.put(distCount, word);
                        distCount++;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] words;
    public static String getString(int idx){
        return words[idx];
    }
    public static void generateAStrings(int howMany){
        words = new String[howMany+1];
        for(int i = 1; i <= howMany; i++){
            words[i] = generateAString().toLowerCase();
        }
    }
    public static String generateAString(){
        Random random = new Random();
        int numwords = random.nextInt(10) + 1;
        int commonwords = random.nextInt(numwords);
        StringBuilder myword = new StringBuilder();
        myword.append("<'importantmsg':'");
        for(int idx = 0; idx < numwords; idx ++){
            if(commonwords > 0) {
                int dice = random.nextInt(2);
                if (dice == 0){
                    int randIndx = random.nextInt(commCount);
                    myword.append(common.get(randIndx));
                    myword.append(" " );
                    commonwords--;
                }else{
                    int randIndx = random.nextInt(distCount);
                    myword.append(distinct.get(randIndx));
                    myword.append(" " );
                }
            }else{
                int randIndx = random.nextInt(distCount);
                myword.append(distinct.get(randIndx));
                myword.append(" " );
            }
        }
        myword.append("'/>");
        return myword.toString();
    }

    public static HashMap<Integer, String> prime(DB dbinstance, int recordcount) throws IOException {
        HashMap<Integer, String> points = new HashMap<>();
        long timerstart = 0;
        long timerstop = 0;
        long runtime = 0;
        SampleDataUtil.loadWords();
        //generate the payloads in advance so it does not affect our results.
        System.out.println("Generating test data");
        SampleDataUtil.generateAStrings(recordcount);
        System.out.println("Done generating test data");
        System.out.println("Starting write performance load test for " + recordcount + " records");
        timerstart = currentTimeMillis();
        float fastestavg = 0;
        float slowestavg = 0;
        float fastestbatch = 0;
        float slowestbatch = 0;
        int samplerate = 25000;
        for(int i = 1; i <= recordcount; i++) {
            String aword = SampleDataUtil.getString(i);
            //periodically measure performance to see if it degrades over time
            if(i % samplerate == 0){
                points.put(i,aword);
                timerstop = currentTimeMillis();
                runtime  = timerstop - timerstart;
                System.out.println("Insert time at " + i + " records: " + runtime + "ms");
                float precisionms = (float)runtime / (float)samplerate;
                System.out.println("Avg. insert time per record: " + precisionms + "ms");
                if(precisionms > slowestavg)
                    slowestavg = precisionms;
                if(fastestavg != 0 & fastestavg < precisionms)
                    fastestavg = precisionms;

                if(runtime > slowestbatch)
                    slowestbatch = runtime;
                if(fastestbatch != 0 & fastestbatch < runtime)
                    fastestbatch = runtime;

                //reset the timer...
                timerstart = currentTimeMillis();
            }
            dbinstance.put(String.valueOf(i), aword);
        }
        System.out.println("Range:");
        System.out.println("Fastest avg insert: " + fastestavg + "ms slowest avg insert " + slowestavg + "ms");
        System.out.println("Fastest batch insert: " + fastestbatch + "ms slowest batch insert " + slowestbatch + "ms");
        return points;
    }
}
