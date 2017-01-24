package com.rsh;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.rootpane.WebFrame;
import com.rsh.util.Store;
import pw.tdekk.deob.usage.UnusedMembers;
import pw.tdekk.util.Archive;
import pw.tdekk.util.Crawler;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by TimD on 1/20/2017.
 * Probably should try not to have such a clutter in main...
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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);

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
        file.add(exit);
        frame.setLayout(new BorderLayout());
        frame.add(menuBar, BorderLayout.NORTH);
        frame.setVisible(true);

        if (Store.getCrawler().outdated()) {
            int result = WebOptionPane.showConfirmDialog(frame, "Would you like to update", "Outdated!", WebOptionPane.YES_NO_OPTION);
            if (result == WebOptionPane.YES_OPTION) {
                Store.updateJar();
            }
        }else{
            Store.loadClasses(Store.getHighestRevision().getPath());
        }
    }

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(getFrame(), infoMessage, "Message: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static JFrame getFrame() {
        return frame != null ? frame : (frame = new WebFrame(NAME));
    }
}
