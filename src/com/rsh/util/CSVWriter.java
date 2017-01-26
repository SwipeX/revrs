package com.rsh.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by TimD on 1/21/2017.
 */
public class CSVWriter {

    public static void WriteMap(String path, HashMap<String, String> map) {
        try {
            PrintWriter writer = new PrintWriter(path);
            map.entrySet().forEach(entry -> writer.write(entry.getKey() + "," + entry.getValue() + "\r\n"));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
