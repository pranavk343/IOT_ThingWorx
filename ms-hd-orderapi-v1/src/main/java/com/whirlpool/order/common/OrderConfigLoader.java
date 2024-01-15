package com.whirlpool.order.common;

import com.whirlpool.order.exception.UtilityException;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.whirlpool.order.common.OrderConstants.*;

@UtilityClass
public class OrderConfigLoader {

    Logger logger = LoggerFactory.getLogger(OrderConfigLoader.class);
    private static final Properties properties;

    static {
        properties = loadProperties();
    }

    public static String getRfcDestination() {
        return properties.getProperty(SAP_RFC_DESTINATION);
    }

    private static Properties loadProperties() {
        try {
            return PropertiesLoader.loadProperties(System.getenv(CONFIG_FILE));
        } catch (UtilityException e) {
            logger.error(e.getMessage());
        }
        return new Properties();
    }
}