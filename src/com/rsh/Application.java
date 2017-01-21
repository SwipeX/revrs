package com.rsh;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by TimD on 1/20/2017.
 */
public class Application {

    private static JFrame frame;
    private static final String NAME = "revrs";

    public static void main(String[] args) {
        JFrame frame = getFrame();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        JMenuBar menuBar = new JMenuBar();
        JMenuItem file = new JMenuItem("File");
    }

    public static JFrame getFrame() {
        return frame != null ? frame : (frame = new JFrame(NAME));
    }
}
