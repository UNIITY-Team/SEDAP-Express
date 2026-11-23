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

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;

/**
 * Provides Elliptic-Curve Diffie-Hellman operations restricted to the BrainpoolP256r1 curve.
 *
 * <p>Key-pair generation uses the single shared random generator
 * {@link EncryptionUtils#random}. The ECDH result is treated as raw shared secret material and is
 * passed to the common HKDF-SHA-256 implementation in {@link EncryptionUtils} to derive the final
 * AES key. The SEDAP-Express profile uses a 32-byte salt.
 *
 * <p>Callers must authenticate the exchange transcript; ECDH by itself does not authenticate the
 * communicating peers.
 *
 * @author Volker Voß
 * @since 1.5.5
 * @see EncryptionUtils
 * @see AESKeyLength
 */
public final class ECDHUtils {

    /** Standard JCA curve name accepted by the Bouncy Castle provider. */
    private static final String CURVE_NAME = "brainpoolP256r1";

    /** HKDF context label identifying the SEDAP-Express BrainpoolP256r1 key derivation. */
    private static final String KDF_INFO = "SEDAP-Express v1 AES session key / ECDH brainpoolP256r1";

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Prevents instantiation of this utility class.
     *
     * @throws AssertionError always, because this class only provides static utility methods
     */
    private ECDHUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Generates an ephemeral ECDH key pair on BrainpoolP256r1.
     * The {@link EncryptionUtils#random} instance is supplied to the key-pair generator.
     *
     * @return newly generated BrainpoolP256r1 public/private key pair
     * @throws NoSuchAlgorithmException if the configured provider does not implement ECDH
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidAlgorithmParameterException if the provider rejects BrainpoolP256r1
     */
    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(new ECGenParameterSpec(CURVE_NAME), EncryptionUtils.random);
        return generator.generateKeyPair();
    }

    /**
     * Determines whether an EC key uses the exact BrainpoolP256r1 domain parameters.
     *
     * @param key EC public or private key whose parameters are to be checked
     * @return {@code true} if the curve, base point, subgroup order, and cofactor match
     *         BrainpoolP256r1; {@code false} otherwise
     * @throws InvalidKeyException if the key has no EC parameters or the expected curve parameters
     *         cannot be loaded
     * @throws NoSuchAlgorithmException if the EC algorithm-parameters implementation is unavailable
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     */
    private static boolean isBrainpoolP256r1(ECKey key)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (key.getParams() == null) {
            throw new InvalidKeyException("EC key parameters must not be null.");
        }
        try {
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            parameters.init(new ECGenParameterSpec(CURVE_NAME));
            ECParameterSpec expected = parameters.getParameterSpec(ECParameterSpec.class);
            ECParameterSpec actual = key.getParams();
            return Objects.equals(expected.getCurve(), actual.getCurve())
                    && Objects.equals(expected.getGenerator(), actual.getGenerator())
                    && Objects.equals(expected.getOrder(), actual.getOrder())
                    && expected.getCofactor() == actual.getCofactor();
        } catch (java.security.spec.InvalidParameterSpecException exception) {
            throw new InvalidKeyException("Unable to validate brainpoolP256r1 parameters.", exception);
        }
    }

    /**
     * Performs ECDH with the local private key and remote public key on BrainpoolP256r1, then derives
     * an AES-128 or AES-256 key using HKDF-SHA-256.
     *
     * @param ownSecretKey local BrainpoolP256r1 EC private key
     * @param otherPublicKey remote peer's BrainpoolP256r1 EC public key
     * @param aesKeyLength negotiated output AES key length (128 or 256 bits)
     * @param salt common 32-byte HKDF salt supplied identically by both peers
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if either key is not of the required EC key type, the AES key
     *         length is {@code null}, or the salt is not exactly 32 bytes
     * @throws InvalidKeyException if either key does not use BrainpoolP256r1 or the key agreement
     *         implementation rejects the supplied key material
     * @throws NoSuchAlgorithmException if ECDH or HKDF-SHA-256 is unavailable
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     */
    public static byte[] getSharedSecretKeyHKDFSHA256(PrivateKey ownSecretKey, PublicKey otherPublicKey,
            AESKeyLength aesKeyLength, byte[] salt)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (!(ownSecretKey instanceof ECPrivateKey)) {
            throw new IllegalArgumentException("The local private key must be an EC private key.");
        }
        if (!(otherPublicKey instanceof ECPublicKey)) {
            throw new IllegalArgumentException("The remote public key must be an EC public key.");
        }
        if (!isBrainpoolP256r1((ECKey) ownSecretKey) || !isBrainpoolP256r1((ECKey) otherPublicKey)) {
            throw new InvalidKeyException("Both ECDH keys must use the brainpoolP256r1 curve.");
        }
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        if (salt == null || salt.length != EncryptionUtils.SALT_LENGTH_BYTES) {
            throw new IllegalArgumentException("HKDF salt must contain exactly "
                    + EncryptionUtils.SALT_LENGTH_BYTES + " bytes.");
        }

        KeyAgreement agreement = KeyAgreement.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
        agreement.init(ownSecretKey);
        agreement.doPhase(otherPublicKey, true);
        byte[] sharedSecret = agreement.generateSecret();
        try {
            return EncryptionUtils.deriveKeyFromSharedSecretHKDFSHA256(
                    sharedSecret, aesKeyLength, salt, KDF_INFO);
        } finally {
            Arrays.fill(sharedSecret, (byte) 0);
        }
    }
}
