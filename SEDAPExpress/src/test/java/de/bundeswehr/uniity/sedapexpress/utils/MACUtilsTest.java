package de.bundeswehr.uniity.sedapexpress.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.TEXT;

/**
 * Tests MAC generation and verification.
 *
 * MAC calculation must use the complete serialized message with the MAC field
 * left empty, preserving the field position and CSV delimiters. Verification
 * must preserve the received MAC while comparing it with the newly calculated
 * MAC.
 *
 * The 32-bit variants must be the first four bytes of the corresponding full
 * cryptographic MAC tag. They must not use a secondary checksum such as Adler32.
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
		assertSameMac(m -> MACUtils.setMACFieldWithHMAC(key, m), 64);
	}

	@Test
	void cmacGenerationIgnoresExistingMac() {
		assertSameMac(m -> MACUtils.setMACFieldWithCMAC(key, m), 8);
	}

	@Test
	void gmacGenerationIgnoresExistingMac() {
		assertSameMac(m -> MACUtils.setMACFieldWithGMAC(key, iv, m), 8);
	}

	@Test
	void truncatedHmacGenerationIgnoresExistingMac() {
		assertSameMac(m -> MACUtils.setMACFieldWith32BitHMAC(key, m), 8);
	}

	@Test
	void truncatedCmacGenerationIgnoresExistingMac() {
		assertSameMac(m -> MACUtils.setMACFieldWith32BitCMAC(key, m), 8);
	}

	@Test
	void truncatedGmacGenerationIgnoresExistingMac() {
		assertSameMac(m -> MACUtils.setMACFieldWith32BitGMAC(key, iv, m), 8);
	}

	@Test
	void truncatedHmacIsFirstFourBytesOfFullHmac() {
		assertTruncatedMacMatchesFullMac(
				m -> MACUtils.setMACFieldWithHMAC(key, m),
				m -> MACUtils.setMACFieldWith32BitHMAC(key, m));
	}

	@Test
	void truncatedCmacIsFirstFourBytesOfFullCmac() {
		assertTruncatedMacMatchesFullMac(
				m -> MACUtils.setMACFieldWithCMAC(key, m),
				m -> MACUtils.setMACFieldWith32BitCMAC(key, m));
	}

	@Test
	void truncatedGmacIsFirstFourBytesOfFullGmac() {
		assertTruncatedMacMatchesFullMac(
				m -> MACUtils.setMACFieldWithGMAC(key, iv, m),
				m -> MACUtils.setMACFieldWith32BitGMAC(key, iv, m));
	}

	@Test
	void hmac128GenerationAndVerificationWorksForKeyConfirmation() {
		TestMessage message = new TestMessage(PAYLOAD, null);
		MACUtils.setMACFieldWith128BitHMAC(key, message);
		assertEquals(32, message.getMAC().length());
		assertTrue(MACUtils.isMessageAuthenticWith128BitHMAC(key, message));
	}

	@Test
	void cmac128GenerationAndVerificationWorks() {
		TestMessage message = new TestMessage(PAYLOAD, null);
		MACUtils.setMACFieldWith128BitCMAC(key, message);
		assertEquals(32, message.getMAC().length());
		assertTrue(MACUtils.isMessageAuthentic(key, message));
	}

	@Test
	void gmac128GenerationAndVerificationWorks() {
		TestMessage message = new TestMessage(PAYLOAD, null);
		MACUtils.setMACFieldWith128BitGMAC(key, iv, message);
		assertEquals(32, message.getMAC().length());
		assertTrue(MACUtils.isMessageAuthentic(key, iv, message));
	}

	@Test
	void verifiesMacAgainstExactReceivedWireText() throws Exception {
		String unsignedWire = "TEXT;01;0195238E25AD;NODE;U;FALSE;;PEER;01;NONE;Hello";
		Mac hmac = Mac.getInstance("HmacSHA256");
		hmac.init(new SecretKeySpec(key, "HmacSHA256"));
		String macValue = HexFormat.of().withUpperCase().formatHex(hmac.doFinal(unsignedWire.getBytes(StandardCharsets.ISO_8859_1)));
		String signedWire = unsignedWire.replace(";FALSE;;PEER;", ";FALSE;" + macValue + ";PEER;");
		SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(signedWire);
		assertNotNull(parsed);
		assertTrue(MACUtils.isMessageAuthentic(key, parsed));
		assertFalse(parsed.toString().equals(signedWire), "The test must exercise a non-canonical received wire representation.");
	}

	@Test
	void macVerificationAcceptsLowerCaseHex() {
		TestMessage message = new TestMessage(PAYLOAD, null);
		MACUtils.setMACFieldWith128BitHMAC(key, message);
		message.setMAC(message.getMAC().toLowerCase(java.util.Locale.ROOT));
		assertTrue(MACUtils.isMessageAuthenticWith128BitHMAC(key, message));
	}

	@Test
	void hmac256TagIsAcceptedAfterMessageDeserialization() throws Exception {
		String unsignedWire = "TEXT;01;0195238E25AD;NODE;U;FALSE;;PEER;01;NONE;Hello";
		Mac hmac = Mac.getInstance("HmacSHA256");
		hmac.init(new SecretKeySpec(key, "HmacSHA256"));
		String macValue = HexFormat.of().withUpperCase().formatHex(hmac.doFinal(unsignedWire.getBytes(StandardCharsets.ISO_8859_1)));
		String signedWire = unsignedWire.replace(";FALSE;;PEER;", ";FALSE;" + macValue + ";PEER;");

		SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(signedWire);
		assertNotNull(parsed);
		assertEquals(64, parsed.getMAC().length());
		assertTrue(MACUtils.isMessageAuthentic(key, parsed));
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
		assertEquals(noMac.getMAC(), existingMac.getMAC(), "The MAC result must not depend on the current MAC field.");
	}

	private void assertTruncatedMacMatchesFullMac(MacSetter fullMacSetter, MacSetter truncatedMacSetter) {
		TestMessage fullMacMessage = new TestMessage(PAYLOAD, null);
		TestMessage truncatedMacMessage = new TestMessage(PAYLOAD, null);

		fullMacSetter.set(fullMacMessage);
		truncatedMacSetter.set(truncatedMacMessage);

		String fullMac = fullMacMessage.getMAC();
		String truncatedMac = truncatedMacMessage.getMAC();

		assertNotNull(fullMac);
		assertNotNull(truncatedMac);
		assertEquals(8, truncatedMac.length(), "A 32-bit MAC must contain exactly four bytes.");
		assertTrue(fullMac.length() >= 8, "The full MAC must contain at least four bytes.");
		assertEquals(fullMac.substring(0, 8), truncatedMac,
				"The 32-bit MAC must be the first four bytes of the full cryptographic MAC.");
	}

	private void assertVerificationPreservesMac(TestMessage message, BooleanSupplierWithException verification) {
		String receivedMac = message.getMAC();

		assertTrue(verification.getAsBoolean());
		assertEquals(receivedMac, message.getMAC(), "Verification must not overwrite the received MAC.");
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
		private static final long serialVersionUID = -447639167554559062L;
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
