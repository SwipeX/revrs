package pw.tdekk.deob.usage;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import pw.tdekk.deob.Mutator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TimD on 6/21/2016.
 */
public class UnusedParameters implements Mutator {
    private int totalRemoved = 0;

    @Override
    public void mutate() {
        for (ClassNode node : Application.getClasses().values()) {
            for (MethodNode mn : node.methods) {
                int offset = (mn.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;
                Type[] types = Type.getArgumentTypes(mn.desc);
                List<Integer> used = new ArrayList<>();
                mn.accept(new MethodVisitor() {
                    @Override
                    public void visitVarInsn(VarInsnNode var) {
                        int index = var.var - offset;
                        used.add(index);
                    }
                });
                if (used.size() == 0) continue;
                List<Integer> targets = new ArrayList<>();
                for (int i = offset; i < types.length; i++) {
                    if (!used.contains(i))
                        targets.add(i);
                }
                if (targets.size() == 0) continue;
                Collections.sort(targets, Collections.reverseOrder());
                remove(mn, targets);
                totalRemoved += targets.size();
            }
        }
        System.out.println("Removed " + totalRemoved + " unused parameters!");
    }

    private MethodNode remove(MethodNode mn, List<Integer> targets) {

        //        final int[] call = {0};
//        for (ClassData node : Application.getClasses().values()) {
//            for (MethodData method : node.methods) {
//                method.accept(new MethodVisitor(Opcodes.ASM5) {
//                    @Override
//                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//                        if (owner.equals(mn.owner.name) && name.equals(mn.name) && desc.equals(mn.desc)) {
//                            call[0]++;
//                            //alter param loads to not have @targets
//                        }
//                        super.visitMethodInsn(opcode, owner, name, desc, itf);
//                    }
//                });
//
//            }
//        }
        //  System.out.println("Altered "+ call[0] +" calls");
        String newDesc = "(";
        Type[] types = Type.getArgumentTypes(mn.desc);
        for (
                int i = 0;
                i < types.length; i++)

        {
            if (!targets.contains(i)) {
                newDesc += types[i].getDescriptor();
            }
        }

        newDesc += ")" + Type.getReturnType(mn.desc).getDescriptor();

      //  System.out.println(mn.owner.name+'.'+mn.name+ " "+mn.desc + " -> " + newDesc);

//        for (ClassData node : Application.getClasses().values()) {
//            for (MethodData method : node.methods) {
//                Arrays.stream(method.instructions.toArray()).filter(a -> a instanceof MethodInsnNode).forEach(a -> {
//                    MethodInsnNode min = (MethodInsnNode) a;
//                    if (min.owner.equals(mn.owner.name) && min.name.equals(mn.name) && min.desc.equals(mn.desc)) {
//                        int idx = method.instructions.indexOf(a);
//                        System.out.println(BasicBlock.containing(method.blocks, idx));
//                    }
//                });
//            }
//        }
        // System.out.println("changed " + mn.desc + " to " + newDesc);
        //  mn.desc = newDesc;
        return mn;
    }
}
