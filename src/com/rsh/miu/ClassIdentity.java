package com.rsh.miu;

import com.rsh.util.CSVReader;
import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by TimD on 1/20/2017.
 */
public class ClassIdentity extends MemberIdentity {
    private String superClass;
    private String subClass;
    private String explicitName;
    private Map<String, Integer> fieldCounts;

    public ClassIdentity(String path) {
        identity = path.substring(path.lastIndexOf(File.separatorChar) + 1).split("\\.")[0];
        HashMap<String, String> map = CSVReader.readAsMap(path);
        //remove these so we can use the rest for the fieldCounts
        superClass = map.remove("superClass");
        subClass = map.remove("subClass");
        explicitName = map.remove("explicitName");
        //this will only contain fieldCounts
        fieldCounts = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.parseInt(e.getValue())));
    }

    private String normalize(String var, ClassNode node) {
        if (var.startsWith("#")) {
            var = var.replace("#", "");
            int depth = var.length() - var.replace("[", "").length();
            switch (var) {
                case "self":
                    return node.name;
                case "selfdef":
                    return arrayPrefix(depth) + "L" + node.name + ";";
                case "super":
                    return node.superName;
            }
        }
        return var;
    }

    private String arrayPrefix(int depth) {
        String prefix = "";
        for (int i = 0; i < depth; i++)
            prefix += "[";
        return prefix;
    }

    public boolean matches(ClassNode node) {
        int score = 0;
        if (explicitName == null || normalize(explicitName, node).equals(node.name)) {
            score++;
        }
        if (superClass == null || normalize(superClass, node).equals(node.superName)) {
            score++;
        }
        if (subClass == null || Store.getClasses().values().stream().filter(c -> c.superName.equals(normalize(subClass, node))).anyMatch(c -> c.name.equals(node.name))) {
            score++;
        }
        for (Map.Entry<String, Integer> entry : fieldCounts.entrySet()) {
            if (node.fieldCount(normalize(entry.getKey(), node)) == entry.getValue())
                score++;
        }
        return score == (3 + fieldCounts.size());
    }

    public void onIdentify(ClassNode node) {
        name = node.name;
    }

    public String toString() {
        StringBuilder fields = new StringBuilder();
        fieldCounts.entrySet().forEach(entry -> fields.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n"));
        return "explicitName: " + explicitName + "\nsuperClass: " + superClass + "\nsubClass: " + subClass + "\nfieldCounts: \n" + fields;
    }

    public HashMap<String, String> getOutputMap() {
        HashMap<String, String> output = new HashMap<>();
        fieldCounts.entrySet().forEach(e -> output.put(e.getKey(), e.getValue() + ""));
        output.put("superClass", subClass);
        output.put("subClass", subClass);
        output.put("explicitName", explicitName);
        return output;
    }
}
