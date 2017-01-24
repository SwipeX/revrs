package com.rsh.util;

import com.rsh.Application;
import org.objectweb.asm.tree.ClassNode;
import pw.tdekk.VersionVisitor;
import pw.tdekk.util.Archive;
import pw.tdekk.util.Crawler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

/**
 * Created by TimD on 1/23/2017.
 */
public class Store {
    private static ConcurrentHashMap<String, ClassNode> classes = new ConcurrentHashMap<>();
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

    public static void loadClasses(String filename) {
        try {
            loadClasses(new JarFile(filename, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Archive.write(dest, classes);
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
                    crawler.download();
                    Application.infoBox("Jar updated!", "Done");
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

    public static String getDeobDirectory() {
        return DEOB_DIR;
    }
}
