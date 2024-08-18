package bulls.tool.conf;

import bulls.exception.ConfigurationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

public class Configuration {

    public Properties properties = new Properties();

    public Configuration(String configurationFileName) throws ConfigurationException {

        try {
            final FileInputStream fin = new FileInputStream(configurationFileName);
            properties.load(new InputStreamReader(fin, StandardCharsets.UTF_8));
            fin.close();
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(String.format("Configuration file does not exist (%s)", configurationFileName), e);
        } catch (IOException e) {
            throw new ConfigurationException(String.format("Failed to read configuration file (%s)", configurationFileName), e);
        }
    }

    public String getString(String key) {
        return properties.getProperty(key);

    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }


    public Integer getInteger(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        // will throw NumberFormatException
        return Integer.parseInt(valueString);
    }

    public Integer getInteger(String key, int defaultValue) {
        Integer value = getInteger(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Double getDouble(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        // will throw NumberFormatException
        return Double.parseDouble(valueString);
    }

    public Boolean getBoolean(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        return valueString.equals("true");
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        Boolean value;
        if ((value = getBoolean(key)) == null) {
            return defaultValue;
        }
        return value;
    }

    public Long getLong(String key) {
        String valueString = getString(key);
        if (valueString == null) {
            return null;
        }
        // will throw NumberFormatException
        return Long.parseLong(valueString);
    }

    public Long getLong(String key, long defaultValue) {
        Long value = getLong(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> keys() {
        return (Enumeration<String>) properties.propertyNames();
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }
}