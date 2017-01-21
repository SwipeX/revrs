package com.rsh.miu;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by TimD on 1/20/2017.
 */
public class IdentificationPool {
    private HashMap<String, MemberIdentity> identityMap = new HashMap<>();

    //TODO figure out wtf im doing wrong lmao
    public Collection<? extends MemberIdentity> getByType(Class identityClass){
        return identityMap.values().stream().collect(Collectors.toCollection(id -> identityClass.equals(id.getClass())));
    }
}
