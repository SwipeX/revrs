package com.rsh.util;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Scanner;

/**
 * Created by TimD on 1/21/2017.
 */
public class CSVReader {

    public static HashMap<String, String> readAsMap(String path) {
        Scanner scanner = new Scanner(path);
        HashMap<String, String> map = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split(",");
            map.put(split[0], split[1]);
        }
        scanner.close();
        return map;
    }
}
