import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    Properties prop;
    InputStream input;

    public PropertiesReader() throws FileNotFoundException {
        prop = new Properties();
        input = null;
        readProperties();
    }

    private void readProperties() {
        try {
            input = new FileInputStream("game.properties");
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return prop.getProperty("Username");
    }

    public String getAIDifficulty() {
        return prop.getProperty("AIDifficulty");
    }

    public String getEnableAESEncryption() {
        return prop.getProperty("EnableAESEncrpytion");
    }

    public String getEnableSound() {
        return prop.getProperty("EnableSound");
    }

    public String getBoardsize() {
        return prop.getProperty("Boardsize");
    }
}
