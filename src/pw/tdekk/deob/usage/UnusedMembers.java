package pw.tdekk.deob.usage;

import com.rsh.Application;
import com.rsh.util.Store;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import pw.tdekk.deob.Mutator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Created by TimD on 6/21/2016.
 * Entry Points should be considered any method in which code may enter from.
 * This should eventually be built with a ClassHierarchy & CallGraph.
 * </p>
 * <p>
 * A method should be considered used if:
 * 1) It is an entry point.
 * 2) It is called from another method that was used.
 * 3) It is overridden by a subclass.
 * 4) It is inherited from a superclass. Note: could be any superclass in hierarchy.
 * </p>
 * <p>
 * ClassHierarchies are associated to a class if the class is contained in the hierarchy.
 */
public class UnusedMembers implements Mutator {
    private ArrayList<Handle> usedMethods = new ArrayList<>();
    private HashMap<ClassNode, ArrayList<ClassHierarchy>> hierarchies = new HashMap<>();
    private int removedCount = 0;

    @Override
    public void mutate() {
        for (ClassNode node : Store.getClasses().values()) {
            if (Store.getClasses().values().stream().filter(c -> c.superName.equals(node.name)).count() == 0) {
                ClassHierarchy hierarchy = new ClassHierarchy(node);
                addHierarchy(node, hierarchy);
                hierarchy.asList().forEach(classNode -> addHierarchy(classNode, hierarchy));
            }
        }
        long startTime = System.currentTimeMillis();
        getEntryPoints().forEach(this::visit);

        ArrayList<MethodNode> toRemove = new ArrayList<>();
        Store.getClasses().values().forEach(c ->
                c.methods.forEach(m -> {
                    if (!usedMethods.contains(m.getHandle())) {
                        toRemove.add(m);
                        removedCount++;
                    }
                }));
        toRemove.forEach(m -> m.owner.methods.remove(m));
        Application.infoBox(String.format("Removed %s methods in %s ms", removedCount, (System.currentTimeMillis() - startTime)), "Status");
    }

    private void addHierarchy(ClassNode node, ClassHierarchy hierarchy) {
        ArrayList<ClassHierarchy> list = hierarchies.containsKey(node) ? hierarchies.get(node) : new ArrayList<>();
        list.add(hierarchy);
        hierarchies.put(node, list);
    }

    private void visit(MethodNode mn) {
        if (mn == null) return;
        Handle handle = mn.getHandle();
        if (usedMethods.contains(handle)) return;
        usedMethods.add(handle);
        mn.accept(new MethodVisitor() {
            @Override
            public void visitMethodInsn(MethodInsnNode mn) {
                if (Store.getClasses().containsKey(mn.owner)) {
                    ClassNode node = Store.getClasses().get(mn.owner);
                    MethodNode method = node.getMethod(mn.name, mn.desc);
                    if (method != null) {
                        visit(method);
                        List<ClassHierarchy> hierarchyList = hierarchies.get(node);
                        for (ClassHierarchy hierarchy : hierarchyList) {
                            visit(hierarchy.getOverriding(method));
                            visit(hierarchy.getOverriden(method));
                        }
                    }
                }
            }
        });
    }

    public List<MethodNode> getEntryPoints() {
        ArrayList<MethodNode> entry = new ArrayList<>();
        Store.getClasses().values().forEach(node -> {
            //All methods > 2 length for osrs obfuscator
            entry.addAll(node.methods.stream().filter(m -> m.name.length() > 2).collect(Collectors.toList()));
            //Any subclass methods should be added
            String superName = node.superName;
            if (Store.getClasses().containsKey(superName)) {
                for (MethodNode method : node.methods) {
                    entry.addAll(Store.getClasses().get(superName).methods.stream().filter(m ->
                            m.name.equals(method.name) && m.desc.equals(method.desc)).collect(Collectors.toList()));
                }
            }
            //interface methods
            List<String> interfaces = node.interfaces;
            for (String iface : interfaces) {
                ClassNode impl = Store.getClasses().get(iface);
                if (impl != null) {
                    for (MethodNode mn : node.methods) {
                        impl.methods.stream().filter(imn -> mn.name.equals(imn.name) && mn.desc.equals(imn.desc)).forEach(imn -> {
                            entry.add(mn);
                            entry.add(imn);
                        });
                    }
                }
            }
        });
        return entry;
    }
}
