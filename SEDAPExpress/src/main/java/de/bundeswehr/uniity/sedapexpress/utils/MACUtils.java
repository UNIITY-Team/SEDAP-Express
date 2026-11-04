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
 * FITNESS FOR A PARTICULAR PURPOSEnARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.utils;

import java.nio.charset.StandardCharsets;
import java.security.Security;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * Some utils for generating and checking message authentication codes
 * 
 * @author Volker Voß
 *
 */
public class MACUtils {

	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	/**
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set HMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithHMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcHMAC(key, message));
		return message;

	}

	/**
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set CMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithCMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcCMAC(key, message));
		return message;

	}

	/**
	 *
	 * @param key        Key for creating the MAC
	 * @param initVector IV for creating the MAC
	 * @param message    SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set GMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWithGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcGMAC(key, initVector, message));
		return message;
	}

	/**
	 *
	 * @param key
	 * @param message
	 *
	 * @return the HMAC of the message
	 */
	/**
	 * Serializes a message for MAC calculation while excluding its MAC field. The
	 * original MAC is restored before this method returns.
	 *
	 * @param message SEDAP-Express message
	 * @return serialized message content without the MAC value
	 */
	private static byte[] getMessageContentWithoutMAC(final SEDAPExpressMessage message) {

		final String originalMAC = message.getMAC();
		try {
			message.setMAC(null);
			return message.toString().getBytes(StandardCharsets.ISO_8859_1);
		} finally {
			message.setMAC(originalMAC);
		}
	}

	private static String calcHMAC(final byte[] key, final SEDAPExpressMessage message) {

		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);

		HMac hmac = new HMac(new SHA256Digest());
		hmac.init(new KeyParameter(key));
		hmac.update(content, 0, content.length);
		final byte[] generatedHMac = new byte[hmac.getMacSize()];
		hmac.doFinal(generatedHMac, 0);
		return SEDAPExpressMessage.HexFormater.formatHex(generatedHMac);

	}

	/**
	 *
	 * @param key
	 * @param message
	 *
	 * @return the CMAC of the messaga
	 */
	private static String calcCMAC(final byte[] key, final SEDAPExpressMessage message) {

		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);

		final CipherParameters cipherParameters = new KeyParameter(key);
		final CMac cmac = new CMac(new AESLightEngine(), 32);
		cmac.init(cipherParameters);
		cmac.update(content, 0, content.length);
		final byte[] generatedCMac = new byte[cmac.getMacSize()];
		cmac.doFinal(generatedCMac, 0);

		return SEDAPExpressMessage.HexFormater.formatHex(generatedCMac);
	}

	/**
	 *
	 * @param key
	 * @param initVector
	 * @param message
	 *
	 * @return the GMAC of the message
	 */
	private static String calcGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		final byte[] content = MACUtils.getMessageContentWithoutMAC(message);

		final CipherParameters cipherParameters = new KeyParameter(key);
		final ParametersWithIV cipherParametersWithInitVector = new ParametersWithIV(cipherParameters, initVector);

		final GMac gmac = new GMac(GCMBlockCipher.newInstance(new AESLightEngine()), 32);
		gmac.init(cipherParametersWithInitVector);
		gmac.update(content, 0, content.length);
		final byte[] generatedGMac = new byte[gmac.getMacSize()];
		gmac.doFinal(generatedGMac, 0);

		return SEDAPExpressMessage.HexFormater.formatHex(generatedGMac);
	}

	/**
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit HMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitHMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcHMAC(key, message).substring(0, 8));
		return message;

	}

	/**
	 *
	 * @param key     Key for creating the MAC
	 * @param message SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit CMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitCMAC(final byte[] key, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcCMAC(key, message).substring(0, 8));
		return message;

	}

	/**
	 *
	 * @param key        Key for creating the MAC
	 * @param initVector IV for creating the MAC
	 * @param message    SEDAPExpress message
	 *
	 * @return SEDAPExpress message with set 32bit GMAC field
	 */
	public static SEDAPExpressMessage setMACFieldWith32BitGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		message.setMAC(MACUtils.calcGMAC(key, initVector, message).substring(0, 8));
		return message;
	}

	/**
	 * Checks if a message is authentic.
	 *
	 * @param key     Shared secret key
	 * @param message Original message
	 *
	 * @return Status if the message is authentic. True means yes, false means not.
	 */
	public static boolean isMessageAuthentic(final byte[] key, final SEDAPExpressMessage message) {

		return isMessageAuthentic(key, null, message);

	}

	/**
	 * Checks if a message is authentic.
	 * 
	 * @param key        En-/Decryption key
	 * @param initVector Initialisation vector (sometimes called IV), could be null
	 * @param message    Message to prove
	 *
	 * @return Status if the message is authentic. True means yes, false means not.
	 */
	public static boolean isMessageAuthentic(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

		final String receivedMAC = message.getMAC();
		if (receivedMAC == null) {
			return false;
		}

		// 32Bit MAC
		if (receivedMAC.length() == 8) {
			if (initVector == null) {
				return MACUtils.calcHMAC(key, message).subSequence(0, 8).equals(receivedMAC) || MACUtils.calcCMAC(key, message).subSequence(0, 8).equals(receivedMAC);
			} else {
				return MACUtils.calcGMAC(key, initVector, message).subSequence(0, 8).equals(receivedMAC);
			}

		} else { // 128Bit or 256Bit MAC
			if (initVector == null) {
				return MACUtils.calcHMAC(key, message).equals(receivedMAC) || MACUtils.calcCMAC(key, message).equals(receivedMAC);
			} else {
				return MACUtils.calcGMAC(key, initVector, message).equals(receivedMAC);
			}
		}

	}

	public static void main(String[] args) {
		OWNUNIT msg1 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;;53.32;8.11;0;5.5;21;22;;;FGS Bayern");
		OWNUNIT msg2 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;DDA6B526;53.32;8.11;0;5.5;21;22;;;FGS Bayern");
		OWNUNIT msg3 = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;DAA6B526;53.32;8.11;0;5.5;21;22;;;FGS Bayern");

		setMACFieldWith32BitCMAC("expressexpressex".getBytes(), msg1);
		System.out.println(msg1);

		System.out.println(isMessageAuthentic("expressexpressex".getBytes(), msg2));
		System.out.println(isMessageAuthentic("expressexpressex".getBytes(), msg3));
	}

}
