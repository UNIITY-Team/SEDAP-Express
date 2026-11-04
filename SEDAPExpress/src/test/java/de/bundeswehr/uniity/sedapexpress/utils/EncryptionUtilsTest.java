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
 * FITNESS FOR A PARTICULAR PURPOSEnARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.utils.EncryptionUtils.AESKeyLength;

class EncryptionUtilsTest {

	@Test
	void testGeneratesKey() {

		byte[] key128 = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_128_BITS));

		Assertions.assertEquals(16, key128.length);
		double sum = 0;
		for (int i : key128)
			sum += i;
		Assertions.assertNotEquals(0, sum);

		byte[] key256 = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_256_BITS));
		Assertions.assertEquals(32, key256.length);
		sum = 0;
		for (int i : key256)
			sum += i;
		Assertions.assertNotEquals(0, sum);

	}

	@Test
	void testGenerateIV() {
		byte[] iv = EncryptionUtils.generateIV();
		Assertions.assertEquals(16, iv.length);
	}

	@Test
	void testGenerateNonce() {

		byte[] nonce1 = EncryptionUtils.generateNonce();
		byte[] nonce2 = EncryptionUtils.generateNonce();

		Assertions.assertNotNull(nonce1);
		Assertions.assertNotNull(nonce2);
		Assertions.assertEquals(12, nonce1.length, "A GCM nonce must be 96 bits (12 bytes).");
		Assertions.assertEquals(12, nonce2.length, "A GCM nonce must be 96 bits (12 bytes).");
		Assertions.assertFalse(Arrays.equals(nonce1, nonce2), "Consecutive nonces from the same generator must be different.");
		Assertions.assertArrayEquals(Arrays.copyOfRange(nonce1, 0, 4), Arrays.copyOfRange(nonce2, 0, 4), "The session identifier must remain constant for one generator.");
	}

	@Test
	void testGenerateNonceWithSessionId() {
		int sessionId = 0x12345678;

		byte[] nonce1 = EncryptionUtils.generateNonce(sessionId);
		byte[] nonce2 = EncryptionUtils.generateNonce(sessionId);

		Assertions.assertNotNull(nonce1);
		Assertions.assertNotNull(nonce2);
		Assertions.assertEquals(12, nonce1.length);
		Assertions.assertEquals(12, nonce2.length);

		ByteBuffer buffer1 = ByteBuffer.wrap(nonce1);
		ByteBuffer buffer2 = ByteBuffer.wrap(nonce2);

		// First 4 bytes: session ID
		Assertions.assertEquals(sessionId, buffer1.getInt());
		Assertions.assertEquals(sessionId, buffer2.getInt());

		// Last 8 bytes: incrementing counter
		long counter1 = buffer1.getLong();
		long counter2 = buffer2.getLong();

		Assertions.assertEquals(counter1 + 1, counter2);
	}

	@Test
	void testGetIVStringFromByteArray() {

		byte[] refArray = new byte[] { (byte) 0xB4, 0x6A, 0x45, (byte) 0x83, (byte) 0x9E, (byte) 0xF3, 0x79, (byte) 0xC0 };

		String ivString = EncryptionUtils.getIVStringFromByteArray(refArray);

		Assertions.assertEquals("B46A45839EF379C0", ivString);
	}

	@Test
	void testGetIVByteArrayFromString() {

		byte[] refArray = new byte[] { (byte) 0xE2, 0x47, (byte) 0xD1, (byte) 0x8B, 0x29, 0x4B, 0x38, (byte) 0xAF };

		byte[] ivArray = EncryptionUtils.getIVByteArrayFromString("E247D18B294B38AF");

		Assertions.assertArrayEquals(refArray, ivArray);
	}

	@Test
	void testEnDecryptionCFB() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

		byte[] iv = EncryptionUtils.generateIV();

		for (AESKeyLength keyLength : AESKeyLength.values()) {
			byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
			String encryptedMessage = EncryptionUtils.encodeBase64(EncryptionUtils.encrypt_AES_CFB(message.getBytes(StandardCharsets.ISO_8859_1), key, iv));
			String decryptedMessage = new String(EncryptionUtils.decrypt_AES_CFB(EncryptionUtils.decodeBase64(encryptedMessage), key, iv), StandardCharsets.ISO_8859_1);

			Assertions.assertNotEquals(message, encryptedMessage);
			Assertions.assertEquals(message, decryptedMessage);
		}

	}

	@Test
	void testEnDecryptionCTR() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

		byte[] iv = EncryptionUtils.generateIV();

		for (AESKeyLength keyLength : AESKeyLength.values()) {
			byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
			String encryptedMessage = EncryptionUtils.encodeBase64(EncryptionUtils.encrypt_AES_CTR(message.getBytes(StandardCharsets.ISO_8859_1), key, iv));
			String decryptedMessage = new String(EncryptionUtils.decrypt_AES_CTR(EncryptionUtils.decodeBase64(encryptedMessage), key, iv), StandardCharsets.ISO_8859_1);

			Assertions.assertNotEquals(message, encryptedMessage);
			Assertions.assertEquals(message, decryptedMessage);
		}

	}

	@Test
	void testEnDecryptionGCM() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

		for (AESKeyLength keyLength : AESKeyLength.values()) {
			byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
			byte[] nonce = EncryptionUtils.generateNonce();

			String encryptedMessage = EncryptionUtils.encodeBase64(EncryptionUtils.encrypt_AES_GCM(message.getBytes(StandardCharsets.ISO_8859_1), key, nonce));
			String decryptedMessage = new String(EncryptionUtils.decrypt_AES_GCM(EncryptionUtils.decodeBase64(encryptedMessage), key, nonce), StandardCharsets.ISO_8859_1);

			Assertions.assertNotEquals(message, encryptedMessage);
			Assertions.assertEquals(message, decryptedMessage);
		}
	}

	@Test
	void testEnDecryptionGCMWithDifferentNonce()
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		String message = "GCM nonce test";
		byte[] key = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_128_BITS));

		byte[] encryptionNonce = EncryptionUtils.generateNonce();
		byte[] decryptionNonce = EncryptionUtils.generateNonce();
		String encryptedMessage = EncryptionUtils.encodeBase64(EncryptionUtils.encrypt_AES_GCM(message.getBytes(StandardCharsets.ISO_8859_1), key, encryptionNonce));

		Assertions.assertFalse(Arrays.equals(encryptionNonce, decryptionNonce));
		Assertions.assertThrows(BadPaddingException.class, () -> EncryptionUtils.decrypt_AES_GCM(EncryptionUtils.decodeBase64(encryptedMessage), key, decryptionNonce),
				"Decryption with a different GCM nonce must fail authentication.");
	}

	@Test
	void testEnDecryptionGCMWithModifiedCiphertext()
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		String message = "GCM integrity test";
		byte[] key = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_128_BITS));
		byte[] nonce = EncryptionUtils.generateNonce();
		String encryptedMessage = EncryptionUtils.encodeBase64(EncryptionUtils.encrypt_AES_GCM(message.getBytes(StandardCharsets.ISO_8859_1), key, nonce));

		byte[] ciphertextAndTag = Base64.decode(encryptedMessage);
		ciphertextAndTag[0] ^= 0x01;
		String modifiedEncryptedMessage = new String(Base64.encode(ciphertextAndTag), java.nio.charset.StandardCharsets.US_ASCII);

		Assertions.assertThrows(BadPaddingException.class, () -> EncryptionUtils.decrypt_AES_GCM(EncryptionUtils.decodeBase64(modifiedEncryptedMessage), key, nonce),
				"Decryption must fail if the encrypted data or authentication tag was modified.");
	}

}
