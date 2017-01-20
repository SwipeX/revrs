package pw.tdekk.test;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Date: 08/06/2016
 * Time: 16:33
 *
 * @author Matt Collinge
 */
public class RSAppletContext implements AppletContext {

    private final Map<String, InputStream> streams = new HashMap<>();
    private Applet applet;

    @Override
    public AudioClip getAudioClip(URL url) {
        // Use the Java Applet implementation of getting an AudioClip
        return Applet.newAudioClip(url);
    }

    @Override
    public Image getImage(URL url) {
        try {
            // Pretty standard stuff, as long as we return an Image we are good
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Applet getApplet(String name) {
        // Return the Runescape Applet
        return applet;
    }

    @Override
    public Enumeration<Applet> getApplets() {
        // Create a Vector of Applets and add the Runescape one to it
        // We use a Vector because it's the easiest way to get an Enumeration<Applet>
        Vector<Applet> applets = new Vector<>();
        applets.add(applet);
        return applets.elements();
    }

    @Override
    public void showDocument(URL url) {
        // Make Java open up the requested url if it's supported
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Unable to open document " + url.getPath());
            }
        }
    }

    @Override
    public void showDocument(URL url, String target) {
        // Make Java open up the requested url if it's supported
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Unable to open document " + url.getPath());
            }
        }
    }

    @Override
    public void showStatus(String status) {
        // We don't really need to do anything with the status
    }

    @Override
    public void setStream(String key, InputStream stream) throws IOException {
        // Basic implementation of a stream map that the AppletContext requires
        if (streams.containsKey(key)) {
            streams.remove(key);
        }
        streams.put(key, stream);
    }

    @Override
    public InputStream getStream(String key) {
        // Basic implementation of a stream map that the AppletContext requires
        return streams.get(key);
    }

    @Override
    public Iterator<String> getStreamKeys() {
        // Return the keys to the stream map
        return streams.keySet().iterator();
    }

    public void setApplet(Applet applet) {
        // Our one and only method we are going to add to this. It sets the Applet so we can return it and the Enumeration of Applets
        this.applet = applet;
    }
}