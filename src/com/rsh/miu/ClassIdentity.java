package com.rsh.miu;

import com.rsh.util.CSVReader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by TimD on 1/20/2017.
 */
public class ClassIdentity extends MemberIdentity {
    private String superClass;
    private String subClass;
    private Map<String, Integer> fieldCounts;

    public ClassIdentity(String path) {
        HashMap<String, String> map = CSVReader.readAsMap(path);
        //remove these so we can use the rest for the fieldCounts
        superClass = map.remove("superClass");
        subClass = map.remove("subClass");
        fieldCounts = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt(e.getValue())));
    }
}
