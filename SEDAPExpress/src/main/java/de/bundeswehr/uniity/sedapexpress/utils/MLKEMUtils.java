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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AlgorithmType;

/**
 * Provides ML-KEM key-pair generation, encapsulation, and decapsulation for ML-KEM-512,
 * ML-KEM-768, and ML-KEM-1024 using Bouncy Castle 1.87.
 *
 * <p>The encapsulation and decapsulation operations use Bouncy Castle's low-level KEM interfaces
 * so that the raw 32-byte ML-KEM shared secret is passed directly to the common HKDF-SHA-256
 * implementation. This avoids applying Bouncy Castle's default KDF3 before the SEDAP-Express HKDF
 * step. The final AES key and encapsulation ciphertext are returned together for the sending peer.
 *
 * <p>All random operations use {@link EncryptionUtils#random}; no additional random generator is
 * created by this class.
 *
 * @author Volker Voß
 * @since 1.5.5
 * @see EncryptionUtils
 * @see AlgorithmType
 * @see AESKeyLength
 */
public final class MLKEMUtils {

    /** JCA provider used for ML-KEM key-pair generation. */
    private static final String PROVIDER = "BC";

    /** Context prefix used for the algorithm-specific HKDF-SHA-256 derivation. */
    private static final String KDF_INFO_PREFIX = "SEDAP-Express v1 AES session key / ";

    /**
     * Prevents instantiation of this utility class.
     *
     * @throws AssertionError always, because this class only provides static utility methods
     */
    private MLKEMUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Generates a key pair for the ML-KEM parameter set supplied by the caller.
     *
     * @param parameterSpec ML-KEM-512, ML-KEM-768, or ML-KEM-1024 parameter specification
     * @return newly generated ML-KEM public/private key pair
     * @throws IllegalArgumentException if {@code parameterSpec} is {@code null}
     * @throws NoSuchAlgorithmException if the configured provider does not implement ML-KEM
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidAlgorithmParameterException if the provider rejects the parameter set
     */
    public static KeyPair generateKeyPair(MLKEMParameterSpec parameterSpec)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (parameterSpec == null) {
            throw new IllegalArgumentException("ML-KEM parameter set must not be null.");
        }
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ML-KEM", PROVIDER);
        generator.initialize(parameterSpec, EncryptionUtils.random);
        return generator.generateKeyPair();
    }

    /**
     * Maps a SEDAP-Express algorithm identifier to its corresponding ML-KEM parameter set and
     * generates a key pair.
     *
     * @param algorithmType algorithm identifier; only {@code MLKEM512}, {@code MLKEM768}, and
     *        {@code MLKEM1024} are accepted
     * @return newly generated ML-KEM public/private key pair for the selected parameter set
     * @throws IllegalArgumentException if the identifier is {@code null} or does not denote an
     *         ML-KEM variant
     * @throws NoSuchAlgorithmException if the configured provider does not implement ML-KEM
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidAlgorithmParameterException if the provider rejects the selected parameter set
     */
    public static KeyPair generateKeyPair(AlgorithmType algorithmType)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (algorithmType == null) {
            throw new IllegalArgumentException("Algorithm type must not be null.");
        }
        MLKEMParameterSpec spec = switch (algorithmType) {
            case MLKEM512 -> MLKEMParameterSpec.ml_kem_512;
            case MLKEM768 -> MLKEMParameterSpec.ml_kem_768;
            case MLKEM1024 -> MLKEMParameterSpec.ml_kem_1024;
            default -> throw new IllegalArgumentException("The selected algorithm is not an ML-KEM variant: " + algorithmType);
        };
        return generateKeyPair(spec);
    }

    /**
     * Encapsulates to the supplied ML-KEM public key, derives the AES key from the raw ML-KEM
     * shared secret using HKDF-SHA-256, and returns both the derived AES key and the encapsulation.
     *
     * @param publicKey recipient's ML-KEM public key; its encoding must be understood by Bouncy Castle
     * @param aesKeyLength required AES output key length (128 or 256 bits)
     * @param salt common 32-byte HKDF salt exchanged during the KEYEXCHANGE protocol
     * @return a {@link SecretKeyWithEncapsulation} whose encoded key is the derived AES key and whose
     *         encapsulation is the ciphertext that must be sent to the recipient
     * @throws IllegalArgumentException if a required argument is {@code null}, the salt is not
     *         exactly 32 bytes, the AES key length is not specified, or the key encoding is invalid
     *         or is not a supported ML-KEM public key
     * @throws NoSuchAlgorithmException if HKDF-SHA-256 is unavailable
     * @throws NoSuchProviderException retained for API compatibility; the low-level KEM operation
     *         does not perform a provider lookup
     * @throws InvalidAlgorithmParameterException retained for API compatibility with the former JCA
     *         KEM implementation
     */
    public static SecretKeyWithEncapsulation generateSharedSecretKeyWithEncapsulation(
            PublicKey publicKey, AESKeyLength aesKeyLength, byte[] salt)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        validateSalt(salt);
        MLKEMPublicKeyParameters publicParameters = decodePublicKey(publicKey);

        SecretWithEncapsulation kemResult = new MLKEMGenerator(EncryptionUtils.random)
                .generateEncapsulated(publicParameters);
        byte[] rawSecret = kemResult.getSecret();
        byte[] encapsulation = kemResult.getEncapsulation();
        if (rawSecret == null || rawSecret.length != 32) {
            destroy(kemResult);
            throw new IllegalStateException("The BC ML-KEM provider returned an invalid raw shared secret.");
        }
        if (encapsulation == null || encapsulation.length == 0) {
            Arrays.fill(rawSecret, (byte) 0);
            destroy(kemResult);
            throw new IllegalStateException("The BC ML-KEM provider returned an empty encapsulation.");
        }

        try {
            String info = KDF_INFO_PREFIX + publicParameters.getParameters().getName();
            byte[] aesKey = EncryptionUtils.deriveKeyFromSharedSecretHKDFSHA256(
                    rawSecret, aesKeyLength, salt, info);
            try {
                return new SecretKeyWithEncapsulation(
                        new SecretKeySpec(aesKey, "AES"), encapsulation.clone());
            } finally {
                Arrays.fill(aesKey, (byte) 0);
            }
        } finally {
            Arrays.fill(rawSecret, (byte) 0);
            destroy(kemResult);
        }
    }

    /**
     * Decapsulates an ML-KEM ciphertext with the recipient's private key and derives the same AES key
     * that the encapsulating peer obtained from the corresponding raw shared secret.
     *
     * @param privateKey recipient's ML-KEM private key
     * @param encapsulation ciphertext returned by the encapsulating peer
     * @param aesKeyLength negotiated AES output key length (128 or 256 bits)
     * @param salt the same 32-byte HKDF salt used by the encapsulating peer
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if a required argument is {@code null}, the encapsulation is
     *         empty, the salt is not exactly 32 bytes, the AES key length is not specified, or the
     *         key encoding is invalid or is not a supported ML-KEM private key
     * @throws NoSuchAlgorithmException if HKDF-SHA-256 is unavailable
     * @throws NoSuchProviderException retained for API compatibility; the low-level KEM operation
     *         does not perform a provider lookup
     * @throws InvalidAlgorithmParameterException retained for API compatibility with the former JCA
     *         KEM implementation
     */
    public static byte[] generateSharedSecretKeyFromEncapsulation(
            PrivateKey privateKey, byte[] encapsulation, AESKeyLength aesKeyLength, byte[] salt)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        if (encapsulation == null || encapsulation.length == 0) {
            throw new IllegalArgumentException("ML-KEM encapsulation must not be null or empty.");
        }
        validateSalt(salt);
        MLKEMPrivateKeyParameters privateParameters = decodePrivateKey(privateKey);

        String info = KDF_INFO_PREFIX + privateParameters.getParameters().getName();
        byte[] rawSecret;
        try {
            rawSecret = new MLKEMExtractor(privateParameters).extractSecret(encapsulation);
        } finally {
            destroyPrivateKey(privateParameters);
        }
        if (rawSecret == null || rawSecret.length != 32) {
            if (rawSecret != null) {
                Arrays.fill(rawSecret, (byte) 0);
            }
            throw new IllegalStateException("The BC ML-KEM provider returned an invalid raw shared secret.");
        }
        try {
            return EncryptionUtils.deriveKeyFromSharedSecretHKDFSHA256(
                    rawSecret, aesKeyLength, salt, info);
        } finally {
            Arrays.fill(rawSecret, (byte) 0);
        }
    }

    /**
     * Decodes and validates an X.509 SubjectPublicKeyInfo encoding as a supported ML-KEM public key.
     *
     * @param publicKey JCA public key to decode
     * @return Bouncy Castle ML-KEM public-key parameters
     * @throws IllegalArgumentException if the key or its encoding is {@code null}
     * @throws IllegalArgumentException if decoding fails or the key is not ML-KEM-512/768/1024
     */
    private static MLKEMPublicKeyParameters decodePublicKey(PublicKey publicKey) {
        if (publicKey == null || publicKey.getEncoded() == null) {
            throw new IllegalArgumentException("A non-null, encodable ML-KEM public key is required.");
        }
        try {
            AsymmetricKeyParameter parameters = PublicKeyFactory.createKey(publicKey.getEncoded());
            if (parameters instanceof MLKEMPublicKeyParameters mlkemParameters) {
                String name = mlkemParameters.getParameters().getName();
                if ("ML-KEM-512".equals(name) || "ML-KEM-768".equals(name) || "ML-KEM-1024".equals(name)) {
                    return mlkemParameters;
                }
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to decode the ML-KEM public key.", exception);
        }
        throw new IllegalArgumentException("The supplied key is not a supported ML-KEM public key.");
    }

    /**
     * Decodes and validates a PKCS#8 private-key encoding as a supported ML-KEM private key.
     *
     * @param privateKey JCA private key to decode
     * @return Bouncy Castle ML-KEM private-key parameters
     * @throws IllegalArgumentException if the key or its encoding is {@code null}
     * @throws IllegalArgumentException if decoding fails or the key is not ML-KEM-512/768/1024
     */
    private static MLKEMPrivateKeyParameters decodePrivateKey(PrivateKey privateKey) {
        if (privateKey == null || privateKey.getEncoded() == null) {
            throw new IllegalArgumentException("A non-null, encodable ML-KEM private key is required.");
        }
        try {
            AsymmetricKeyParameter parameters = PrivateKeyFactory.createKey(privateKey.getEncoded());
            if (parameters instanceof MLKEMPrivateKeyParameters mlkemParameters) {
                String name = mlkemParameters.getParameters().getName();
                if ("ML-KEM-512".equals(name) || "ML-KEM-768".equals(name) || "ML-KEM-1024".equals(name)) {
                    return mlkemParameters;
                }
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to decode the ML-KEM private key.", exception);
        }
        throw new IllegalArgumentException("The supplied key is not a supported ML-KEM private key.");
    }

    /**
     * Destroys the temporary private-key parameter copy created by {@link PrivateKeyFactory}.
     *
     * @param privateParameters decoded private-key parameters to clear
     */
    private static void destroyPrivateKey(MLKEMPrivateKeyParameters privateParameters) {
        privateParameters.destroy();
    }

    /**
     * Destroys the temporary KEM result after its secret has been copied or explicitly cleared.
     *
     * @param result encapsulation result to destroy; may be {@code null}
     */
    private static void destroy(SecretWithEncapsulation result) {
        if (result != null) {
            try {
                result.destroy();
            } catch (DestroyFailedException ignored) {
                // The raw secret byte array is explicitly cleared by the calling method.
            }
        }
    }

    /**
     * Enforces the fixed salt size used by the SEDAP-Express KEM profile.
     *
     * @param salt salt bytes to validate
     * @throws IllegalArgumentException if the salt is {@code null} or not exactly 32 bytes
     */
    private static void validateSalt(byte[] salt) {
        if (salt == null || salt.length != EncryptionUtils.SALT_LENGTH_BYTES) {
            throw new IllegalArgumentException("HKDF salt must contain exactly "
                    + EncryptionUtils.SALT_LENGTH_BYTES + " bytes.");
        }
    }
}
