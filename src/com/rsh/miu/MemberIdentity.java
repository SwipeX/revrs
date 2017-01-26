package com.rsh.miu;

/**
 * Created by TimD on 1/20/2017.
 * A member can be a class/field/method.
 * Each member has a name, which represents the name it is given in the class file.
 * Each member also has an identity, which is what it is called across all revisions.
 */
public abstract class MemberIdentity {
    protected String name;
    protected String identity;

    public boolean isIdentified() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    public String getIdentity() {
        return identity;
    }

//I need some way to read/save parameters to conjugate a validate method.
}
