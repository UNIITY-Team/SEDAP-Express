/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2026 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.utils;

import java.nio.charset.StandardCharsets;
import java.security.Security;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * Generates and verifies HMAC-SHA-256, AES-CMAC and AES-GMAC values for
 * SEDAP-Express messages. MAC calculation leaves the MAC field empty and does
 * not modify the message while verifying it. Supported wire tag sizes are
 * 32, 128 and 256 bits, subject to the algorithm-specific variants.
 *
 * @author Volker Voß
 */
public class MACUtils {

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
 * Sets the MAC field with HMAC-SHA-256.
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set HMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithHMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcHMAC(key, message));
		return message;

	}

	/**
 * Sets the MAC field with AES-CMAC.
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set CMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithCMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcCMAC(key, message));
		return message;

	}

	/**
 * Sets the MAC field with AES-GMAC.
	 *
	 * @param key        Key for creating the MAC
	 * @param initVector IV for creating the MAC
	 * @param message    SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set GMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcGMAC(key, initVector, message));
		return message;
	}

	/**
	 * Returns the original wire representation, when available, with only the MAC
	 * value replaced by an empty field. Locally created messages use their serializer.
	 * The message object itself is never modified.
	 *
	 * @param message SEDAP-Express message
	 * @return ISO-8859-1 bytes of the message with an empty MAC field
	 */	
	private static byte[] getMessageContentWithoutMAC(final SEDAPExpressMessage message) {
		String serialized = message.toString();
		String receivedWire = message.getReceivedWireText();
		String receivedCanonical = message.getReceivedCanonicalText();
		if (receivedWire != null && receivedCanonical != null
				&& clearMacField(serialized).equals(clearMacField(receivedCanonical))) {
			serialized = receivedWire;
		}
		return clearMacField(serialized).getBytes(StandardCharsets.ISO_8859_1);
	}

	/** Returns the serialized message with the MAC field (field index 6) emptied. */
	private static String clearMacField(final String serialized) {
		int separator = -1;
		for (int i = 0; i < 6; i++) {
			separator = serialized.indexOf(';', separator + 1);
			if (separator < 0) {
				throw new IllegalArgumentException("Message does not contain the MAC field separator.");
			}
		}
		int valueStart = separator + 1;
		int valueEnd = serialized.indexOf(';', valueStart);
		if (valueEnd < 0) {
			valueEnd = serialized.length();
		}
		return serialized.substring(0, valueStart) + serialized.substring(valueEnd);
	}

	private static String calcHMAC(final byte[] key, final SEDAPExpressMessage message) {
		return calcHMAC(key, message, 256);
	}

	private static String calcHMAC(final byte[] key, final SEDAPExpressMessage message, final int macBits) {
		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);
		HMac hmac = new HMac(new SHA256Digest());
		hmac.init(new KeyParameter(key));
		hmac.update(content, 0, content.length);
		final byte[] generatedHMac = new byte[hmac.getMacSize()];
		hmac.doFinal(generatedHMac, 0);
		return SEDAPExpressMessage.HexFormater.formatHex(generatedHMac).substring(0, macBits / 4);
	}

	/**
	 * Calculates a 32-bit AES-CMAC value for compatibility with the original API.
	 *
	 * @param key shared AES key
	 * @param message message whose MAC field is excluded from the calculation
	 * @return uppercase hexadecimal representation of the 32-bit CMAC
	 */
	private static String calcCMAC(final byte[] key, final SEDAPExpressMessage message) {
		return calcCMAC(key, message, 32);
	}

	private static String calcCMAC(final byte[] key, final SEDAPExpressMessage message, final int macBits) {
		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);
		final CipherParameters cipherParameters = new KeyParameter(key);
		final CMac cmac = new CMac(new AESLightEngine(), macBits);
		cmac.init(cipherParameters);
		cmac.update(content, 0, content.length);
		final byte[] generatedCMac = new byte[cmac.getMacSize()];
		cmac.doFinal(generatedCMac, 0);
		return SEDAPExpressMessage.HexFormater.formatHex(generatedCMac);
	}

	/**
	 * Calculates a 32-bit AES-GMAC value for compatibility with the original API.
	 *
	 * @param key shared AES key
	 * @param initVector unique GMAC initialization vector
	 * @param message message whose MAC field is excluded from the calculation
	 * @return uppercase hexadecimal representation of the 32-bit GMAC
	 */
	private static String calcGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {
		return calcGMAC(key, initVector, message, 32);
	}

	private static String calcGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message, final int macBits) {
		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);
		final CipherParameters cipherParameters = new KeyParameter(key);
		final ParametersWithIV cipherParametersWithInitVector = new ParametersWithIV(cipherParameters, initVector);
		final GMac gmac = new GMac(GCMBlockCipher.newInstance(new AESLightEngine()), macBits);
		gmac.init(cipherParametersWithInitVector);
		gmac.update(content, 0, content.length);
		final byte[] generatedGMac = new byte[gmac.getMacSize()];
		gmac.doFinal(generatedGMac, 0);
		return SEDAPExpressMessage.HexFormater.formatHex(generatedGMac);
	}

	/**
	 * Sets a 128-bit HMAC-SHA-256 tag on the message.
	 *
	 * @param key shared HMAC key
	 * @param message message whose MAC field is updated
	 * @return the same message with a 128-bit HMAC tag
	 */
	public static SEDAPExpressMessage setMACFieldWith128BitHMAC(final byte[] key, final SEDAPExpressMessage message) {
		message.setMAC(MACUtils.calcHMAC(key, message, 128));
		return message;
	}

	/**
	 * Sets a 128-bit AES-CMAC tag on the message.
	 *
	 * @param key shared AES key
	 * @param message message whose MAC field is updated
	 * @return the same message with a 128-bit CMAC tag
	 */
	public static SEDAPExpressMessage setMACFieldWith128BitCMAC(final byte[] key, final SEDAPExpressMessage message) {
		message.setMAC(MACUtils.calcCMAC(key, message, 128));
		return message;
	}

	/**
	 * Sets a 128-bit AES-GMAC tag on the message.
	 *
	 * @param key shared AES key
	 * @param initVector unique GMAC initialization vector
	 * @param message message whose MAC field is updated
	 * @return the same message with a 128-bit GMAC tag
	 */
	public static SEDAPExpressMessage setMACFieldWith128BitGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {
		message.setMAC(MACUtils.calcGMAC(key, initVector, message, 128));
		return message;
	}

	/**
 * Sets the MAC field with a 32-bit truncated HMAC-SHA-256.
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit HMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitHMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcHMAC(key, message).substring(0, 8));
		return message;

	}

	/**
 * Sets the MAC field with a 32-bit AES-CMAC.
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit CMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitCMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcCMAC(key, message).substring(0, 8));
		return message;

	}

	/**
 * Sets the MAC field with a 32-bit AES-GMAC.
	 *
	 * @param key        Key for creating the MAC
	 * @param initVector IV for creating the MAC
	 * @param message    SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit GMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcGMAC(key, initVector, message).substring(0, 8));
		return message;
	}

	/**
	 * Checks if a message is authentic.
	 *
	 * @param key     Shared secret key
	 * @param message Original message
	 *
	 * @return Status if the message is authentic. True means yes, false means not.
	 */
	public static boolean isMessageAuthentic(final byte[] key, final SEDAPExpressMessage message) {

		return isMessageAuthentic(key, null, message);

	}

	/**
	 * Verifies a 128-bit HMAC-SHA-256 tag, as required for KEYEXCHANGE phase-2 key confirmation.
	 * Unlike {@link #isMessageAuthentic(byte[], SEDAPExpressMessage)}, this method does not accept CMAC/GMAC tags.
	 *
	 * @param key shared HMAC key
	 * @param message parsed message containing the 32-hex-character tag
	 * @return {@code true} only when the exact 128-bit HMAC-SHA-256 tag matches
	 */
	public static boolean isMessageAuthenticWith128BitHMAC(final byte[] key, final SEDAPExpressMessage message) {
		if (key == null || message == null || message.getMAC() == null || message.getMAC().length() != 32) {
			return false;
		}
		return MACUtils.calcHMAC(key, message, 128).equalsIgnoreCase(message.getMAC());
	}

	/**
	 * Checks if a message is authentic.
	 * 
	 * @param key        En-/Decryption key
	 * @param initVector Initialisation vector (sometimes called IV), could be null
	 * @param message    Message to prove
	 *
	 * @return Status if the message is authentic. True means yes, false means not.
	 */
	public static boolean isMessageAuthentic(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		if (key == null || message == null || message.getMAC() == null) {
			return false;
		}
		final String receivedMAC = message.getMAC();
		return switch (receivedMAC.length()) {
		case 8 -> initVector == null
				? MACUtils.calcHMAC(key, message, 32).equalsIgnoreCase(receivedMAC) || MACUtils.calcCMAC(key, message, 32).equalsIgnoreCase(receivedMAC)
				: MACUtils.calcGMAC(key, initVector, message, 32).equalsIgnoreCase(receivedMAC);
		case 32 -> initVector == null
				? MACUtils.calcHMAC(key, message, 128).equalsIgnoreCase(receivedMAC) || MACUtils.calcCMAC(key, message, 128).equalsIgnoreCase(receivedMAC)
				: MACUtils.calcGMAC(key, initVector, message, 128).equalsIgnoreCase(receivedMAC);
		case 64 -> initVector == null && MACUtils.calcHMAC(key, message, 256).equalsIgnoreCase(receivedMAC);
		default -> false;
		};
	}

	/**
	 * Runs the application entry point.
	 * @param args value for args
	 */
	public static void main(String[] args) {
		OWNUNIT msg1 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;;53.32;8.11;0;5.5;21;22;;;FGS Bayern");
		OWNUNIT msg2 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;DDA6B526;53.32;8.11;0;5.5;21;22;;;FGS Bayern");
		OWNUNIT msg3 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;DAA6B526;53.32;8.11;0;5.5;21;22;;;FGS Bayern");

		setMACFieldWith32BitCMAC("expressexpressex".getBytes(), msg1);
		System.out.println(msg1);

		System.out.println(isMessageAuthentic("expressexpressex".getBytes(), msg2));
		System.out.println(isMessageAuthentic("expressexpressex".getBytes(), msg3));
	}

}
