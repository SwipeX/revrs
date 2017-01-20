package pw.tdekk;

/**
 * Created by TimD on 6/21/2016.
 */

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VersionVisitor extends MethodVisitor {
    private int state = 0;
    private int version = -1;


    @Override
    public void visitIntInsn(IntInsnNode var) {
        if (state == 2) {
            version = var.operand;
            ++state;
        }

        if (var.operand == 765 || var.operand == 503) {
            ++state;
        }
    }

    public int getVersion() {
        return version;
    }
}