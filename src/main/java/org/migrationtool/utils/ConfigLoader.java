package org.migrationtool.utils;

import org.migrationtool.exceptions.ConfigLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    private ConfigLoader() {}

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new ConfigLoadException("Configuration file 'database.properties' not found in resources!");
            }

            properties.load(input);
            logger.info("Configuration file loaded successfully.");
        } catch (IOException e) {
            throw new ConfigLoadException("Error loading configuration: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Requested configuration key '{}' is missing!", key);
        } else {
            logger.debug("Retrieved config key '{}': {}", key, value);
        }
        return value;
    }
}
