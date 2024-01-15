package com.whirlpool.order;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static com.whirlpool.order.common.OrderConfigLoader.getRfcDestination;
import static com.whirlpool.order.common.OrderConstants.CONFIG_FILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SystemStubsExtension.class)
public class OrderConfigLoaderTest {

    public static final String APPLICATION_QA_PROPERTIES = "src/main/resources/application-qa.properties";
    @SystemStub
    private static EnvironmentVariables environmentVariables;

    @BeforeAll
    static void init() {
        environmentVariables.set(CONFIG_FILE, APPLICATION_QA_PROPERTIES);
    }

    @Test
    void testPropertiesInitialization() {
        assertEquals("ABAP_AS1", getRfcDestination());
    }
}
