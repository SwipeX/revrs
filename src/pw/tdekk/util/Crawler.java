package pw.tdekk.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;

public class Crawler {

    private final Map<String, String> parameters = new HashMap<>();

    private static final int[] WORLDS = {
            1, 2, 5, 6, 9, 10, 13, 14, 17, 21, 22, 26, 29, 30, 33, 34, 38,
            41, 42, 45, 46, 49, 50, 53, 54, 58, 61, 62, 66, 70, 74, 77, 78
    };

    private final String home;
    private final String config;
    private final String pack;

    private int hash = -1;

    public Crawler() {
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
        this.home = "http://oldschool" +
               WORLDS[(int) (Math.random() * WORLDS.length)] + ".runescape.com/";
        this.config = home + "jav_config.ws";
        this.pack = "os_pack.jar";
        crawl();
    }

    private int getLocalHash() {
        try {
            URL url = new File(pack).toURI().toURL();
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (MalformedURLException e) {
            return -1;
        }
    }


    private int getRemoteHash() {
        try {
            URL url = new URL(home + parameters.get("initial_jar"));
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (IOException e) {
            return -1;
        }
    }

    public boolean outdated() {
        File gamepack = new File(pack);
        if (!gamepack.exists()) {
            return true;
        }
        if (hash == -1) {
            hash = getLocalHash();
        }
        return hash == -1 || hash != getRemoteHash();
    }

    private boolean crawl() {
        try {
            List<String> source = Internet.read(config);
            for (String line : source) {
                if (line.startsWith("param=")) {
                    line = line.substring(6);
                }
                int idx = line.indexOf("=");
                if (idx == -1) {
                    continue;
                }
                parameters.put(line.substring(0, idx), line.substring(idx + 1));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean download(String target) {
        hash = getRemoteHash();
        return Internet.download(home + parameters.get("initial_jar"), target) != null;
    }

    public boolean download() {
        return download(pack);
    }

    public Dimension getAppletSize() {
        return new Dimension(Integer.parseInt(parameters.get("applet_minwidth")),
                Integer.parseInt(parameters.get("applet_minheight")));
    }
}