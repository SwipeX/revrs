package com.rsh.tree;

import com.alee.extended.tree.WebAsyncTreeCellRenderer;
import com.alee.laf.tree.WebTreeElement;
import com.alee.laf.tree.WebTreeUI;
import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;

/**
 * Sample asynchronous tree cell renderer.
 *
 * @author Mikle Garin
 */

public class PositionNodeRenderer extends WebAsyncTreeCellRenderer {
    /**
     * Returns custom tree cell renderer component
     *
     * @param tree       tree
     * @param value      cell value
     * @param isSelected whether cell is selected or not
     * @param expanded   whether cell is expanded or not
     * @param leaf       whether cell is leaf or not
     * @param row        cell row number
     * @param hasFocus   whether cell has focusor not
     * @return renderer component
     */
    @Override
    public WebTreeElement getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected,
                                                       final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);

        if (value instanceof PositionNode) {
            final PositionNode node = (PositionNode) value;

            // Node icon
            if (!node.isLoading()) {
                // Type icon
                final ImageIcon icon;
                switch (node.getType()) {
                    case JAR: {
                        icon = WebTreeUI.ROOT_ICON;
                        setText(Store.getLastVersion() + ".jar");
                        break;
                    }
                    case PARENT: {
                        icon = expanded ? WebTreeUI.LEAF_ICON : WebTreeUI.CLOSED_ICON;
                        break;
                    }
                    case CHILD: {
                        icon = WebTreeUI.OPEN_ICON;
                        break;
                    }
                    default: {
                        icon = null;
                        break;
                    }
                }
                setIcon(node.isFailed() ? getFailedStateIcon(icon) : icon);
            }
            ClassNode classNode = node.getClassNode();
            if (node.getType() != PositionType.JAR)
                setText(classNode == null ? "N/A" : classNode.name);
        }

        return this;
    }
}