package de.bundeswehr.uniity.sedapexpress.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

class SEDAPExpressMQTTClientTest {
    // Inspect configuration only: constructing these clients never contacts a broker.
    private String root(SEDAPExpressMQTTClient client) throws Exception {
        Field field = SEDAPExpressMQTTClient.class.getDeclaredField("mqttRoot");
        field.setAccessible(true);
        return (String) field.get(client);
    }

    @Test
    void defaultConstructorsUseIcdRoot() throws Exception {
        assertEquals("UNIITY-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender")));
        assertEquals("UNIITY-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", List.of())));
        assertEquals("UNIITY-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", "user", "password", List.of())));
        assertEquals("UNIITY-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", "user", "password",
                (InputStream) null, null, null, List.of())));
    }

    @Test
    void explicitRootsPreserveLegacyAndCustomNamespaces() throws Exception {
        assertEquals("SEDAP-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", List.of(), "SEDAP-X")));
        assertEquals("custom/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", List.of(), "custom/")));
        assertEquals("SEDAP-X/test-sender", root(new SEDAPExpressMQTTClient("tcp://localhost", "test-sender", "user", "password",
                (InputStream) null, null, null, List.of(), "SEDAP-X/")));
    }
}
