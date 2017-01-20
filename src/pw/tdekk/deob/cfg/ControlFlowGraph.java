package pw.tdekk.deob.cfg;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by TimD on 7/12/2016.
 * This class encompasses a graph to keep track of the flow of blocks throughout the method.
 */
public class ControlFlowGraph extends DirectedGraph<BasicBlock, BasicBlock> {

    private final BasicBlock head;
    private final MethodNode method;
    private final ArrayList<BasicBlock> blocks;

    public ControlFlowGraph(MethodNode mn) {
        System.out.println(mn.name + " " + mn.blocks.size());
        this.method = mn;
        blocks = method.blocks;
        head = blocks.get(0);
        head.setType(BlockType.HEADER);
    }

    /**
     * Builds the resulting graph based on block flow.
     * This uses a Depth-First-Search (DFS) algorithm to trace the block execution.
     */
    public final ControlFlowGraph generate() {
        for (BasicBlock block : blocks) {
            addVertex(block);
            AbstractInsnNode last = block.getLastInstruction();
            if (last != null) {
                if (last instanceof JumpInsnNode) {
                    addEdge(block, blockFrom(((JumpInsnNode) last).label));
                    if (last.getOpcode() != Opcodes.GOTO) {
                        //Jumps also can keep going down the tree, GOTOs cannot.
                        addEdge(block, successor(block));
                    }
                } else {
                    //a return would halt, all other types of blocks continue the tree.
                    if (block.getType().equals(BlockType.NORMAL)) {
                        addEdge(block, successor(block));
                    }
                }
            }
        }
        for (TryCatchBlockNode tcbn : method.tryCatchBlocks) {
            int start = method.instructions.indexOf(tcbn.start);
            int end = method.instructions.indexOf(tcbn.end);
            List<BasicBlock> filter = blocks.stream().filter(block -> method.instructions.indexOf(block.getInstructions().get(0)) >= start &&
                    method.instructions.indexOf(block.getLastInstruction()) <= end).collect(Collectors.toList());
            BasicBlock handler = blockFrom(tcbn.handler);
            handler.setType(BlockType.HANDLER);
            filter.forEach(block -> addEdge(block, handler));
        }
        ArrayList<BasicBlock> toRemove = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            if (edgesTo(block).size() == 1) {
                Set<BasicBlock> from = edgesFrom(block);
                if (from != null && from.size() == 1) {
                    BasicBlock next = (BasicBlock) from.toArray()[0];
                    if (next != null && edgesTo(next).size() == 1) {
                        join(block, next);
                        toRemove.add(next);
                    }
                }
            }
        }
        blocks.removeAll(toRemove);
        return this;
    }


    /**
     * @param ain - the AbstractInsnNode to use to determine the block it is contained in.
     * @return the corresponding block if it exists.
     */
    private final BasicBlock blockFrom(AbstractInsnNode ain) {
        int offset = ain instanceof LabelNode ? 1 : 0;
        int idx = method.instructions.indexOf(ain);
        //offset accounts for label + line number
        if (method.instructions.get(idx + 1).getOpcode() < 0) {
            offset++;
        }
        return blockFrom(idx + offset);
    }

    /**
     * @param index - index used to determine the block it is contained in.
     * @return the corresponding block if it exists.
     */
    private final BasicBlock blockFrom(int index) {
        for (BasicBlock block : blocks) {
            AbstractInsnNode start = block.getInstructions().get(0);
            AbstractInsnNode end = block.getLastInstruction();
            //if the instruction range resides in this block
            if (index >= method.instructions.indexOf(start) && index <= method.instructions.indexOf(end))
                return block;
        }
        return null;
    }

    /**
     * @return The block that begins the method.
     */
    public BasicBlock getHead() {
        return head;
    }

    /**
     * @return All blocks that will exit the method
     */
    public List<BasicBlock> getExits() {
        return blocks.stream().filter(b -> b.getType().equals(BlockType.RETURN)).collect(Collectors.toList());
    }

    /**
     * @param block - the block to search for the successor of.
     * @return the successor block if it exists.
     */
    private final BasicBlock successor(BasicBlock block) {
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (blocks.get(i).equals(block))
                return blocks.get(i + 1);
        }
        return null;
    }

    /**
     * This method joins together two blocks <3, BlockType of the second block will persist.
     *
     * @param accept Block that will remain
     * @param insert Block that will be inserted at the end of accept
     * @Warning may not be fully working
     */
    private final void join(BasicBlock accept, BasicBlock insert) {
        accept.getInstructions().addAll(insert.getInstructions());
        accept.setType(insert.getType());
        removeEdge(accept, insert);
        edgesFrom(insert).forEach(e -> addEdge(accept, e));
        removeVertex(insert);
    }

    /**
     * @return a DOT formatting of the graph.
     */
    public String toString() {
        Set<String> shapes = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\r\n");
        for (BasicBlock block : blocks) {
            edgesFrom(block).forEach(edge -> {
                builder.append(block.getIdentifier() + " -> " + edge.getIdentifier());
                if (edge.getType().equals(BlockType.RETURN)) {
                    builder.append("[color=red,penwidth=3.0]");
                    shapes.add(edge.getIdentifier() + "[shape=Msquare];");
                } else if (edge.getType().equals(BlockType.HANDLER)) {
                    builder.append("[label=\"Handler\"][color=blue,penwidth=3.0]");
                    shapes.add(edge.getIdentifier() + "[shape=doubleoctagon];");
                } else if (block.getLastInstruction().getOpcode() == Opcodes.GOTO) {
                    if (!block.equals(edge)) {
                        builder.append("[label=\"GOTO\"][color=green,penwidth=3.0]");
                    }
                } else {
                    if (!successor(block).equals(edge))
                        builder.append("[label=\"IF\"][color=yellow,penwidth=3.0]");
                }
                builder.append(";\r\n");
            });
        }
        builder.append(head.getIdentifier() + "[shape=Mdiamond];\r\n");
        shapes.forEach(s -> builder.append(s + "\r\n"));
        return builder.append("}").toString();
    }

}
