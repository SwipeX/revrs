package pw.tdekk.deob.usage;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Swipe on 7/18/2016.
 * A root class is that such that it is not extended by any other class.
 * The exact hierarchy cannot exist twice in the same context.
 * Interfaces can be accessed from the ClassNode they correspond to.
 * Scales from root up to (not including) java/lang/Object.
 * NOTE: the root is not contained in the hierarchy list.
 */
public class ClassHierarchy {

    private final ClassNode root;
    private final List<ClassNode> hierarchy;
    private final HashMap<ClassNode, List<ClassNode>> interfaces;

    public ClassHierarchy(ClassNode root) {
        this.root = root;
        hierarchy = new ArrayList<>();
        interfaces = new HashMap<>();
        ClassNode parent = getSuperClass(root);
        while (parent != null) {
            hierarchy.add(parent);
            interfaces.put(parent, getInterfaces(parent.interfaces));
            parent = getSuperClass(parent);
        }
    }

    /**
     * @return the classes in the hierarchy, not containing the root.
     */
    public List<ClassNode> getHierarchy() {
        return hierarchy;
    }

    /**
     * @return the depth of the hierarchy.
     */
    public final int depth() {
        return hierarchy.size() + 1;
    }

    /**
     * @param node the node for which the interfaces will be derived from.
     * @return a List<ClassNode> of valid interfaces.
     */
    public final List<ClassNode> getInterfaces(ClassNode node) {
        if (interfaces.containsKey(node)) return interfaces.get(node);
        List<ClassNode> resultant = getInterfaces(node.interfaces);
        interfaces.put(node, resultant);
        return resultant;
    }

    /**
     * This is more of an internal method...
     *
     * @param interfaces - a list of interface names from a ClassNode object
     * @return a list of the ClassNodes for these interfaces if they exist.
     */
    private final List<ClassNode> getInterfaces(List<String> interfaces) {
        return interfaces.stream().filter(i -> Application.getClasses().containsKey(i)).map(i ->
                Application.getClasses().get(i)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param node a ClassNode which may be in this hierarchy.
     * @return true if the node is contained in this class hierarchy, including interfaces.
     */
    public final boolean contains(ClassNode node) {
        return node.equals(root) || hierarchy.contains(node)
                || getInterfaces(root.interfaces).contains(node)
                || hierarchy.stream().anyMatch(h -> getInterfaces(h.interfaces).contains(node));
    }

    /**
     * @param node - the node to which the parent may exist.
     * @return the parent of the ClassNode if it exists in the loaded classes.
     */
    public final ClassNode getSuperClass(ClassNode node) {
        String parentName = node.superName;
        if (Application.getClasses().containsKey(parentName)) {
            return Application.getClasses().get(parentName);
        }
        return null;
    }

    /**
     * @param node the node to which a child may exist.
     * @return the directly next child node if it exists in the loaded classes.
     */
    public final ClassNode getSubClass(ClassNode node) {
        int index = hierarchy.indexOf(node);
        if (node == root || index < 0) return null;
        else if (index == 0) return root;
        return hierarchy.get(index - 1);
    }

    /**
     * @return The class in the hierarchy that must stem from a native Java class.
     */
    public final ClassNode getAncestor() {
        return hierarchy.isEmpty() ? root : hierarchy.get(hierarchy.size() - 1);
    }

    /**
     * @return the superclass method if it is overriding a parent (superclass) method.
     */
    public final MethodNode getOverriden(MethodNode mn) {
        ClassNode owner = mn.owner;
        //only need to check classes higher than the current in the hierarchy.
        ClassNode parent = getSuperClass(owner);
        while (parent != null) {
            List<ClassNode> interfaces = this.interfaces.get(parent);
            MethodNode method;
            for (ClassNode iface : interfaces) {
                if ((method = iface.getMethod(mn.name, mn.desc)) != null)
                    return method;
            }
            if ((method = parent.getMethod(mn.name, mn.desc)) != null)
                return method;
            parent = getSuperClass(parent);
        }
        return null;
    }

    /**
     * @return the subclass method if it is overridden by a subclass.
     */
    public final MethodNode getOverriding(MethodNode mn) {
        ClassNode owner = mn.owner;
        ClassNode child = getSubClass(owner);
        while (child != null) {
            List<ClassNode> interfaces = this.interfaces.get(child);
            MethodNode method;
            if (interfaces != null) {
                for (ClassNode iface : interfaces) {
                    if ((method = iface.getMethod(mn.name, mn.desc)) != null)
                        return method;
                }
            }
            if ((method = child.getMethod(mn.name, mn.desc)) != null)
                return method;
            child = getSubClass(child);
        }
        return null;
    }

    /**
     * @return a String representation of this Object.
     */

    public String toString() {
        StringBuilder builder = new StringBuilder("*  Hierarchy *\r\n");
        for (int i = hierarchy.size() - 1; i >= 0; i--) {
            ClassNode next = hierarchy.get(i);
            builder.append("*     ").append(next.name).append("     *").append("\r\n");
        }
        builder.append("*  ").append("Root: " + root.name).append(root.name.length() == 1 ? " " : "").append("  *");
        return builder.toString();
    }
}