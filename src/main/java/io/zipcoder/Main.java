package io.zipcoder;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;


public class Main {

    public String readRawDataToString() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void main(String[] args) throws Exception {
       String output = (new Main()).readRawDataToString();

        ItemParser parse = new ItemParser(output);
      ArrayList numbers = parse.parseRawDataIntoStringArray(output);


        parse.print();

        System.out.println(output);
        // TODO: parse the data in output into items, and display to console.
    }
}
