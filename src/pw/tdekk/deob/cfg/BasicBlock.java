package pw.tdekk.deob.cfg;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;

/**
 * Created by TimD on 7/11/2016.
 * Encapsulates a list of instructions that have only an entry on the first instruction and an exit on the last instruction.
 * A block may have infinite entry points, and up to two exit paths.
 * Parent and children fields will be set from TreeNode upon generating a CFG.
 */
public class BasicBlock {

    private final ArrayList<AbstractInsnNode> instructions = new ArrayList();
    private final MethodNode owner;
    private BlockType type = BlockType.NORMAL;
    private String identifier = "";

    public BasicBlock(MethodNode owner) {
        this.owner = owner;

    }

    public ArrayList<AbstractInsnNode> getInstructions() {
        return instructions;
    }

    /**
     * @return the last instruction in this block, if it exists
     */
    public AbstractInsnNode getLastInstruction() {
        return instructions == null || instructions.isEmpty() ? null : instructions.get(instructions.size() - 1);
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public String toString() {
        final String[] result = {" { \r\n"};
        instructions.stream().filter(i-> i.getOpcode()>0).forEach(i -> result[0] += Opcodes.OPCODES[i.getOpcode()] + " \r\n");
        return result[0] + " }\r\n";
    }

    public void setIdentifier(String input) {
        identifier = input;
    }

    public String getIdentifier() {
        return identifier;
    }

}
