/*
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2026 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
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
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.util.encoders.Base64;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;

/**
 * Provides the common cryptographic primitives used by the SEDAP-Express utility classes.
 *
 * <p>This class owns the single shared {@link SecureRandom} instance used for key-pair generation,
 * KEM encapsulation, salt generation, key generation, and IV/nonce generation. Other utility
 * classes must use {@link #random} rather than creating additional random generators.
 *
 * <p>The class implements HKDF-SHA-256 for high-entropy DH/ECDH/KEM shared secrets and Argon2id for
 * password-based key derivation. It also provides AES-CFB, AES-CTR, and AES-GCM encryption helpers.
 * CFB and CTR do not authenticate ciphertext; AES-GCM should be preferred when integrity protection
 * is required.
 *
 * @author Volker Voß
 * @since 1.5.5
 * @see DHUtils
 * @see ECDHUtils
 * @see MLKEMUtils
 * @see FrodoKEMUtils
 */
public final class EncryptionUtils {

    /**
     * The only {@link SecureRandom} instance used by the SEDAP-Express cryptographic utility classes.
     * Do not create additional {@code SecureRandom} or {@code SP800SecureRandomBuilder} instances
     * elsewhere in these utilities.
     */
    public static final SecureRandom random = new SP800SecureRandomBuilder()
            .setSecurityStrength(128)
            .setEntropyBitsRequired(128)
            .buildHMAC(new HMac(new SHA256Digest()), null, true);

    private static final int RANDOM_BUFFER_SIZE = 4096;
    private static final ThreadLocal<ByteBuffer> THREAD_RANDOM_BUFFER = ThreadLocal.withInitial(() -> ByteBuffer.allocate(0));

    /**
     * Copies bytes from a per-thread buffer replenished by the single central SP800SecureRandom.
     * This amortizes synchronization on the central generator without introducing another entropy source.
     *
     * @param destination destination for the random bytes
     */
    private static void nextRandomBytes(byte[] destination) {
        ByteBuffer buffer = THREAD_RANDOM_BUFFER.get();
        if (buffer.remaining() < destination.length) {
            byte[] refill = new byte[Math.max(RANDOM_BUFFER_SIZE, destination.length)];
            random.nextBytes(refill);
            buffer = ByteBuffer.wrap(refill);
            THREAD_RANDOM_BUFFER.set(buffer);
        }
        buffer.get(destination);
    }

    /** Number of bytes in a SEDAP-Express protocol salt. */
    public static final int SALT_LENGTH_BYTES = 32;

    /** Number of bytes in an AES-GCM nonce. */
    private static final int GCM_NONCE_LENGTH_BYTES = 12;

    /** AES-GCM authentication-tag length in bits. */
    private static final int GCM_TAG_LENGTH_BITS = 128;

    /** AES block and CFB/CTR IV length in bytes. */
    private static final int AES_BLOCK_LENGTH_BYTES = 16;

    /** Argon2id memory cost in kibibytes (64 MiB). */
    private static final int ARGON2_MEMORY_KIB = 65_536;

    /** Argon2id iteration count. */
    private static final int ARGON2_ITERATIONS = 3;

    /** Argon2id parallelism (number of lanes). */
    private static final int ARGON2_PARALLELISM = 1;

    /** Process-local random session identifier used by the no-argument counter-based IV generator. */
    private static final int sessionId;

    /** Monotonically increasing process-local counter for deterministic AES-CTR IV generation. */
    private static final AtomicLong ivCounter = new AtomicLong();

    /** Monotonically increasing process-local counter for deterministic AES-GCM nonce generation. */
    private static final AtomicLong nonceCounter = new AtomicLong();

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        if (Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastlePQCProvider());
        }
        sessionId = random.nextInt();
    }

    /**
     * Prevents instantiation of this utility class.
     *
     * @throws AssertionError always, because this class only provides static utility methods
     */
    private EncryptionUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Generates a fresh protocol salt using the shared random generator.
     *
     * @return a new, independently generated 32-byte salt
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Generates a random 32-bit session identifier using the shared random generator.
     *
     * @return newly generated session identifier
     */
    public static int createRandomSessionID() {
        return random.nextInt();
    }

    /**
     * Creates a process-local, counter-based 128-bit IV for AES-CTR. The layout is a 32-bit
     * process-session identifier, a 64-bit monotonic counter, and four reserved zero bytes.
     *
     * <p>This construction is unique only within the current process and counter lifetime. Do not
     * reuse the same AES key with this IV scheme across processes or independent directions unless
     * uniqueness is guaranteed externally. Prefer separately derived keys per direction/session.
     *
     * @return 16-byte counter-based AES-CTR IV
     * @throws IllegalStateException if the local IV counter is exhausted
     */
    public static byte[] generateCounterBasedIV() {
        return generateCounterBasedIV(sessionId);
    }

    /**
     * Creates a process-local, counter-based 128-bit IV for AES-CTR using a caller-supplied session
     * identifier. The layout is the 32-bit session identifier, a 64-bit monotonic counter, and four
     * reserved zero bytes.
     *
     * <p>The counter is shared with the no-argument overload. The same key/session identifier must
     * not be used by another process or transmission direction unless IV uniqueness is guaranteed.
     *
     * @param sessionIdentifier 32-bit identifier associated with the cryptographic session
     * @return 16-byte counter-based AES-CTR IV
     * @throws IllegalStateException if the local IV counter is exhausted
     */
    public static byte[] generateCounterBasedIV(int sessionIdentifier) {
        long counter = nextCounter(ivCounter, "AES-CTR IV");
        return ByteBuffer.allocate(AES_BLOCK_LENGTH_BYTES)
                .putInt(sessionIdentifier).putLong(counter).putInt(0).array();
    }

    /**
     * Generates a random 128-bit IV for AES-CFB or AES-CTR using the shared random generator.
     *
     * @return a new 16-byte IV
     */
    public static byte[] generateRandomIV() {
        byte[] iv = new byte[AES_BLOCK_LENGTH_BYTES];
        nextRandomBytes(iv);
        return iv;
    }

    /**
     * Generates a random 96-bit nonce for AES-GCM using the shared random generator.
     *
     * <p>Random nonces provide probabilistic uniqueness. Use independently derived traffic keys for
     * each direction and avoid excessive message counts with one key; nonce reuse under a GCM key
     * compromises security.
     *
     * @return a new random 12-byte AES-GCM nonce
     */
    public static byte[] generateNonce() {
        byte[] nonce = new byte[GCM_NONCE_LENGTH_BYTES];
        nextRandomBytes(nonce);
        return nonce;
    }

    /**
     * Generates a deterministic 96-bit AES-GCM nonce consisting of a 32-bit session identifier and
     * a 64-bit process-local monotonic counter.
     *
     * <p>Do not use the same AES key and session identifier in multiple processes or directions.
     * The caller must ensure that a session identifier is not reused with the same key after a
     * process restart.
     *
     * @param sessionIdentifier 32-bit identifier of the cryptographic session
     * @return 12-byte nonce containing the session identifier and counter
     * @throws IllegalStateException if the local nonce counter is exhausted
     */
    public static byte[] generateNonce(int sessionIdentifier) {
        long counter = nextCounter(nonceCounter, "AES-GCM nonce");
        return ByteBuffer.allocate(GCM_NONCE_LENGTH_BYTES).putInt(sessionIdentifier).putLong(counter).array();
    }

    /**
     * Returns and increments a process-local counter, rejecting values after signed 64-bit overflow.
     *
     * @param counter counter to increment atomically
     * @param label operation name included in the exception message
     * @return current nonnegative counter value before increment
     * @throws IllegalStateException if the counter has overflowed into the negative range
     */
    private static long nextCounter(AtomicLong counter, String label) {
        long value = counter.getAndIncrement();
        if (value < 0) {
            throw new IllegalStateException(label + " counter exhausted.");
        }
        return value;
    }

    /**
     * Generates a cryptographically random AES key and returns its Base64 representation.
     *
     * @param bitLength requested AES key length (128 or 256 bits)
     * @return generated AES key encoded using standard Base64
     * @throws IllegalArgumentException if {@code bitLength} is {@code null}
     */
    public static String generatesKey(AESKeyLength bitLength) {
        if (bitLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        CipherKeyGenerator generator = new CipherKeyGenerator();
        generator.init(new KeyGenerationParameters(random, bitLength.getIntValue()));
        return Base64.toBase64String(generator.generateKey());
    }

    /**
     * Derives an AES-128 or AES-256 key from high-entropy shared secret material using HKDF-SHA-256.
     * The same secret, salt, context string, and output length must be supplied by both peers.
     *
     * @param sharedSecret nonempty raw shared secret from DH, ECDH, ML-KEM, or FrodoKEM
     * @param aesKeyLength requested AES key length (128 or 256 bits)
     * @param salt HKDF salt; between 16 and 1024 bytes for this general-purpose method
     * @param info nonempty UTF-8 context label used for domain separation and protocol binding
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if the shared secret, salt, key length, or context label is
     *         null, empty, or outside its permitted range
     */
    public static byte[] deriveKeyFromSharedSecretHKDFSHA256(
            byte[] sharedSecret, AESKeyLength aesKeyLength, byte[] salt, String info) {
        if (sharedSecret == null || sharedSecret.length == 0) {
            throw new IllegalArgumentException("Shared secret must not be null or empty.");
        }
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        if (salt == null || salt.length < 16 || salt.length > 1024) {
            throw new IllegalArgumentException("HKDF salt must contain between 16 and 1024 bytes.");
        }
        if (info == null || info.isBlank()) {
            throw new IllegalArgumentException("HKDF info must not be null or blank.");
        }

        byte[] output = new byte[aesKeyLength.getIntValue() / 8];
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());
        hkdf.init(new HKDFParameters(sharedSecret, salt, info.getBytes(StandardCharsets.UTF_8)));
        hkdf.generateBytes(output, 0, output.length);
        return output;
    }

    /**
     * Derives an AES-128 or AES-256 key from a password using Argon2id version 1.3, 64 MiB of memory,
     * three iterations, and one lane. The returned key is derived from the password and supplied salt.
     *
     * <p>The salt and Argon2 parameters must be persisted with the protected data so the same key can
     * be derived later. The caller remains responsible for clearing the supplied password array.
     *
     * @param password password characters; must not be {@code null} or empty
     * @param aesKeyLength requested AES key length (128 or 256 bits)
     * @param salt Argon2id salt; between 16 and 1024 bytes
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if the password, salt, or key length is null, empty, or
     *         outside its permitted range
     */
    public static byte[] deriveKeyFromPasswordArgon2id(
            char[] password, AESKeyLength aesKeyLength, byte[] salt) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        if (salt == null || salt.length < 16 || salt.length > 1024) {
            throw new IllegalArgumentException("Argon2id salt must contain between 16 and 1024 bytes.");
        }
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }

        Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withSalt(salt)
                .withIterations(ARGON2_ITERATIONS)
                .withMemoryAsKB(ARGON2_MEMORY_KIB)
                .withParallelism(ARGON2_PARALLELISM)
                .build();
        try {
            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(parameters);
            byte[] derivedKey = new byte[aesKeyLength.getIntValue() / 8];
            generator.generateBytes(password, derivedKey);
            return derivedKey;
        } finally {
            parameters.clear();
        }
    }

    /**
     * Checks whether a byte array has a valid AES key length supported by this class.
     *
     * @param key candidate AES key bytes; may be {@code null}
     * @return {@code true} if the key is exactly 16 or 32 bytes; {@code false} otherwise
     */
    public static boolean checkKey(byte[] key) {
        return key != null && (key.length == 16 || key.length == 32);
    }

    /**
     * Checks whether the supplied AES key and IV have the required lengths for AES-CFB or AES-CTR.
     *
     * @param key candidate AES key bytes
     * @param iv candidate initialization vector
     * @return {@code true} if the key is 16 or 32 bytes and the IV is exactly 16 bytes; otherwise
     *         {@code false}
     */
    public static boolean checkKey(byte[] key, byte[] iv) {
        return checkKey(key) && iv != null && iv.length == AES_BLOCK_LENGTH_BYTES;
    }

    /**
     * Validates an AES key before passing it to a JCA cipher.
     *
     * @param key AES key bytes to validate
     * @throws IllegalArgumentException if the key is not exactly 16 or 32 bytes
     */
    private static void requireKey(byte[] key) {
        if (!checkKey(key)) {
            throw new IllegalArgumentException("AES key must contain exactly 16 or 32 bytes.");
        }
    }

    /**
     * Checks that an input byte array is present.
     *
     * @param data byte array to check
     * @param label human-readable name included in the exception message
     * @throws IllegalArgumentException if {@code data} is {@code null}
     */
    private static void requireData(byte[] data, String label) {
        if (data == null) {
            throw new IllegalArgumentException(label + " must not be null.");
        }
    }

    /**
     * Prepends an IV or nonce to encrypted bytes.
     *
     * @param iv IV or nonce to prepend
     * @param encryptedBytes ciphertext, optionally including an authentication tag
     * @return a new array containing {@code iv || encryptedBytes}
     */
    private static byte[] prependIV(byte[] iv, byte[] encryptedBytes) {
        byte[] result = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, result, iv.length, encryptedBytes.length);
        return result;
    }

    /**
     * Extracts the leading IV or nonce from an encrypted message.
     *
     * @param data encrypted message containing the IV/nonce prefix
     * @param ivLength required prefix length in bytes
     * @return a copy of the first {@code ivLength} bytes
     * @throws IllegalArgumentException if the data is null or shorter than the requested prefix
     */
    private static byte[] extractIV(byte[] data, int ivLength) {
        if (data == null || data.length < ivLength) {
            throw new IllegalArgumentException("Encrypted data is too short to contain the IV/nonce.");
        }
        return Arrays.copyOfRange(data, 0, ivLength);
    }

    /**
     * Returns the ciphertext portion after the IV/nonce prefix.
     *
     * @param data encrypted message containing the IV/nonce prefix
     * @param ivLength number of leading bytes to omit
     * @return a copy of the bytes after the prefix
     * @throws IllegalArgumentException if the data is null or shorter than the requested prefix
     */
    private static byte[] removeIV(byte[] data, int ivLength) {
        if (data == null || data.length < ivLength) {
            throw new IllegalArgumentException("Encrypted data is too short to contain the IV/nonce.");
        }
        return Arrays.copyOfRange(data, ivLength, data.length);
    }

    /**
     * Encrypts arbitrary plaintext bytes with AES-CFB/NoPadding and prefixes a random 16-byte IV.
     * CFB does not authenticate ciphertext; use AES-GCM or an independent authentication mechanism
     * if tamper detection is required.
     *
     * @param originalData plaintext bytes; may be empty but must not be {@code null}
     * @param key AES-128 or AES-256 key bytes
     * @return {@code IV || ciphertext}, with a 16-byte IV prefix
     * @throws IllegalArgumentException if the plaintext or key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if CFB/NoPadding is unavailable
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if the cipher reports a padding error
     * @throws InvalidKeyException if the AES key is rejected
     * @throws InvalidAlgorithmParameterException if the IV is rejected
     */
    public static byte[] encrypt_AES_CFB(byte[] originalData, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        requireData(originalData, "Plaintext");
        requireKey(key);
        byte[] iv = generateRandomIV();
        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return prependIV(iv, cipher.doFinal(originalData));
    }

    /**
     * Decrypts an AES-CFB message in the format {@code 16-byte IV || ciphertext}.
     * CFB does not authenticate ciphertext, so successful decryption does not prove integrity.
     *
     * @param data encrypted message with its 16-byte IV prefix
     * @param key AES-128 or AES-256 key bytes
     * @return decrypted plaintext bytes
     * @throws IllegalArgumentException if the data is too short or the key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if CFB/NoPadding is unavailable
     * @throws InvalidKeyException if the AES key is rejected
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if the cipher reports a padding error
     * @throws InvalidAlgorithmParameterException if the IV is rejected
     */
    public static byte[] decrypt_AES_CFB(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        requireKey(key);
        byte[] iv = extractIV(data, AES_BLOCK_LENGTH_BYTES);
        byte[] ciphertext = removeIV(data, AES_BLOCK_LENGTH_BYTES);
        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    /**
     * Encrypts arbitrary plaintext bytes with AES-CTR/NoPadding and prefixes a random 16-byte IV.
     * CTR does not authenticate ciphertext; use AES-GCM or an independent authentication mechanism
     * if tamper detection is required.
     *
     * @param originalData plaintext bytes; may be empty but must not be {@code null}
     * @param key AES-128 or AES-256 key bytes
     * @return {@code IV || ciphertext}, with a 16-byte IV prefix
     * @throws IllegalArgumentException if the plaintext or key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if CTR/NoPadding is unavailable
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if the cipher reports a padding error
     * @throws InvalidKeyException if the AES key is rejected
     * @throws InvalidAlgorithmParameterException if the IV is rejected
     */
    public static byte[] encrypt_AES_CTR(byte[] originalData, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        requireData(originalData, "Plaintext");
        requireKey(key);
        byte[] iv = generateRandomIV();
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return prependIV(iv, cipher.doFinal(originalData));
    }

    /**
     * Decrypts an AES-CTR message in the format {@code 16-byte IV || ciphertext}.
     * CTR does not authenticate ciphertext, so successful decryption does not prove integrity.
     *
     * @param data encrypted message with its 16-byte IV prefix
     * @param key AES-128 or AES-256 key bytes
     * @return decrypted plaintext bytes
     * @throws IllegalArgumentException if the data is too short or the key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if CTR/NoPadding is unavailable
     * @throws InvalidKeyException if the AES key is rejected
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if the cipher reports a padding error
     * @throws InvalidAlgorithmParameterException if the IV is rejected
     */
    public static byte[] decrypt_AES_CTR(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        requireKey(key);
        byte[] iv = extractIV(data, AES_BLOCK_LENGTH_BYTES);
        byte[] ciphertext = removeIV(data, AES_BLOCK_LENGTH_BYTES);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(ciphertext);
    }

    /**
     * Encrypts arbitrary plaintext bytes with AES-GCM/NoPadding, prefixes a random 96-bit nonce, and
     * appends a 128-bit authentication tag to the ciphertext.
     *
     * @param originalData plaintext bytes; may be empty but must not be {@code null}
     * @param key AES-128 or AES-256 key bytes
     * @return {@code nonce || ciphertext || 128-bit authentication tag}
     * @throws IllegalArgumentException if the plaintext or key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if GCM/NoPadding is unavailable
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if the cipher reports a padding or authentication error
     * @throws InvalidKeyException if the AES key is rejected
     * @throws InvalidAlgorithmParameterException if the nonce or GCM parameters are rejected
     */
    public static byte[] encrypt_AES_GCM(byte[] originalData, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        requireData(originalData, "Plaintext");
        requireKey(key);
        byte[] nonce = generateNonce();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        return prependIV(nonce, cipher.doFinal(originalData));
    }

    /**
     * Decrypts an AES-GCM message in the format
     * {@code 12-byte nonce || ciphertext || 128-bit authentication tag}.
     * The JCA cipher verifies the tag before returning plaintext.
     *
     * @param data encrypted message containing a nonce, ciphertext, and complete authentication tag
     * @param key AES-128 or AES-256 key bytes
     * @return authenticated plaintext bytes
     * @throws IllegalArgumentException if the data does not contain at least a nonce and tag or the
     *         key is invalid
     * @throws NoSuchAlgorithmException if AES is unavailable
     * @throws NoSuchPaddingException if GCM/NoPadding is unavailable
     * @throws InvalidKeyException if the AES key is rejected
     * @throws IllegalBlockSizeException if the cipher rejects the input length
     * @throws BadPaddingException if authentication fails or the cipher reports a padding error
     * @throws InvalidAlgorithmParameterException if the nonce or GCM parameters are rejected
     */
    public static byte[] decrypt_AES_GCM(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        requireKey(key);
        if (data == null || data.length < GCM_NONCE_LENGTH_BYTES + GCM_TAG_LENGTH_BITS / 8) {
            throw new IllegalArgumentException("AES-GCM input must contain a nonce and a complete 128-bit tag.");
        }
        byte[] nonce = extractIV(data, GCM_NONCE_LENGTH_BYTES);
        byte[] ciphertextAndTag = removeIV(data, GCM_NONCE_LENGTH_BYTES);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
        return cipher.doFinal(ciphertextAndTag);
    }

    /**
     * Encodes a byte array using standard Base64.
     *
     * @param data bytes to encode; must not be {@code null}
     * @return standard Base64 representation of the input bytes
     * @throws IllegalArgumentException if {@code data} is {@code null}
     */
    public static String encodeBase64(byte[] data) {
        requireData(data, "Data");
        return Base64.toBase64String(data);
    }

    /**
     * Decodes a standard Base64 string into its original bytes.
     *
     * @param base64Data Base64 text to decode; must not be {@code null}
     * @return decoded binary data
     * @throws IllegalArgumentException if the input is {@code null} or is not valid Base64 text
     */
    public static byte[] decodeBase64(String base64Data) {
        if (base64Data == null) {
            throw new IllegalArgumentException("Base64 data must not be null.");
        }
        return Base64.decode(base64Data);
    }
}
