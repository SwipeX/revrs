package pw.tdekk.deob.cfg;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by TimD on 7/11/2016.
 * <p>
 * This class will assemble the blocks for a method using the visitor framework.
 * </p>
 */
public class BasicBlockAssembler extends MethodVisitor {

    private final MethodNode node;
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();
    private final ArrayList<Integer> targets = new ArrayList<>();
    private BasicBlock currentBlock;
    private String lastIdentifier;

    /**
     * @param node - the method from which the blocks will be generated.
     */
    public BasicBlockAssembler(MethodNode node) {
        this.node = node;
        buildTargets();
        currentBlock = new BasicBlock(node);
        node.accept(this);
        blocks.forEach(b -> b.setIdentifier(lastIdentifier = increment(lastIdentifier)));
    }

    /**
     * @return the list of blocks, post computation.
     */
    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    /**
     * Constructs an index of each possible target jump location.
     * The reason this must be used is that a basic block cannot contain any entry or exit except for the first and last instruction.
     */
    private final void buildTargets() {
        node.accept(new MethodVisitor() {
            @Override
            public void visitTableSwitchInsn(TableSwitchInsnNode tsin) {
                targets.add(node.instructions.indexOf(tsin.dflt));
                tsin.labels.forEach(l -> targets.add(node.instructions.indexOf(l)));
            }

            @Override
            public void visitLookupSwitchInsn(LookupSwitchInsnNode lsin) {
                targets.add(node.instructions.indexOf(lsin.dflt));
                lsin.labels.forEach(l -> targets.add(node.instructions.indexOf(l)));
            }

            @Override
            public void visitJumpInsn(JumpInsnNode jin) {
                targets.add(node.instructions.indexOf(jin.label));
            }

            @Override
            public void visitTryCatchBlock(TryCatchBlockNode tcbn) {
                targets.add(node.instructions.indexOf(tcbn.start));
                targets.add(node.instructions.indexOf(tcbn.end));
                targets.add(node.instructions.indexOf(tcbn.handler));
            }
        });
    }

    /**
     * This will visit any instruction before it is formally visited, mostly this just saves code space.
     *
     * @param ain - any instruction
     */
    public void visitAbstractInsn(AbstractInsnNode ain) {
        if (targets.contains(node.instructions.indexOf(ain))) {
            nextBlock();
        }
        currentBlock.getInstructions().add(ain);
    }

    /**
     * This will account for returns, which always end a block.
     *
     * @param in - any generic instruction as defined by ASM
     */
    @Override
    public void visitInsn(InsnNode in) {
        switch (in.getOpcode()) {
            case RETURN:
            case IRETURN:
            case ARETURN:
            case FRETURN:
            case DRETURN:
            case LRETURN:
            case ATHROW: {
                currentBlock.setType(BlockType.RETURN);
                nextBlock();
            }
        }
    }

    /**
     * This will account for all jump instructions - which always end a block
     *
     * @param jin - a jump instruction node
     */
    @Override
    public void visitJumpInsn(JumpInsnNode jin) {
        currentBlock.setType(jin.getOpcode() == Opcodes.GOTO ? BlockType.GOTO : BlockType.JUMP);
        nextBlock();
    }

    /**
     * Visiting this method will conclude the last block, and add it into the list.
     */
    public void visitEnd() {
        if (currentBlock != null && currentBlock.getInstructions().size() > 0) {
            blocks.add(currentBlock);
        }
    }

    /**
     * As long as the current block is not empty, this will create a fresh block and store the previous one.
     */
    private final void nextBlock() {
        if (currentBlock.getInstructions().size() > 0) {
            blocks.add(currentBlock);
            currentBlock = new BasicBlock(node);
        }
    }

    /**
     * @param src last value of a block.
     * @return the incemented value.
     * Hopefully we dont have a method with 26^2 blocks.
     */
    public static String increment(final String src) {
        if (src == null) return "AA";
        final int first = (src.charAt(0) - 'A') * 26;
        final int second = src.charAt(1) - 'A';
        final int next = (first + second + 1) % (26 * 26);
        return new String(new byte[]{(byte) (next / 26 + 'A'), (byte) (next % 26 + 'A')});
    }
}
