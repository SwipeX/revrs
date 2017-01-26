package com.rsh.miu;

import com.rsh.util.CSVReader;
import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
            int depth = var.length() - (var = var.replace("[", "")).length();
            switch (var) {
                case "self":
                    return node.name;
                case "selfdef":
                    return arrayPrefix(depth) + "L" + node.name + ";";
                case "super":
                    return node.superName;
            }
            boolean def = false;
            if (var.startsWith("def")) {
                var = var.replace("def", "");
                def = true;
            }
            if ((var = getIdentityValue(var)) != null) {
                return arrayPrefix(depth) + (def ? "L" + var + ";" : var);
            }
        }
        return var;
    }

    private String getIdentityValue(String name) {
        ConcurrentHashMap<String, ClassIdentity> identityMap = Store.getClassIdentities();
        if (identityMap.containsKey(name)) {
            ClassIdentity identity = identityMap.get(name);
            if (identity != null && identity.isIdentified()) {
                return identity.getName();
            }
        }
        return null;
    }

    private String arrayPrefix(int depth) {
        String prefix = "";
        for (int i = 0; i < depth; i++)
            prefix += "[";
        return prefix;
    }

    /**
     * @param node a ClassNode object to check against
     * @return true if the node scores a perfect score.
     * <p>
     * Honestly this is fine, but it has repetitive code and probably could get better.
     */
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

    public boolean match(Collection<ClassNode> nodes) {
        Collection<ClassNode> matchedNodes = nodes.stream().filter(this::matches).collect(Collectors.toCollection(ArrayList::new));
        if (matchedNodes.size() > 1) {
            //more than one match...lets decide what to do later
            return false;
        } else {
            //there is only one...this is the best one liner...
            matchedNodes.forEach(this::onIdentify);
            return true;
        }
    }

    private void onIdentify(ClassNode node) {
        name = node.name;
    }

    public String toString() {
        StringBuilder fields = new StringBuilder();
        fieldCounts.entrySet().forEach(entry -> fields.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n"));
        return "explicitName: " + explicitName + "\nsuperClass: " + superClass + "\nsubClass: " + subClass + "\nfieldCounts: \n" + fields;
    }

    private HashMap<String, String> getOutputMap() {
        HashMap<String, String> output = new HashMap<>();
        fieldCounts.entrySet().forEach(e -> output.put(e.getKey(), e.getValue() + ""));
        output.put("superClass", subClass);
        output.put("subClass", subClass);
        output.put("explicitName", explicitName);
        return output;
    }
}
