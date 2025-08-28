/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2025 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
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
package de.bundeswehr.mese.sedapexpress.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HexFormat;

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.utils.EncryptionUtils.DHKEMKeyLength;
import de.bundeswehr.uniity.sedapexpress.utils.KyberUtils;

class KyberUtilsTest {

    @Test
    void testKyberKEM512() throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

	for (DHKEMKeyLength keyLength : DHKEMKeyLength.values()) {

	    KeyPair pairServer = KyberUtils.generateKeyPair(KyberParameterSpec.kyber512);

	    SecretKeyWithEncapsulation encapsulation = KyberUtils.generateSharedSecretKeyWithEncapsulation(pairServer.getPublic(), keyLength);

	    byte[] secretClient = encapsulation.getEncoded();
	    byte[] encapsulatedKey = encapsulation.getEncapsulation();

	    byte[] secretServer = KyberUtils.generateSharedSecretKeyFromEncapsulation(pairServer.getPrivate(), encapsulatedKey, keyLength);

	    Assertions.assertArrayEquals(secretClient, secretServer);

	    System.out.println("Shared secret client: " + HexFormat.of().withUpperCase().formatHex(secretClient));
	    System.out.println("Shared secret sever:  " + HexFormat.of().withUpperCase().formatHex(secretServer));
	}

    }

    @Test
    void testKyberKEM768() throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

	for (DHKEMKeyLength keyLength : DHKEMKeyLength.values()) {

	    KeyPair pairServer = KyberUtils.generateKeyPair(KyberParameterSpec.kyber768);

	    SecretKeyWithEncapsulation encapsulation = KyberUtils.generateSharedSecretKeyWithEncapsulation(pairServer.getPublic(), keyLength);

	    byte[] secretClient = encapsulation.getEncoded();
	    byte[] encapsulatedKey = encapsulation.getEncapsulation();

	    byte[] secretServer = KyberUtils.generateSharedSecretKeyFromEncapsulation(pairServer.getPrivate(), encapsulatedKey, keyLength);

	    Assertions.assertArrayEquals(secretClient, secretServer);

	    System.out.println("Shared secret client: " + HexFormat.of().withUpperCase().formatHex(secretClient));
	    System.out.println("Shared secret sever:  " + HexFormat.of().withUpperCase().formatHex(secretServer));
	}

    }

    @Test
    void testKyberKEM1024() throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

	for (DHKEMKeyLength keyLength : DHKEMKeyLength.values()) {

	    KeyPair pairServer = KyberUtils.generateKeyPair(KyberParameterSpec.kyber1024);

	    SecretKeyWithEncapsulation encapsulation = KyberUtils.generateSharedSecretKeyWithEncapsulation(pairServer.getPublic(), keyLength);

	    byte[] secretClient = encapsulation.getEncoded();
	    byte[] encapsulatedKey = encapsulation.getEncapsulation();

	    byte[] secretServer = KyberUtils.generateSharedSecretKeyFromEncapsulation(pairServer.getPrivate(), encapsulatedKey, keyLength);

	    Assertions.assertArrayEquals(secretClient, secretServer);

	    System.out.println("Shared secret client: " + HexFormat.of().withUpperCase().formatHex(secretClient));
	    System.out.println("Shared secret sever:  " + HexFormat.of().withUpperCase().formatHex(secretServer));
	}

    }
}
