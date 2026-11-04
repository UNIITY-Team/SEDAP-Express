package de.bundeswehr.uniity.sedapexpress.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * Tests MAC generation and verification. The MAC field itself is excluded
 * from the authenticated message data. Verification must preserve the
 * received MAC while comparing it with the newly calculated MAC.
 */
class MACUtilsTest {

    private byte[] key;
    private byte[] wrongKey;
    private byte[] iv;
    private static final String PAYLOAD = "Test payload";

    @BeforeEach
    void setUp() {
        SecureRandom random = new SecureRandom();
        key = new byte[32];
        wrongKey = new byte[32];
        iv = new byte[12];
        random.nextBytes(key);
        random.nextBytes(wrongKey);
        random.nextBytes(iv);
    }

    @Test
    void hmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWithHMAC(key, m),
                64);
    }

    @Test
    void cmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWithCMAC(key, m),
                8);
    }

    @Test
    void gmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWithGMAC(key, iv, m),
                8);
    }

    @Test
    void truncatedHmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWith32BitHMAC(key, m),
                8);
    }

    @Test
    void truncatedCmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWith32BitCMAC(key, m),
                8);
    }

    @Test
    void truncatedGmacGenerationIgnoresExistingMac() {
        assertSameMac(
                m -> MACUtils.setMACFieldWith32BitGMAC(key, iv, m),
                8);
    }

    @Test
    void hmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithHMAC(key, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, message));
    }

    @Test
    void cmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithCMAC(key, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, message));
    }

    @Test
    void gmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithGMAC(key, iv, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, iv, message));
    }

    @Test
    void truncatedHmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWith32BitHMAC(key, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, message));
    }

    @Test
    void truncatedCmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWith32BitCMAC(key, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, message));
    }

    @Test
    void truncatedGmacVerificationSucceedsAndPreservesReceivedMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWith32BitGMAC(key, iv, message);
        assertVerificationPreservesMac(message, () -> MACUtils.isMessageAuthentic(key, iv, message));
    }

    @Test
    void verificationRejectsChangedPayloadAndPreservesMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithHMAC(key, message);
        String originalMac = message.getMAC();
        message.setPayload("Modified payload");

        assertFalse(MACUtils.isMessageAuthentic(key, message));
        assertEquals(originalMac, message.getMAC());
    }

    @Test
    void verificationRejectsWrongKeyAndPreservesMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithHMAC(key, message);
        String originalMac = message.getMAC();

        assertFalse(MACUtils.isMessageAuthentic(wrongKey, message));
        assertEquals(originalMac, message.getMAC());
    }

    @Test
    void gmacVerificationRejectsWrongIvAndPreservesMac() {
        TestMessage message = new TestMessage(PAYLOAD, null);
        MACUtils.setMACFieldWithGMAC(key, iv, message);
        String originalMac = message.getMAC();
        byte[] wrongIv = Arrays.copyOf(iv, iv.length);
        wrongIv[0] ^= 0x01;

        assertFalse(MACUtils.isMessageAuthentic(key, wrongIv, message));
        assertEquals(originalMac, message.getMAC());
    }

    private void assertSameMac(MacSetter setter, int expectedLength) {
        TestMessage noMac = new TestMessage(PAYLOAD, null);
        TestMessage existingMac = new TestMessage(PAYLOAD, "DEADBEEF");

        setter.set(noMac);
        setter.set(existingMac);

        assertNotNull(noMac.getMAC());
        assertEquals(expectedLength, noMac.getMAC().length());
        assertEquals(noMac.getMAC(), existingMac.getMAC(),
                "The MAC result must not depend on the current MAC field.");
    }

    private void assertVerificationPreservesMac(TestMessage message, BooleanSupplierWithException verification) {
        String receivedMac = message.getMAC();

        assertTrue(verification.getAsBoolean());
        assertEquals(receivedMac, message.getMAC(),
                "Verification must not overwrite the received MAC.");
    }

    @FunctionalInterface
    private interface MacSetter {
        void set(TestMessage message);
    }

    @FunctionalInterface
    private interface BooleanSupplierWithException {
        boolean getAsBoolean();
    }

    private static class TestMessage extends SEDAPExpressMessage {
        private String payload;

        TestMessage(String payload, String mac) {
            super((byte) 1, 123L, "test-sender", null, null, mac);
            this.payload = payload;
        }

        void setPayload(String payload) {
            this.payload = payload;
        }

        @Override
        public String toString() {
            return serializeHeader().append(payload).toString();
        }
    }
}
