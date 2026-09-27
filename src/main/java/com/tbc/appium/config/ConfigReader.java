package com.tbc.appium.config;

import com.tbc.appium.exceptions.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads test configuration from {@code config.properties} on the classpath.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new FrameworkException("Configuration file '" + CONFIG_FILE + "' was not found on the classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new FrameworkException("Failed to read configuration file '" + CONFIG_FILE + "'", e);
        }
        return props;
    }

    /** Returns the value for {@code key}, or an empty string if it is not set. */
    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key, ""));
        return value.trim();
    }

    /** Returns the value for {@code key}, failing fast with a message if it is missing. */
    public static String getRequired(String key) {
        String value = get(key);
        if (value.isEmpty()) {
            throw new FrameworkException("Required configuration property '" + key + "' is not set");
        }
        return value;
    }

    public static int getInt(String key) {
        String value = getRequired(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new FrameworkException("Configuration property '" + key + "' must be an integer but was '" + value + "'", e);
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
