package com.rsh.util;

import java.io.File;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Scanner;

/**
 * Created by TimD on 1/21/2017.
 */
public class CSVReader {

    public static HashMap<String, String> readAsMap(String path) {
        HashMap<String, String> map = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");
                map.put(split[0].trim(), split[1].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
