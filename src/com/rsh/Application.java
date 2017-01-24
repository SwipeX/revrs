package com.rsh;

import com.rsh.util.Store;
import pw.tdekk.deob.usage.UnusedMembers;
import pw.tdekk.util.Archive;

import javax.swing.*;
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
        Store.createFiles();
        JFrame frame = getFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        menuBar.add(file);
        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(e -> {
            FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
            fd.setDirectory(Store.getHomeDirectory());
            fd.setFile("*.jar");
            fd.setVisible(true);
            String filename = fd.getDirectory() + File.separator + fd.getFile();
            Store.loadClasses(filename);
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
    }

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(getFrame(), infoMessage, "Message: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static JFrame getFrame() {
        return frame != null ? frame : (frame = new JFrame(NAME));
    }
}
