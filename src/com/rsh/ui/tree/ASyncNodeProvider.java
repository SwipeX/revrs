package com.rsh.ui.tree;

import com.alee.extended.tree.AsyncTreeDataProvider;
import com.alee.extended.tree.ChildsListener;
import com.alee.utils.compare.Filter;
import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;
import pw.tdekk.deob.usage.ClassHierarchy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by TimD on 1/24/2017.
 */
public class ASyncNodeProvider implements AsyncTreeDataProvider<PositionNode> {

    private HashMap<ClassNode, ClassHierarchy> hierarchies = new HashMap<>();

    /**
     * Set up all of the loaded class hierarchies; only from classes which are the end of a hierarchy (highest depth).
     */
    public ASyncNodeProvider() {
        outer:
        for (ClassNode node : Store.getClasses().values()) {
            for (ClassNode node2 : Store.getClasses().values()) {
                if (node2.superName.equals(node.name)) continue outer;
            }
            //only add a class if it has a hierarchy depth of 2 or more...
            if (Store.getClasses().containsKey(node.superName)) {
                hierarchies.put(node, new ClassHierarchy(node));
            }
        }
    }

    @Override
    public PositionNode getRoot() {
        return new PositionNode(null, PositionType.JAR, 0, null);
    }

    @Override
    public void loadChilds(PositionNode positionNode, ChildsListener<PositionNode> loader) {
        if (positionNode.getType() == PositionType.JAR) {
            loader.childsLoadCompleted(hierarchies.keySet().stream().map(k -> new PositionNode(k, PositionType.CHILD, -1, hierarchies.get(k))).collect(Collectors.toList()));
        } else {
            ArrayList<PositionNode> positionNodes = new ArrayList<>();
            ClassHierarchy hierarchy = positionNode.getHierarchy();
            List<ClassNode> nodes = hierarchy.asList();
            int index = positionNode.getIndex();
            positionNodes.add(new PositionNode(nodes.get(index + 1), (index + 2 >= nodes.size()) ? PositionType.PARENT : PositionType.CHILD, index + 1, hierarchy));
            loader.childsLoadCompleted(positionNodes);
        }
    }

    @Override
    public Comparator<PositionNode> getChildsComparator(PositionNode positionNode) {
        return (o1, o2) -> {
            ClassNode a = o1.getClassNode();
            if (a == null) return -1;
            ClassNode b = o2.getClassNode();
            if (b == null) return 1;
            return a.name.compareTo(b.name);
        };
    }

    @Override
    public Filter<PositionNode> getChildsFilter(PositionNode positionNode) {
        return null;
    }

    @Override
    public boolean isLeaf(PositionNode positionNode) {
        return positionNode.getType() == PositionType.PARENT;
    }

}
