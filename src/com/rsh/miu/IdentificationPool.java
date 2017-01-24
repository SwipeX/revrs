package com.rsh.miu;

import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by TimD on 1/20/2017.
 */
public class IdentificationPool {
    private HashMap<String, MemberIdentity> identityMap = new HashMap<>();

    private List<MemberIdentity> getByType(Class<?> cType) {
        return identityMap.values().stream().filter(i -> i.getClass().equals(cType)).collect(Collectors.toCollection(ArrayList::new));
    }
}
