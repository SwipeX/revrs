package com.rsh.util;

import com.alee.laf.panel.WebPanel;
import com.rsh.Application;
import com.rsh.miu.ClassIdentity;
import com.rsh.ui.DownloadProgress;
import org.objectweb.asm.tree.ClassNode;
import pw.tdekk.VersionVisitor;
import pw.tdekk.test.App;
import pw.tdekk.util.Archive;
import pw.tdekk.util.Crawler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

/**
 * Created by TimD on 1/23/2017.
 */
public class Store {
    private static ConcurrentHashMap<String, ClassNode> classes = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ClassIdentity> classIdentities = new ConcurrentHashMap<>();
    private static final String HOME_DIR = System.getProperty("user.home") + File.separator + "revrs";
    private static final String MIU_DIR = HOME_DIR + File.separator + "miu";
    private static final String JAR_DIR = HOME_DIR + File.separator + "jars";
    private static final String DEOB_DIR = JAR_DIR + File.separator + "deob";
    private static final String[] DIRECTORIES = new String[]{HOME_DIR, MIU_DIR, JAR_DIR, DEOB_DIR};
    private static int lastVersion = -1;
    private static Crawler crawler = new Crawler();

    public static Crawler getCrawler() {
        return crawler;
    }

    public static int getLastVersion() {
        return lastVersion;
    }

    public static void createFiles() {
        Arrays.stream(DIRECTORIES).forEach(d -> new File(d).mkdirs());
    }

    public static ConcurrentHashMap<String, ClassNode> getClasses() {
        return classes;
    }

    public static ConcurrentHashMap<String, ClassIdentity> getClassIdentities() {
        return classIdentities;
    }

    static {
        loadIdentifiers();
    }

    public static void loadClasses(String filename) {
        try {
            loadClasses(new JarFile(filename, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadIdentifiers() {
        File[] files = new File(getMiuDirectory()).listFiles();
        if (files != null) {
            for (File file : files) {
                ClassIdentity identity = new ClassIdentity(file.getAbsolutePath());
                classIdentities.put(identity.getIdentity(), identity);
            }
        }
    }

    public static File getHighestRevision() {
        int highest = 0;
        File jar = null;
        for (File file : new File(JAR_DIR).listFiles()) {
            if (file == null || file.isDirectory()) continue;
            String name = file.getName();
            if (name.endsWith(".jar")) {
                int revision = Integer.parseInt(name.replace(".jar", ""));
                if (revision > highest) {
                    jar = file;
                    highest = revision;
                }
            }
        }
        return jar;
    }

    public static void loadClasses(JarFile file) {
        try {
            classes = Archive.build(file);
            VersionVisitor versionVisitor = new VersionVisitor();
            classes.get("client").getMethod("init", "()V").accept(versionVisitor);
            int version = versionVisitor.getVersion();
            lastVersion = version;
            File dest = new File(JAR_DIR + File.separator + version + ".jar");
            if (!dest.exists()) {
                //have to use file copy due to the resources not being verified, which means not loaded.
                Files.copy(new File(file.getName()).toPath(), dest.toPath());
            }
            Application.getFrame().setTitle("revrs - OSRS #" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateJar() {
        new Thread(() -> {
            try {
                if (crawler.outdated()) {
                    WebPanel panel = Application.getPanel();
                    panel.removeAll();
                    panel.add(new DownloadProgress(), BorderLayout.SOUTH);
                    Application.getFrame().setVisible(true);
                    crawler.download();
                    panel.removeAll();
                    Application.infoBox("Updated", "Updated");
                    loadClasses(HOME_DIR + File.separator + "os_pack.jar");
                } else {
                    Application.infoBox("Already up to date!", "Connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static String getHomeDirectory() {
        return HOME_DIR;
    }

    public static String getJarDirectory() {
        return JAR_DIR;
    }

    public static String getMiuDirectory() {
        return MIU_DIR;
    }

    public static String getDeobDirectory() {
        return DEOB_DIR;
    }
}
