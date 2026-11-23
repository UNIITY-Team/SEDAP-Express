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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;

class ECDHUtilsTest {

	@Test
	void testECDHKeyExchangeHKDFSHA256()
			throws InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {

		byte[] salt = new byte[32];
		EncryptionUtils.random.nextBytes(salt);

		for (AESKeyLength keyLengthSharedSecret : AESKeyLength.values()) {

			// Client Key (private / public)
			KeyPair clientKeyPair = ECDHUtils.generateKeyPair();

			// Server Key (private / public)
			KeyPair serverKeyPair = ECDHUtils.generateKeyPair();

			// Client
			byte[] secretClient = ECDHUtils.getSharedSecretKeyHKDFSHA256(clientKeyPair.getPrivate(), serverKeyPair.getPublic(), keyLengthSharedSecret, salt);

			// Server
			byte[] secretServer = ECDHUtils.getSharedSecretKeyHKDFSHA256(serverKeyPair.getPrivate(), clientKeyPair.getPublic(), keyLengthSharedSecret, salt);

			Assertions.assertEquals(keyLengthSharedSecret.getIntValue() / 8, secretClient.length,
					"The derived key must have the negotiated AES key length.");
			Assertions.assertArrayEquals(secretClient, secretServer);
		}
	}

	@Test
	void generateKeyPairCreatesUsableKeyPairs() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

		KeyPair first = ECDHUtils.generateKeyPair();
		KeyPair second = ECDHUtils.generateKeyPair();

		assertNotNull(first);
		assertNotNull(first.getPrivate());
		assertNotNull(first.getPublic());
		assertNotNull(second);
		assertNotNull(second.getPrivate());
		assertNotNull(second.getPublic());
		assertFalse(Arrays.equals(first.getPublic().getEncoded(), second.getPublic().getEncoded()), "Two independently generated key pairs should have different public keys");
	}

}
