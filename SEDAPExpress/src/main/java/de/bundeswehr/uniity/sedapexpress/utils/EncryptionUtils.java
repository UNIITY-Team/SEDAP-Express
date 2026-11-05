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
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.atomic.AtomicLong;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.util.encoders.Base64;

/**
 * Some encryption utils
 * 
 * @author Volker Voß
 *
 */
public class EncryptionUtils {

	public static enum DHKEMKeyLength {

		KEY_LENGTH_1024_BITS(1024), KEY_LENGTH_2048_BITS(2048);

		private final int keyLength;

		public int getIntValue() {
			return this.keyLength;
		}

		private DHKEMKeyLength(int keyLength) {
			this.keyLength = keyLength;
		}
	}

	public static enum AESKeyLength {

		KEY_LENGTH_128_BITS(128), KEY_LENGTH_256_BITS(256);

		private final int keyLength;

		public int getIntValue() {
			return this.keyLength;
		}

		private AESKeyLength(int keyLength) {
			this.keyLength = keyLength;
		}
	}

	private static final SecureRandom random = new SP800SecureRandomBuilder().setSecurityStrength(128).setEntropyBitsRequired(128).buildHMAC(new HMac(new SHA256Digest()), null, true);

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		sessionId = random.nextInt();
	}

	private static final int sessionId;
	private static final AtomicLong ivCounter = new AtomicLong(0);
	private static final AtomicLong nonceCounter = new AtomicLong(0);

	/**
	 * Creates a cryptographically secure random session ID.
	 *
	 * @return Random session ID
	 */
	public static int createRandomSessionID() {
		return random.nextInt();
	}

	/**
	 * Generates an appropriate Initialization Vector (IV) for AES-CTR
	 * 
	 * The IV consists of:
	 * - 4 bytes: Session ID
	 * - 8 bytes: Incrementing counter
	 * - 4 bytes: Reserved (zero)
	 * 
	 * @return Initialization Vector (IV)
	 */
	public static byte[] generateCounterBasedIV() {

		return generateCounterBasedIV(sessionId);
	}

	/**
	 * Generates an appropriate Initialization Vector (IV) for AES-CTR
	 * 
	 * The IV consists of:
	 * - 4 bytes: Session ID
	 * - 8 bytes: Incrementing counter
	 * - 4 bytes: Reserved (zero)
	 * 
	 * @return Initialization Vector (IV)
	 */
	public static byte[] generateCounterBasedIV(int sessionId) {

		long currentCounter = ivCounter.getAndIncrement();

		if (currentCounter < 0) {
			throw new IllegalStateException("IV counter exhausted");
		}

		return ByteBuffer.allocate(16).putInt(sessionId).putLong(currentCounter).putInt(0).array();
	}

	/**
	 * Generates an unpredictable 16-byte IV for AES-CFB.
	 *
	 * @return 16-byte random IV
	 */
	public static byte[] generateRandomIV() {
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		return iv;
	}

	/**
	 * Generates a unique 96-bit nonce for use with AES-GCM
	 * Uses a default random sessionid
	 * 
	 * @return 12-byte AES-GCM nonce
	 */
	public static byte[] generateNonce() {

		return generateNonce(sessionId);
	}

	/**
	 * Generates a unique 96-bit nonce for use with AES-GCM
	 * 
	 * @return 12-byte AES-GCM nonce
	 */
	public static byte[] generateNonce(int sessionId) {

		long currentCounter = nonceCounter.getAndIncrement();

		if (currentCounter < 0) {
			throw new IllegalStateException("Nonce counter exhausted");
		}

		return ByteBuffer.allocate(12).putInt(sessionId).putLong(currentCounter).array();
	}

	/**
	 * This method generates a (pseudo)random key for using the authentication or
	 * encryption feature. Instead of generating a key and transmit it on a
	 * different channel to the counterpart, you can also use the key generation and
	 * exchange functions of this framework. You will find the required information
	 * in the ICD or by exploring the other Java files in this package.
	 * 
	 * @return BASE64 encoded key of the desired bit length. The bit length is 128
	 *         bits/16 bytes or 256 bits/32 bytes.
	 */
	public static String generatesKey(AESKeyLength bitLength) {

		CipherKeyGenerator generator = new CipherKeyGenerator();
		generator.init(new KeyGenerationParameters(EncryptionUtils.random, bitLength.getIntValue()));

		return new String(Base64.encode(generator.generateKey()), StandardCharsets.US_ASCII);

	}

	/**
	 * Checks if the given key is usable for authentication or encryption (ECB).
	 * 
	 * @param key Secret key
	 * 
	 * @return true means the key is usable, false means not. In the last case for
	 *         instance the key lenght doesn't match of something else.
	 */
	public static boolean checkKey(byte[] key) {

		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks if the given key is usable for authentication or encryption (CTR/CFB).
	 * 
	 * @param key Secret key
	 * @param iv  Initialization Vector
	 * 
	 * @return true means the key is usable, false means not. In the last case for
	 *         instance the key lenght doesn't match of something else.
	 */
	public static boolean checkKey(byte[] key, byte[] iv) {

		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

			Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

			cipher = Cipher.getInstance("AES/CTR/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Concatenates the initialization vector/nonce and the encrypted data.
	 *
	 * @param iv             initialization vector or nonce
	 * @param encryptedBytes encrypted data
	 * @return IV/nonce followed by encrypted data
	 */
	private static byte[] prependIV(byte[] iv, byte[] encryptedBytes) {
		byte[] result = new byte[iv.length + encryptedBytes.length];
		System.arraycopy(iv, 0, result, 0, iv.length);
		System.arraycopy(encryptedBytes, 0, result, iv.length, encryptedBytes.length);
		return result;
	}

	/**
	 * Extracts the initialization vector/nonce from the beginning of the input.
	 *
	 * @param data     combined IV/nonce and encrypted data
	 * @param ivLength expected IV/nonce length
	 * @return extracted IV/nonce
	 * @throws IllegalArgumentException if the input is too short
	 */
	private static byte[] extractIV(byte[] data, int ivLength) {
		if (data == null || data.length < ivLength) {
			throw new IllegalArgumentException("Encrypted data is too short to contain the IV/nonce.");
		}

		byte[] iv = new byte[ivLength];
		System.arraycopy(data, 0, iv, 0, ivLength);
		return iv;
	}

	/**
	 * Removes the IV/nonce prefix from the combined input.
	 *
	 * @param data     combined IV/nonce and encrypted data
	 * @param ivLength length of the IV/nonce prefix
	 * @return encrypted data without the IV/nonce prefix
	 */
	private static byte[] removeIV(byte[] data, int ivLength) {
		if (data == null || data.length < ivLength) {
			throw new IllegalArgumentException("Encrypted data is too short to contain the IV/nonce.");
		}

		byte[] encryptedBytes = new byte[data.length - ivLength];
		System.arraycopy(data, ivLength, encryptedBytes, 0, encryptedBytes.length);
		return encryptedBytes;
	}

	/**
	 * Encrypts data with AES-CFB and returns the 16-byte random IV followed by
	 * the ciphertext.
	 *
	 * @param originalData plaintext
	 * @param key          128- or 256-bit AES key
	 * @return IV (16 bytes) || ciphertext
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] encrypt_AES_CFB(byte[] originalData, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

		byte[] iv = generateRandomIV();
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
		return prependIV(iv, cipher.doFinal(originalData));
	}

	/**
	 * Decrypts AES-CFB data. The first 16 bytes are interpreted as the IV.
	 *
	 * @param data combined IV (16 bytes) || ciphertext
	 * @param key  128- or 256-bit AES key
	 * @return plaintext
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decrypt_AES_CFB(byte[] data, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] iv = extractIV(data, 16);
		byte[] encryptedData = removeIV(data, 16);
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		return cipher.doFinal(encryptedData);
	}

	/**
	 * Encrypts data with AES-CTR and returns the 16-byte counter-based IV
	 * followed by the ciphertext.
	 *
	 * @param originalData plaintext
	 * @param key          128- or 256-bit AES key
	 * @return IV (16 bytes) || ciphertext
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] encrypt_AES_CTR(byte[] originalData, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

		byte[] iv = generateCounterBasedIV();
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
		return prependIV(iv, cipher.doFinal(originalData));
	}

	/**
	 * Decrypts AES-CTR data. The first 16 bytes are interpreted as the IV.
	 *
	 * @param data combined IV (16 bytes) || ciphertext
	 * @param key  128- or 256-bit AES key
	 * @return plaintext
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decrypt_AES_CTR(byte[] data, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] iv = extractIV(data, 16);
		byte[] encryptedData = removeIV(data, 16);
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		return cipher.doFinal(encryptedData);
	}

	/**
	 * Encrypts data with AES-GCM and returns the 12-byte nonce followed by
	 * the ciphertext and authentication tag.
	 *
	 * @param originalData plaintext
	 * @param key          128- or 256-bit AES key
	 * @return nonce (12 bytes) || ciphertext || authentication tag
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] encrypt_AES_GCM(byte[] originalData, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

		byte[] nonce = generateNonce();
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
		return prependIV(nonce, cipher.doFinal(originalData));
	}

	/**
	 * Decrypts AES-GCM data. The first 12 bytes are interpreted as the nonce.
	 * The ciphertext includes the authentication tag appended by the provider.
	 *
	 * @param data nonce (12 bytes) || ciphertext || authentication tag
	 * @param key  128- or 256-bit AES key
	 * @return plaintext, only if authentication succeeds
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decrypt_AES_GCM(byte[] data, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] nonce = extractIV(data, 12);
		byte[] encryptedData = removeIV(data, 12);
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
		return cipher.doFinal(encryptedData);
	}

	public static String encodeBase64(byte[] data) {
		return new String(Base64.encode(data), StandardCharsets.ISO_8859_1);
	}

	public static byte[] decodeBase64(String base64Data) {
		return Base64.decode(base64Data);
	}

}
