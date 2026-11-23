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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.FrodoKEMParameterSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;

class FrodoKEMUtilsTest {

	@Test
	void testFrodoKEM976() throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

		byte[] salt = new byte[32];
		EncryptionUtils.random.nextBytes(salt);

		for (AESKeyLength keyLength : AESKeyLength.values()) {

			KeyPair pairServer = FrodoKEMUtils.generateKeyPair(FrodoKEMParameterSpec.frodokem976aes);

			SecretKeyWithEncapsulation encapsulation = FrodoKEMUtils.generateSharedSecretKeyWithEncapsulation(pairServer.getPublic(), keyLength, salt);

			byte[] secretClient = encapsulation.getEncoded();
			byte[] encapsulatedKey = encapsulation.getEncapsulation();

			byte[] secretServer = FrodoKEMUtils.generateSharedSecretKeyFromEncapsulation(pairServer.getPrivate(), encapsulatedKey, keyLength, salt);

			Assertions.assertEquals(keyLength.getIntValue() / 8, secretClient.length,
					"The derived key must have the negotiated AES key length.");
			Assertions.assertArrayEquals(secretClient, secretServer);
		}

	}

	@Test
	void testFrodoKEM1344() throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

		byte[] salt = new byte[32];
		EncryptionUtils.random.nextBytes(salt);

		for (AESKeyLength keyLength : AESKeyLength.values()) {

			KeyPair pairServer = FrodoKEMUtils.generateKeyPair(FrodoKEMParameterSpec.frodokem1344aes);

			SecretKeyWithEncapsulation encapsulation = FrodoKEMUtils.generateSharedSecretKeyWithEncapsulation(pairServer.getPublic(), keyLength, salt);

			byte[] secretClient = encapsulation.getEncoded();
			byte[] encapsulatedKey = encapsulation.getEncapsulation();

			byte[] secretServer = FrodoKEMUtils.generateSharedSecretKeyFromEncapsulation(pairServer.getPrivate(), encapsulatedKey, keyLength, salt);

			Assertions.assertEquals(keyLength.getIntValue() / 8, secretClient.length,
					"The derived key must have the negotiated AES key length.");
			Assertions.assertArrayEquals(secretClient, secretServer);
		}

	}
}
