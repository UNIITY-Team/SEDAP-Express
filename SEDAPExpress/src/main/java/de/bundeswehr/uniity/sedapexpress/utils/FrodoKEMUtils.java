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
import org.bouncycastle.crypto.kems.FrodoKEMExtractor;
import org.bouncycastle.crypto.kems.FrodoKEMGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.FrodoKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.FrodoKEMPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.FrodoKEMParameterSpec;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AlgorithmType;

/**
 * Provides FrodoKEM key-pair generation, encapsulation, and decapsulation for the AES parameter
 * sets FrodoKEM-976-AES and FrodoKEM-1344-AES available in Bouncy Castle 1.87.
 *
 * <p>FrodoKEM-640-AES is intentionally not exposed because it is not supported by the Bouncy Castle
 * version used by this project. The raw KEM shared secret is passed directly to the common
 * HKDF-SHA-256 implementation; Bouncy Castle's default KDF3 layer is not applied before HKDF.
 * FrodoKEM-976-AES produces a 192-bit raw KEM secret and FrodoKEM-1344-AES produces a 256-bit raw
 * KEM secret in Bouncy Castle 1.87. HKDF expands either value to the negotiated AES key length.
 *
 * <p>All random operations use {@link EncryptionUtils#random}; this class does not create an
 * independent random generator.
 *
 * @author Volker Voß
 * @since 1.5.5
 * @see EncryptionUtils
 * @see AlgorithmType
 * @see AESKeyLength
 */
public final class FrodoKEMUtils {

    /** JCA provider used for FrodoKEM key-pair generation. */
    private static final String PROVIDER = "BC";

    /** Context prefix used for the algorithm-specific HKDF-SHA-256 derivation. */
    private static final String KDF_INFO_PREFIX = "SEDAP-Express v1 AES session key / ";

    /**
     * Prevents instantiation of this utility class.
     *
     * @throws AssertionError always, because this class only provides static utility methods
     */
    private FrodoKEMUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Generates a FrodoKEM key pair for the supplied Bouncy Castle AES parameter set.
     *
     * @param parameterSpec FrodoKEM-976-AES or FrodoKEM-1344-AES parameter specification
     * @return newly generated FrodoKEM public/private key pair
     * @throws IllegalArgumentException if {@code parameterSpec} is {@code null}
     * @throws NoSuchAlgorithmException if the configured provider does not implement FrodoKEM
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidAlgorithmParameterException if the provider rejects the parameter set
     */
    public static KeyPair generateKeyPair(FrodoKEMParameterSpec parameterSpec)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (parameterSpec == null) {
            throw new IllegalArgumentException("FrodoKEM parameter set must not be null.");
        }
        String name = parameterSpec.getName();
        if (!"frodokem976aes".equals(name) && !"frodokem1344aes".equals(name)) {
            throw new IllegalArgumentException("Only FrodoKEM-976-AES and FrodoKEM-1344-AES are supported.");
        }
        KeyPairGenerator generator = KeyPairGenerator.getInstance("FrodoKEM", PROVIDER);
        generator.initialize(parameterSpec, EncryptionUtils.random);
        return generator.generateKeyPair();
    }

    /**
     * Maps a SEDAP-Express algorithm identifier to its FrodoKEM AES parameter set and generates a
     * corresponding key pair.
     *
     * @param algorithmType algorithm identifier; only {@code FrodoKEM976} and {@code FrodoKEM1344}
     *        are supported
     * @return newly generated FrodoKEM public/private key pair for the selected AES parameter set
     * @throws IllegalArgumentException if the identifier is {@code null} or does not denote a
     *         supported FrodoKEM AES variant
     * @throws NoSuchAlgorithmException if the configured provider does not implement FrodoKEM
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidAlgorithmParameterException if the provider rejects the selected parameter set
     */
    public static KeyPair generateKeyPair(AlgorithmType algorithmType)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if (algorithmType == null) {
            throw new IllegalArgumentException("Algorithm type must not be null.");
        }
        FrodoKEMParameterSpec spec = switch (algorithmType) {
            case FrodoKEM976 -> FrodoKEMParameterSpec.frodokem976aes;
            case FrodoKEM1344 -> FrodoKEMParameterSpec.frodokem1344aes;
            default -> throw new IllegalArgumentException("The selected algorithm is not a supported FrodoKEM AES variant: " + algorithmType);
        };
        return generateKeyPair(spec);
    }

    /**
     * Encapsulates to the supplied FrodoKEM public key, derives the AES key from the raw FrodoKEM
     * shared secret using HKDF-SHA-256, and returns both the derived AES key and encapsulation.
     *
     * @param publicKey recipient's FrodoKEM-976-AES or FrodoKEM-1344-AES public key
     * @param aesKeyLength required AES output key length (128 or 256 bits)
     * @param salt common 32-byte HKDF salt exchanged during the KEYEXCHANGE protocol
     * @return a {@link SecretKeyWithEncapsulation} whose encoded key is the derived AES key and whose
     *         encapsulation is the ciphertext that must be sent to the recipient
     * @throws IllegalArgumentException if a required argument is {@code null}, the salt is not
     *         exactly 32 bytes, the AES key length is not specified, or the key encoding is invalid
     *         or is not a supported FrodoKEM AES public key
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
        FrodoKEMPublicKeyParameters publicParameters = decodePublicKey(publicKey);

        SecretWithEncapsulation kemResult = new FrodoKEMGenerator(EncryptionUtils.random)
                .generateEncapsulated(publicParameters);
        byte[] rawSecret = kemResult.getSecret();
        byte[] encapsulation = kemResult.getEncapsulation();
        if (rawSecret == null || rawSecret.length == 0) {
            destroy(kemResult);
            throw new IllegalStateException("The BC FrodoKEM provider returned an empty raw shared secret.");
        }
        if (encapsulation == null || encapsulation.length == 0) {
            Arrays.fill(rawSecret, (byte) 0);
            destroy(kemResult);
            throw new IllegalStateException("The BC FrodoKEM provider returned an empty encapsulation.");
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
     * Decapsulates a FrodoKEM ciphertext with the recipient's private key and derives the same AES
     * key that the encapsulating peer obtained from the corresponding raw shared secret.
     *
     * @param privateKey recipient's FrodoKEM-976-AES or FrodoKEM-1344-AES private key
     * @param encapsulation ciphertext returned by the encapsulating peer
     * @param aesKeyLength negotiated AES output key length (128 or 256 bits)
     * @param salt the same 32-byte HKDF salt used by the encapsulating peer
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if a required argument is {@code null}, the encapsulation is
     *         empty, the salt is not exactly 32 bytes, the AES key length is not specified, or the
     *         key encoding is invalid or is not a supported FrodoKEM AES private key
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
            throw new IllegalArgumentException("FrodoKEM encapsulation must not be null or empty.");
        }
        validateSalt(salt);
        FrodoKEMPrivateKeyParameters privateParameters = decodePrivateKey(privateKey);

        String info = KDF_INFO_PREFIX + privateParameters.getParameters().getName();
        byte[] rawSecret;
        try {
            rawSecret = new FrodoKEMExtractor(privateParameters).extractSecret(encapsulation);
        } finally {
            destroyPrivateKey(privateParameters);
        }
        if (rawSecret == null || rawSecret.length == 0) {
            if (rawSecret != null) {
                Arrays.fill(rawSecret, (byte) 0);
            }
            throw new IllegalStateException("The BC FrodoKEM provider returned an empty raw shared secret.");
        }
        try {
            return EncryptionUtils.deriveKeyFromSharedSecretHKDFSHA256(
                    rawSecret, aesKeyLength, salt, info);
        } finally {
            Arrays.fill(rawSecret, (byte) 0);
        }
    }

    /**
     * Decodes and validates an X.509 SubjectPublicKeyInfo encoding as FrodoKEM-976-AES or
     * FrodoKEM-1344-AES public-key parameters.
     *
     * @param publicKey JCA public key to decode
     * @return Bouncy Castle FrodoKEM public-key parameters for a supported AES set
     * @throws IllegalArgumentException if the key or its encoding is {@code null}
     * @throws IllegalArgumentException if decoding fails or the key is not one of the supported AES sets
     */
    private static FrodoKEMPublicKeyParameters decodePublicKey(PublicKey publicKey) {
        if (publicKey == null || publicKey.getEncoded() == null) {
            throw new IllegalArgumentException("A non-null, encodable FrodoKEM public key is required.");
        }
        try {
            AsymmetricKeyParameter parameters = PublicKeyFactory.createKey(publicKey.getEncoded());
            if (parameters instanceof FrodoKEMPublicKeyParameters frodoParameters
                    && isSupportedAesParameterSet(frodoParameters.getParameters().getName())) {
                return frodoParameters;
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to decode the FrodoKEM public key.", exception);
        }
        throw new IllegalArgumentException("The supplied key is not a supported FrodoKEM AES public key.");
    }

    /**
     * Decodes and validates a PKCS#8 private-key encoding as FrodoKEM-976-AES or
     * FrodoKEM-1344-AES private-key parameters.
     *
     * @param privateKey JCA private key to decode
     * @return Bouncy Castle FrodoKEM private-key parameters for a supported AES set
     * @throws IllegalArgumentException if the key or its encoding is {@code null}
     * @throws IllegalArgumentException if decoding fails or the key is not one of the supported AES sets
     */
    private static FrodoKEMPrivateKeyParameters decodePrivateKey(PrivateKey privateKey) {
        if (privateKey == null || privateKey.getEncoded() == null) {
            throw new IllegalArgumentException("A non-null, encodable FrodoKEM private key is required.");
        }
        try {
            AsymmetricKeyParameter parameters = PrivateKeyFactory.createKey(privateKey.getEncoded());
            if (parameters instanceof FrodoKEMPrivateKeyParameters frodoParameters
                    && isSupportedAesParameterSet(frodoParameters.getParameters().getName())) {
                return frodoParameters;
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to decode the FrodoKEM private key.", exception);
        }
        throw new IllegalArgumentException("The supplied key is not a supported FrodoKEM AES private key.");
    }

    /**
     * Destroys the temporary private-key parameter copy created by {@link PrivateKeyFactory}.
     *
     * @param privateParameters decoded private-key parameters to clear
     */
    private static void destroyPrivateKey(FrodoKEMPrivateKeyParameters privateParameters) {
        privateParameters.destroy();
    }

    /**
     * Checks whether the Bouncy Castle parameter-set name is one of the two FrodoKEM AES sets
     * supported by this SEDAP-Express implementation.
     *
     * @param parameterName canonical Bouncy Castle FrodoKEM parameter-set name
     * @return {@code true} for FrodoKEM-976-AES or FrodoKEM-1344-AES; {@code false} otherwise
     */
    private static boolean isSupportedAesParameterSet(String parameterName) {
        return "frodokem976aes".equals(parameterName) || "frodokem1344aes".equals(parameterName);
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
