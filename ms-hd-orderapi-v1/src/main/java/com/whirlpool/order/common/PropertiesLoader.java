package com.whirlpool.order.common;

import com.whirlpool.order.exception.UtilityException;
import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertiesLoader {

    public static Properties loadProperties(String configFile) throws UtilityException {
        Properties configuration = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
            configuration.load(fileInputStream);
        } catch (IOException e) {
            throw new UtilityException("Error loading properties from file: " + configFile, e);
        }
        return configuration;
    }
}
