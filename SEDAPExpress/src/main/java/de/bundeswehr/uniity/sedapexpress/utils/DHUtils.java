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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.DHParameterLength;

/**
 * Provides finite-field Diffie-Hellman (DH) operations for the RFC 7919 FFDHE-2048,
 * FFDHE-3072, and FFDHE-4096 groups.
 *
 * <p>All generated key pairs use {@link EncryptionUtils#random}; this class does not create an
 * independent random-number generator. DH shared secrets are converted into AES-128 or AES-256
 * keys with the common HKDF-SHA-256 implementation in {@link EncryptionUtils}.
 *
 * <p>Only the listed, predefined RFC 7919 groups are accepted. This prevents callers from using
 * arbitrary peer-supplied domain parameters. The caller is still responsible for authenticating
 * the complete key-exchange transcript.
 *
 * @author Volker Voß
 * @since 1.5.5
 * @see EncryptionUtils
 * @see DHParameterLength
 * @see AESKeyLength
 */
public final class DHUtils {

    /** Private exponent length used for generated FFDHE-2048 and FFDHE-3072 keys, in bits. */
    private static final int PRIVATE_EXPONENT_LENGTH_2048_3072 = 256;

    /** Minimum private exponent length required by Bouncy Castle for FFDHE-4096, in bits. */
    private static final int PRIVATE_EXPONENT_LENGTH_4096 = 304;

    /** HKDF context label identifying the SEDAP-Express DH key derivation. */
    private static final String KDF_INFO = "SEDAP-Express v1 AES session key / DH";

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
    private DHUtils() {
        throw new AssertionError("No instances");
    }

    /**
     * Returns the predefined RFC 7919 FFDHE group matching the requested modulus length.
     * The returned specification uses a 256-bit private exponent for FFDHE-2048/3072 and a
     * 304-bit private exponent for FFDHE-4096, as required by the Bouncy Castle provider.
     *
     * @param parameterLength requested DH modulus length; must be one of 2048, 3072, or 4096 bits
     * @return the corresponding validated DH parameter specification
     * @throws IllegalArgumentException if {@code parameterLength} is {@code null}
     * @throws InvalidParameterSpecException retained in the method signature for API compatibility;
     *         the predefined group lookup does not currently throw this exception
     * @throws NoSuchAlgorithmException retained in the method signature for API compatibility;
     *         the predefined group lookup does not currently throw this exception
     * @throws NoSuchProviderException retained in the method signature for API compatibility;
     *         the predefined group lookup does not currently throw this exception
     */
    public static DHParameterSpec generateVariables(DHParameterLength parameterLength)
            throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException {
        if (parameterLength == null) {
            throw new IllegalArgumentException("DH parameter length must not be null.");
        }
        DHParameters group = switch (parameterLength) {
            case Parameter_LENGTH_2048_BITS -> DHStandardGroups.rfc7919_ffdhe2048;
            case Parameter_LENGTH_3072_BITS -> DHStandardGroups.rfc7919_ffdhe3072;
            case Parameter_LENGTH_4096_BITS -> DHStandardGroups.rfc7919_ffdhe4096;
        };
        int privateExponentLength = parameterLength == DHParameterLength.Parameter_LENGTH_4096_BITS
                ? PRIVATE_EXPONENT_LENGTH_4096 : PRIVATE_EXPONENT_LENGTH_2048_3072;
        return new DHParameterSpec(group.getP(), group.getG(), privateExponentLength);
    }

    /**
     * Reconstructs a DH parameter specification from the supplied modulus and generator, but only
     * if both values exactly match one of the supported RFC 7919 FFDHE groups.
     *
     * @param p public prime modulus of the DH group
     * @param g public generator belonging to the DH group
     * @return the validated parameter specification with a group-appropriate private exponent length
     * @throws IllegalArgumentException if either argument is {@code null} or the pair does not match
     *         a supported RFC 7919 group
     * @throws InvalidParameterSpecException retained in the method signature for API compatibility;
     *         the supported group lookup does not currently throw this exception
     * @throws NoSuchAlgorithmException retained in the method signature for API compatibility;
     *         the supported group lookup does not currently throw this exception
     * @throws NoSuchProviderException retained in the method signature for API compatibility;
     *         the supported group lookup does not currently throw this exception
     */
    public static DHParameterSpec generateVariables(BigInteger p, BigInteger g)
            throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException {
        if (p == null || g == null) {
            throw new IllegalArgumentException("DH p and g must not be null.");
        }
        for (DHParameters group : supportedGroups()) {
            if (group.getP().equals(p) && group.getG().equals(g)) {
                int privateExponentLength = group.getP().bitLength() == 4096
                        ? PRIVATE_EXPONENT_LENGTH_4096 : PRIVATE_EXPONENT_LENGTH_2048_3072;
                return new DHParameterSpec(group.getP(), group.getG(), privateExponentLength);
            }
        }
        throw new IllegalArgumentException("Unsupported DH group. Only RFC 7919 FFDHE-2048/3072/4096 are accepted.");
    }

    /**
     * Generates a DH key pair for a previously validated parameter specification.
     * The shared {@link EncryptionUtils#random} instance supplies all random bytes.
     *
     * @param parameterSpec validated parameters for one of the supported RFC 7919 FFDHE groups
     * @return newly generated DH public/private key pair
     * @throws IllegalArgumentException if the parameters are {@code null}, unsupported, or specify
     *         an insufficient private exponent length
     * @throws NoSuchAlgorithmException if the configured provider does not implement DH
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     * @throws InvalidParameterSpecException retained in the method signature for API compatibility
     * @throws InvalidAlgorithmParameterException if the provider rejects the supplied parameters
     */
    public static KeyPair generateKeyPair(DHParameterSpec parameterSpec)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidParameterSpecException,
            InvalidAlgorithmParameterException {
        validateParameters(parameterSpec);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("DH", BouncyCastleProvider.PROVIDER_NAME);
        generator.initialize(parameterSpec, EncryptionUtils.random);
        return generator.generateKeyPair();
    }

    /**
     * Returns the exact set of domain parameters accepted by this class.
     *
     * @return a new array containing the supported 2048-, 3072-, and 4096-bit RFC 7919 groups
     */
    private static DHParameters[] supportedGroups() {
        return new DHParameters[] {
                DHStandardGroups.rfc7919_ffdhe2048,
                DHStandardGroups.rfc7919_ffdhe3072,
                DHStandardGroups.rfc7919_ffdhe4096
        };
    }

    /**
     * Combines the two independently generated DH salts using SHA-256.
     * The order is significant and must be identical at both endpoints: client salt first,
     * server salt second. Each input salt must contain exactly
     * {@link EncryptionUtils#SALT_LENGTH_BYTES} bytes, as required by the SEDAP-Express profile.
     *
     * @param saltClient fresh 32-byte salt sent by the client
     * @param saltServer fresh 32-byte salt sent by the server
     * @return SHA-256 digest of {@code saltClient || saltServer}, suitable as the HKDF salt
     * @throws IllegalArgumentException if either salt is {@code null} or not exactly 32 bytes
     * @throws NoSuchAlgorithmException if SHA-256 is unavailable from the runtime
     */
    public static byte[] combineSalts(byte[] saltClient, byte[] saltServer) throws NoSuchAlgorithmException {
        validateSalt(saltClient, "Client salt");
        validateSalt(saltServer, "Server salt");
        return MessageDigest.getInstance("SHA-256")
                .digest(ByteBuffer.allocate(saltClient.length + saltServer.length)
                        .put(saltClient).put(saltServer).array());
    }

    /**
     * Performs finite-field DH with the supplied local private key and remote public key, then
     * derives an AES-128 or AES-256 key using HKDF-SHA-256. The supplied salt must be the exact same
     * 32-byte value at both peers. For the two-salt DH profile, pass the result of
     * {@link #combineSalts(byte[], byte[])}.
     *
     * @param ownSecretKey local DH private key
     * @param otherPublicKey remote peer's DH public key
     * @param aesKeyLength negotiated output AES key length (128 or 256 bits)
     * @param salt common 32-byte HKDF salt used by both peers
     * @return derived AES key, 16 bytes for AES-128 or 32 bytes for AES-256
     * @throws IllegalArgumentException if either key has the wrong type, the AES length is null, or
     *         the salt is not exactly 32 bytes
     * @throws InvalidKeyException if key parameters differ, are unsupported, or the remote public
     *         value is outside the permitted range or the approved subgroup
     * @throws NoSuchAlgorithmException if DH, HKDF-SHA-256, or SHA-256 is unavailable
     * @throws NoSuchProviderException if the Bouncy Castle provider is unavailable
     */
    public static byte[] getSharedSecretKeyHKDFSHA256(PrivateKey ownSecretKey, PublicKey otherPublicKey,
            AESKeyLength aesKeyLength, byte[] salt)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (!(ownSecretKey instanceof DHPrivateKey privateKey)) {
            throw new IllegalArgumentException("The local private key must be a DH private key.");
        }
        if (!(otherPublicKey instanceof DHPublicKey publicKey)) {
            throw new IllegalArgumentException("The remote public key must be a DH public key.");
        }
        if (aesKeyLength == null) {
            throw new IllegalArgumentException("AES key length must not be null.");
        }
        validateSalt(salt, "HKDF salt");

        DHParameterSpec localParameters = privateKey.getParams();
        DHParameterSpec remoteParameters = publicKey.getParams();
        if (localParameters == null || remoteParameters == null
                || remoteParameters.getP() == null || remoteParameters.getG() == null) {
            throw new InvalidKeyException("DH key parameters must not be null.");
        }
        try {
            validateParameters(localParameters);
        } catch (IllegalArgumentException exception) {
            throw new InvalidKeyException("The local DH key uses unsupported domain parameters.", exception);
        }
        if (!localParameters.getP().equals(remoteParameters.getP())
                || !localParameters.getG().equals(remoteParameters.getG())) {
            throw new InvalidKeyException("The DH public key uses different domain parameters.");
        }

        BigInteger p = localParameters.getP();
        BigInteger q = null;
        for (DHParameters group : supportedGroups()) {
            if (group.getP().equals(p) && group.getG().equals(localParameters.getG())) {
                q = group.getQ();
                break;
            }
        }
        BigInteger y = publicKey.getY();
        if (y == null || y.compareTo(BigInteger.TWO) < 0 || y.compareTo(p.subtract(BigInteger.TWO)) > 0) {
            throw new InvalidKeyException("The remote DH public value is outside the permitted range.");
        }
        if (q == null || !y.modPow(q, p).equals(BigInteger.ONE)) {
            throw new InvalidKeyException("The remote DH public value is not in the approved FFDHE subgroup.");
        }

        KeyAgreement agreement = KeyAgreement.getInstance("DH", BouncyCastleProvider.PROVIDER_NAME);
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

    /**
     * Checks that a DH specification uses an approved RFC 7919 group and a private exponent length
     * accepted for that group (256 bits for FFDHE-2048/3072 and at least 304 bits for FFDHE-4096).
     *
     * @param parameters DH domain parameters to validate
     * @throws IllegalArgumentException if the specification is null, the group is not supported,
     *         or a nonzero private exponent length is below the minimum required for that group
     */
    private static void validateParameters(DHParameterSpec parameters) {
        if (parameters == null || parameters.getP() == null || parameters.getG() == null) {
            throw new IllegalArgumentException("DH parameters p and g must not be null.");
        }
        boolean approved = false;
        for (DHParameters group : supportedGroups()) {
            if (group.getP().equals(parameters.getP()) && group.getG().equals(parameters.getG())) {
                approved = true;
                break;
            }
        }
        if (!approved) {
            throw new IllegalArgumentException("Only RFC 7919 FFDHE-2048/3072/4096 groups are accepted.");
        }
        int minimumExponentLength = parameters.getP().bitLength() == 4096
                ? PRIVATE_EXPONENT_LENGTH_4096 : PRIVATE_EXPONENT_LENGTH_2048_3072;
        if (parameters.getL() != 0 && parameters.getL() < minimumExponentLength) {
            throw new IllegalArgumentException("DH private exponent length must be at least "
                    + minimumExponentLength + " bits for this group.");
        }
    }

    /**
     * Enforces the fixed salt size used by the SEDAP-Express DH profile.
     *
     * @param salt salt bytes to validate
     * @param label human-readable name included in the exception message
     * @throws IllegalArgumentException if the salt is {@code null} or not exactly 32 bytes
     */
    private static void validateSalt(byte[] salt, String label) {
        if (salt == null || salt.length != EncryptionUtils.SALT_LENGTH_BYTES) {
            throw new IllegalArgumentException(label + " must contain exactly "
                    + EncryptionUtils.SALT_LENGTH_BYTES + " bytes.");
        }
    }
}
