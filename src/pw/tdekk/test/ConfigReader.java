package pw.tdekk.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 08/06/2016
 * Time: 16:34
 *
 * @author Matt Collinge
 */
public class ConfigReader {

    private final String url;

    public ConfigReader() {
        this("http://oldschool.runescape.com/jav_config.ws");
    }

    public ConfigReader(String url) {
        this.url = url;
    }

    private String[] readConfig() {
        // Create the stream and reader so we can dispose of it nicely in the finally
        // Yes I could have used the new try with resources but #yolo
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        // Create what we will return, the lines of the config to make it easier to parse
        List<String> lines = new ArrayList<>();
        try {
            // Create the URL instance to read the file, open the stream and init the reader
            URL configUrl = new URL(url);
            inputStream = configUrl.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Read the config line by line and throw it into what we are going to return
            String line;
            while ((line = bufferedReader.readLine()) != null)
                lines.add(line);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close everything up nicely
                if (inputStream != null)
                    inputStream.close();
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Convert to array and return it
        return lines.toArray(new String[lines.size()]);
    }

    public Map<String, String> read() {
        // Read the config file
        String[] page = readConfig();
        // Create the parameter map we want to return
        HashMap<String, String> map = new HashMap<>();
        for (String parameter : page) {
            // Cleanse the string as we don't need "param=" or "msg="
            parameter = parameter.replace("param=", "").replace("msg=", "");
            // Split the string on the "=" sign and limit the split to 2 in case some of the parameters use the "=" sign
            String[] splitParameter = parameter.split("=", 2);
            // Check if the value is empty and add an empty parameter with the name
            if (splitParameter.length == 1)
                map.put(splitParameter[0], "");
            // Check there is a value and add the parameter with the value
            if (splitParameter.length == 2)
                map.put(splitParameter[0], splitParameter[1]);
        }
        // return our parameters
        return map;
    }
}