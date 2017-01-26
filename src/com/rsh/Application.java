package com.rsh;

import com.alee.extended.tree.WebAsyncTree;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.rsh.miu.ClassIdentity;
import com.rsh.ui.ClassIdentityEditor;
import com.rsh.ui.tree.ASyncNodeProvider;
import com.rsh.ui.tree.PositionNode;
import com.rsh.ui.tree.PositionNodeRenderer;
import com.rsh.util.Store;
import org.objectweb.asm.tree.ClassNode;
import pw.tdekk.deob.usage.UnusedMembers;
import pw.tdekk.util.Archive;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by TimD on 1/20/2017.
 */
public class Application {

    private static JFrame frame;
    private static final String NAME = "revrs";

    public static void main(String[] args) {
        WebLookAndFeel.install();
        WebLookAndFeel.initializeManagers();
        WebLookAndFeel.setDecorateAllWindows(true);

        Store.createFiles();

        WebFrame frame = (WebFrame) getFrame();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addMenuItems(frame);
        frame.setVisible(true);
        if (Store.getCrawler().outdated()) {
            int result = WebOptionPane.showConfirmDialog(frame, "Would you like to update", "Outdated!", WebOptionPane.YES_NO_OPTION);
            if (result == WebOptionPane.YES_OPTION) {
                Store.updateJar();
            }
        } else {
            Store.loadClasses(Store.getHighestRevision().getPath());
        }
    }

    private static void addMenuItems(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        menuBar.add(file);

        JMenuItem view = new JMenu("View");
        menuBar.add(view);

        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(e -> {
            WebFileChooser fd = new WebFileChooser(Store.getHomeDirectory());
            fd.setMultiSelectionEnabled(false);
            fd.setAcceptAllFileFilterUsed(false);
            fd.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".jar");
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            if (fd.showOpenDialog(frame) == WebFileChooser.APPROVE_OPTION) {
                String filename = fd.getSelectedFile().getAbsolutePath();
                Store.loadClasses(filename);
            }
        });
        file.add(load);

        JMenuItem update = new JMenuItem("Update");
        update.addActionListener(e -> Store.updateJar());
        file.add(update);

        JMenuItem deob = new JMenuItem("Deob");
        deob.addActionListener(e -> {
            if (Store.getClasses().size() < 1) {
                infoBox("You need to load classes first", "Deobbing Issue");
            } else {
                new UnusedMembers().mutate();
                //allow for custom deobbing here
                Archive.write(new File(Store.getDeobDirectory() + File.separator + Store.getLastVersion() + "_deob.jar"), Store.getClasses());
            }
        });
        file.add(deob);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(1));
        file.add(exit);

        JMenuItem hierarchy = new JMenuItem("Class Hierarchy");
        hierarchy.addActionListener(e -> {
            // Create data provider
            ASyncNodeProvider dataProvider = new ASyncNodeProvider();

            // Create a tree based on your data provider
            WebAsyncTree<PositionNode> asyncTree = new WebAsyncTree<>(dataProvider);
            asyncTree.setVisibleRowCount(8);
            asyncTree.setEditable(true);
            asyncTree.setCellRenderer(new PositionNodeRenderer());
            WebScrollPane webScrollPane = new WebScrollPane(asyncTree);
            webScrollPane.getVerticalScrollBar().setUnitIncrement(35);
            // Show an example frame
            frame.getContentPane().removeAll();
            frame.getContentPane().add(webScrollPane);
            frame.setVisible(true);
        });
        view.add(hierarchy);

        JMenuItem classIdEditor =  new JMenuItem("Class Identity Editor");
        classIdEditor.addActionListener(e -> {
            ClassIdentity ci = new ClassIdentity("C:\\Users\\TimD\\revrs\\miu\\Node.txt");
            for(ClassNode node : Store.getClasses().values()){
                if(ci.matches(node))
                    System.out.println(node.name + "="+ci.getIdentity());
            }
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new ClassIdentityEditor());
        });
        view.add(classIdEditor);

        frame.setLayout(new BorderLayout());
        frame.add(menuBar, BorderLayout.NORTH);
    }

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(getFrame(), infoMessage, "Message: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static JFrame getFrame() {
        return frame != null ? frame : (frame = new WebFrame(NAME));
    }

    public static ImageIcon getIcon(String name) {
        return new ImageIcon(Application.class.getResource("ui/icon/" + name));
    }
}
