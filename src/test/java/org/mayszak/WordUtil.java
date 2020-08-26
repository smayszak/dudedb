package org.mayszak;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

public class WordUtil {
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
            words[i] = generateAString();
        }
    }
    public static String generateAString(){
        Random random = new Random();
        int numwords = random.nextInt(10) + 1;
        int commonwords = random.nextInt(numwords);
        StringBuilder myword = new StringBuilder();
        myword.append("<'constructedstring':'");
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
}
