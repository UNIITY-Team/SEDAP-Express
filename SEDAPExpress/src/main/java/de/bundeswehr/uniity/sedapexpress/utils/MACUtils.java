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
package de.bundeswehr.uniity.sedapexpress.utils;

import java.security.Security;
import java.util.HexFormat;
import java.util.zip.Adler32;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESLightEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

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

    public static HexFormat hexFormatter = HexFormat.of().withUpperCase();

    /**
     *
     * @param key     Key for creating the MAC
     * @param message SEDAPExpress message
     *
     * @return SEDAPExpress message with set HMAC field
     */
    public static SEDAPExpressMessage setMACFieldWith32BitHMAC(final byte[] key, final SEDAPExpressMessage message) {

	message.setMAC(MACUtils.calc32BitHMAC(key, message));
	return message;

    }

    /**
     *
     * @param key     Key for creating the MAC
     * @param message SEDAPExpress message
     *
     * @return SEDAPExpress message with set HMAC field
     */
    public static SEDAPExpressMessage setMACFieldWith32BitCMAC(final byte[] key, final SEDAPExpressMessage message) {

	message.setMAC(MACUtils.calc32BitCMAC(key, message));
	return message;

    }

    /**
     *
     * @param key        Key for creating the MAC
     * @param initVector IV for creating the MAC
     * @param message    SEDAPExpress message
     *
     * @return SEDAPExpress message with set HMAC field
     */
    public static SEDAPExpressMessage setMACFieldWith32BitGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

	message.setMAC(MACUtils.calc32BitGMAC(key, initVector, message));
	return message;
    }

    public static String calcAdler32Checksum(String str) {
	Adler32 adler = new Adler32();
	adler.update(str.getBytes());
	return MACUtils.hexFormatter.toHexDigits(adler.getValue(), 8);
    }

    public static String calc32BitHMAC(final byte[] key, final SEDAPExpressMessage message) {

	return MACUtils.calcAdler32Checksum(MACUtils.calcHMAC(key, message));
    }

    /**
     *
     * @param key
     * @param message
     *
     * @return the HMAC of the message
     */
    public static String calcHMAC(final byte[] key, final SEDAPExpressMessage message) {

	message.setMAC("0000");

	final byte[] content = message.toString().getBytes();

	HMac hmac = new HMac(new SHA256Digest());
	hmac.init(new KeyParameter(key));
	hmac.update(content, 0, content.length);
	final byte[] generatedHMac = new byte[hmac.getMacSize()];
	hmac.doFinal(generatedHMac, 0);
	return MACUtils.hexFormatter.formatHex(generatedHMac);

    }

    public static String calc32BitCMAC(final byte[] key, final SEDAPExpressMessage message) {

	return MACUtils.calcAdler32Checksum(MACUtils.calcCMAC(key, message));
    }

    /**
     *
     * @param key
     * @param message
     *
     * @return the CMAC of the messaga
     */
    public static String calcCMAC(final byte[] key, final SEDAPExpressMessage message) {

	message.setMAC("0000");

	final byte[] content = message.toString().getBytes();

	final CipherParameters cipherParameters = new KeyParameter(key);
	final CMac cmac = new CMac(new AESLightEngine(), 32);
	cmac.init(cipherParameters);
	cmac.update(content, 0, content.length);
	final byte[] generatedCMac = new byte[cmac.getMacSize()];
	cmac.doFinal(generatedCMac, 0);

	return MACUtils.hexFormatter.formatHex(generatedCMac);
    }

    public static String calc32BitGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

	return MACUtils.calcAdler32Checksum(MACUtils.calcGMAC(key, initVector, message));
    }

    /**
     *
     * @param key
     * @param initVector
     * @param message
     *
     * @return the GMAC of the message
     */
    public static String calcGMAC(final byte[] key, final byte[] initVector, final SEDAPExpressMessage message) {

	message.setMAC("0000");

	final byte[] content = message.toString().getBytes();

	final CipherParameters cipherParameters = new KeyParameter(key);
	final ParametersWithIV cipherParametersWithInitVector = new ParametersWithIV(cipherParameters, initVector);

	final GMac gmac = new GMac(GCMBlockCipher.newInstance(new AESLightEngine()), 32);
	gmac.init(cipherParametersWithInitVector);
	gmac.update(content, 0, content.length);
	final byte[] generatedGMac = new byte[gmac.getMacSize()];
	gmac.doFinal(generatedGMac, 0);

	return MACUtils.hexFormatter.formatHex(generatedGMac);
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

	return MACUtils.calcHMAC(key, message).equals(message.getMAC()) ||
		MACUtils.calcCMAC(key, message).equals(message.getMAC()) ||

		MACUtils.calc32BitHMAC(key, message).equals(message.getMAC()) ||
		MACUtils.calc32BitCMAC(key, message).equals(message.getMAC());

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

	if (initVector == null) {

	    return MACUtils.calcHMAC(key, message).equals(message.getMAC()) ||
		    MACUtils.calcCMAC(key, message).equals(message.getMAC()) ||

		    MACUtils.calc32BitHMAC(key, message).equals(message.getMAC()) ||
		    MACUtils.calc32BitCMAC(key, message).equals(message.getMAC());
	} else {

	    return MACUtils.calcGMAC(key, initVector, message).equals(message.getMAC()) ||
		    MACUtils.calc32BitGMAC(key, initVector, message).equals(message.getMAC());
	}

    }

}
