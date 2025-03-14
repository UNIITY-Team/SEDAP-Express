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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.bundeswehr.mese.sedapexpress.utils.EncryptionUtils.AESKeyLength;

public class ECDHUtils {

    static {
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Generates a key pair for the Elliptic-Curves-Diffie-Hellman-Merkle process
     *
     * @return Key pair
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

	KeyPairGenerator clientKeyPairGenerator = KeyPairGenerator.getInstance("X25519", BouncyCastleProvider.PROVIDER_NAME);
	clientKeyPairGenerator.initialize(new XDHParameterSpec(XDHParameterSpec.X25519));

	return clientKeyPairGenerator.generateKeyPair();
    }

    /**
     * Calculates the shared secret key
     *
     * @param ownSecretKey          The own secret key
     * @param otherPublicKey        The public key of the other side
     * @param keyLengthSharedSecret The desired bit length of the shared secret key
     * 
     * @return the shared secret key
     *
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] getSharedSecretKey(PrivateKey ownSecretKey, PublicKey otherPublicKey, AESKeyLength keyLengthSharedSecret) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {

	KeyAgreement serverKeyAgreement = KeyAgreement.getInstance("X25519", BouncyCastleProvider.PROVIDER_NAME);
	serverKeyAgreement.init(ownSecretKey);
	serverKeyAgreement.doPhase(otherPublicKey, true);

	return new SecretKeySpec(serverKeyAgreement.generateSecret(), 0, keyLengthSharedSecret.getIntValue() / 8, "PBKDF2WithHmacSHA256").getEncoded();
    }

}
