package com.rsh.ui.tree;

import com.alee.extended.tree.AsyncUniqueNode;
import org.objectweb.asm.tree.ClassNode;
import pw.tdekk.deob.usage.ClassHierarchy;

/**
 * Created by TimD on 1/24/2017.
 */
public class PositionNode extends AsyncUniqueNode {
    ClassNode classNode;
    ClassHierarchy hierarchy;
    PositionType type;
    int index;

    public PositionNode(ClassNode classNode, PositionType type, int index, ClassHierarchy hierarchy) {
        super();
        this.classNode = classNode;
        this.type = type;
        this.index = index;
        this.hierarchy = hierarchy;
    }

    public ClassHierarchy getHierarchy() {
        return hierarchy;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public PositionType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int hashCode() {
        if (type == PositionType.JAR) return 0;
        return classNode.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof ClassNode) {
            return o.equals(classNode);
        } else if (o instanceof PositionNode) {
            return o.hashCode() == hashCode();
        }
        return false;
    }
}
