package dsb.sunny;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class BotProperties {

    private Properties properties;
    private File file = new File("bot.properties");

    public BotProperties() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadProperties() {

        try {
            FileReader reader = new FileReader(file);
            Properties props = new Properties();

            props.load(reader);
            reader.close();

            this.properties = props;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String property) {
        if (properties == null) {
            throw new NullPointerException("Properties can not be null. Did you forget to load the properties?");
        }
        return (String) properties.get(property);
    }
}
