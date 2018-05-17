package com.mini_mo.viewpager.ReadAndWrite;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018-04-24.
 */

public class HashtagSpans {

    ArrayList<int[]> spans = null;
    int[] startend = null;
    Pattern pattern = null;
    Matcher matcher = null;
    String changebody = null;
    StringBuffer buffer = null;
    int start=0,end=0;


    public HashtagSpans(String body, char prefix){
        this.changebody = body;
        startend = new int[2];
        buffer = new StringBuffer();

        spans = new ArrayList<int[]>();

        pattern = Pattern.compile(prefix + "\\w+");
        matcher = pattern.matcher(body);
    }

    public ArrayList<int[]> getSpans(){

        while (matcher.find()){
            int[] currentSpan = new int[2];

            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return this.spans;
    }

    public StringBuffer getHashtags(){

        buffer.setLength(0);

        for (int i = 0; i < getSpansSize(); i++) {

            startend = getSpans().get(i);

            start = startend[0];
            end = startend[1];

            buffer.append((changebody.substring(start,end))+" ");
        }

        return buffer;
    }

    public int getSpansSize(){
        return getSpans().size();
    }

}
