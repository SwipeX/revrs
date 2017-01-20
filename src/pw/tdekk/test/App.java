package pw.tdekk.test;

import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * Date: 08/06/2016
 * Time: 17:07
 *
 * @author Matt Collinge
 */
public class App {

    public static void main(String[] args) {
        // Create an instance of our config reader and parse the parameters
        ConfigReader configReader = new ConfigReader();
        Map<String, String> map = configReader.read();
        // Use the parameters to build the jar location we are loading

        try {
            // Use a URLClassLoader because we are loading classes from a jar at a URL
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("test.jar").toURI().toURL()});
            // Use the parameters to get the correct class, usually always "client"
            Class<?> clientClass = classLoader.loadClass(map.get("initial_class").replace(".class", ""));
            // Create a new instance of the class and cast it to an Applet so we can use it
            Applet applet = (Applet) clientClass.newInstance();
            // Create our stub so we can set the AppletStub of the Applet and pass in the parsed parameters
            RSAppletStub appletStub = new RSAppletStub(map);
            // Use our setter to set the Applet in the AppletContext
            appletStub.getAppletContext().setApplet(applet);
            // Set the AppletStub of the Applet
            applet.setStub(appletStub);
            // Turn the key and start the Applet up
            applet.init();
            // Set the getSize, this can also be done by reading the parameters, but I was too lazy to parse the Int's
            applet.setSize(765, 503);
            // Using our setter, make it so everything knows the Applet is active
            appletStub.setActive(true);

            // Create a JFrame and add the Applet to it
            JFrame frame = new JFrame("Test Runescape");
            frame.setSize(800, 600);
            JPanel panel = new JPanel();
            frame.add(panel);
            panel.add(applet);
            frame.pack();
            frame.setVisible(true);
        } catch (MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}