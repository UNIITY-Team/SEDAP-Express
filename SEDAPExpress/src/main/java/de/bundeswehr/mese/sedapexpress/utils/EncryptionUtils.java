/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024 MESE POC: Volker Voß, Federal Armed Forces of Germany
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
 * CONTRIBUTORS BEn LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.mese.sedapexpress.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HexFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.util.encoders.Base64;

public class EncryptionUtils {

    public static enum DHKEMKeyLength {

	KEY_LENGTH_1024_BITS(1024), KEY_LENGTH_2048_BTS(2048);

	private int keyLength;

	public int getIntValue() {
	    return this.keyLength;
	}

	private DHKEMKeyLength(int keyLength) {
	    this.keyLength = keyLength;
	}
    }

    public static enum AESKeyLength {

	KEY_LENGTH_128_BITS(128), KEY_LENGTH_256_BTS(256);

	private int keyLength;

	public int getIntValue() {
	    return this.keyLength;
	}

	private AESKeyLength(int keyLength) {
	    this.keyLength = keyLength;
	}
    }

    private static SecureRandom random = new SP800SecureRandomBuilder().buildHMAC(new HMac(new SHA256Digest()), null, true);
    private static HexFormat hexformater = HexFormat.of().withUpperCase();

    static {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * This method generates a (pseudo)random key for using the authentication or encryption feature. Instead of generating a key and transmit it on a different channel to the counterpart, you can also use the key generation and exchange
     * functions of this framework. You will find the required information in the ICD or by exploring the other Java files in this package.
     * 
     * @return BASE64 encoded key of the desired bit length. The bit length is 128 bits/16 bytes or 256 bits/32 bytes.
     */
    public static String generatesKey(AESKeyLength bitLength) {

	CipherKeyGenerator generator = new CipherKeyGenerator();
	generator.init(new KeyGenerationParameters(EncryptionUtils.random, bitLength.getIntValue()));

	return new String(Base64.encode(generator.generateKey()));

    }

    /**
     * Checks if the given key is usable for authentication or encryption (ECB).
     * 
     * @param key Secret key
     * 
     * @return true means the key is usable, false means not. In the last case for instance the key lenght doesn't match of something else.
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
     * @return true means the key is usable, false means not. In the last case for instance the key lenght doesn't match of something else.
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
     * Generates an appropriate Initialization Vector (IV)
     * 
     * @return Initialization Vector (IV)
     */
    public static byte[] generateIV() {

	try {
	    byte[] iv = new byte[Cipher.getInstance("AES").getBlockSize()];
	    EncryptionUtils.random.nextBytes(iv);
	    return iv;
	} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public static String getIVStringFromByteArray(byte[] ivArray) {

	return EncryptionUtils.hexformater.withUpperCase().formatHex(ivArray);
    }

    /**
     * Converts a hexdecimal presentation of a IV to a byte array.
     * 
     * @param ivString hexdecimal presentation of a IV
     * @return IV as byte array
     */
    public static byte[] getIVByteArrayFromString(String ivString) {

	return EncryptionUtils.hexformater.parseHex(ivString);

    }

    /**
     * Encrypts a message with AES128/256 ECB/PKCS7Padding (NIST SP 800-38A)
     *
     * @param originalData plain data which should be encrypted
     * @param key          the 128 or 256 bit wide encryption key
     *
     * @return encrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static String encrypt_AES_ECB(String originalData, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
	cipher.init(Cipher.ENCRYPT_MODE, secretKey, EncryptionUtils.random);
	byte[] encryptedBytes = cipher.doFinal(originalData.getBytes());

	return new String(Base64.encode(encryptedBytes));
    }

    /**
     * Decrypts a message with AES128/256 ECB/PKCS7Padding (NIST SP 800-38A)
     *
     * @param encryptedData encrypted data which should be decrypted
     * @param key           the 128 or 256 bit wide decryption key
     *
     * @return decrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static String decrypt_AES_ECB(String encryptedData, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
	cipher.init(Cipher.DECRYPT_MODE, secretKey, EncryptionUtils.random);
	byte[] decodedBytes = Base64.decode(encryptedData);
	byte[] decryptedBytes = cipher.doFinal(decodedBytes);

	return new String(decryptedBytes);
    }

    /**
     * Encrypts a message with AES128/256 CFB/NoPadding (NIST SP 800-38A)
     *
     * @param originalData plain data which should be encrypted
     * @param key          the 128 or 256 bit wide encryption key
     * @param iv           appropriate Initialization Vector
     *
     * @return encrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static String encrypt_AES_CFB(String originalData, byte[] key, byte[] iv)
	    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
	cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv), EncryptionUtils.random);
	byte[] encryptedBytes = cipher.doFinal(originalData.getBytes());

	return new String(Base64.encode(encryptedBytes));
    }

    /**
     * Decrypts a message with AES128/256 CFB/NoPadding (NIST SP 800-38A)
     *
     * @param encryptedData encrypted data which should be decrypted
     * @param key           the 128 or 256 bit wide decryption key
     * @param iv            appropriate Initialization Vector (IV)
     *
     * @return decrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static String decrypt_AES_CFB(String encryptedData, byte[] key, byte[] iv)
	    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
	cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv), EncryptionUtils.random);
	byte[] decodedBytes = Base64.decode(encryptedData);
	byte[] decryptedBytes = cipher.doFinal(decodedBytes);

	return new String(decryptedBytes);
    }

    /**
     * Encrypts a message with AES128/256 CTR/NoPadding (NIST SP 800-38A)
     *
     * @param originalData plain data which should be encrypted
     * @param key          the 128 or 256 bit wide encryption key
     * @param iv           appropriate Initialization Vector
     *
     * @return encrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static String encrypt_AES_CTR(String originalData, byte[] key, byte[] iv)
	    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
	cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv), EncryptionUtils.random);
	byte[] encryptedBytes = cipher.doFinal(originalData.getBytes());

	return new String(Base64.encode(encryptedBytes));
    }

    /**
     * Decrypts a message with AES128/256 CTR/NoPadding (NIST SP 800-38A)
     *
     * @param encryptedData encrypted data which should be decrypted
     * @param key           the 128 or 256 bit wide decryption key
     * @param iv            appropriate Initialization Vector
     *
     * @return decrypted data
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public static String decrypt_AES_CTR(String encryptedData, byte[] key, byte[] iv)
	    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

	SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
	cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv), EncryptionUtils.random);
	byte[] decodedBytes = Base64.decode(encryptedData);
	byte[] decryptedBytes = cipher.doFinal(decodedBytes);

	return new String(decryptedBytes);
    }

}
