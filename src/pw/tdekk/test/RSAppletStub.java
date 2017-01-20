package pw.tdekk.test;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Date: 08/06/2016
 * Time: 16:32
 *
 * @author Matt Collinge
 */
public class RSAppletStub implements AppletStub {

    private final Map<String, String> parameters;
    private final RSAppletContext appletContext;
    private boolean active = false;

    public RSAppletStub(Map<String, String> parameters) {
        this.parameters = parameters;
        appletContext = new RSAppletContext();
    }

    @Override
    public boolean isActive() {
        // Lets everything know that it is alive
        return active;
    }

    public void setActive(boolean active) {
        // A setter method for us so we can say we've started the Applet
        this.active = active;
    }

    @Override
    public URL getDocumentBase() {
        // Return the codebase parameter from our parameter map, the document base is exactly the same as the codebase
        try {
            return new URL(parameters.get("codebase"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Document Base URL");
        }
    }

    @Override
    public URL getCodeBase() {
        // Return the codebase parameter from our parameter map
        try {
            return new URL(parameters.get("codebase"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Code Base URL");
        }
    }

    @Override
    public String getParameter(String name) {
        // Get the requested parameter from the map
        if (parameters.containsKey(name))
            return parameters.get(name);
        return null;
    }

    @Override
    public RSAppletContext getAppletContext() {
        // Return our instance of RSAppletContext so we can fake the environment
        return appletContext;
    }

    @Override
    public void appletResize(int width, int height) {
        // So the environment can set the applet getSize
        Applet applet = getAppletContext().getApplet("main");
        if (applet != null)
            applet.resize(width, height);
    }
}