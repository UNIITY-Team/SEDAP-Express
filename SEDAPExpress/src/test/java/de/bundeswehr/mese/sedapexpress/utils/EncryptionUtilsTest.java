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
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.mese.sedapexpress.utils.EncryptionUtils.AESKeyLength;

class EncryptionUtilsTest {

    @Test
    void testGeneratesKey() {

	byte[] key128 = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_128_BITS));

	Assertions.assertEquals(16, key128.length);
	double sum = 0;
	for (int i : key128)
	    sum += i;
	Assertions.assertNotEquals(0, sum);

	byte[] key256 = Base64.decode(EncryptionUtils.generatesKey(AESKeyLength.KEY_LENGTH_256_BTS));
	Assertions.assertEquals(32, key256.length);
	sum = 0;
	for (int i : key256)
	    sum += i;
	Assertions.assertNotEquals(0, sum);

    }

    @Test
    void testGenerateIV() {
	byte[] iv = EncryptionUtils.generateIV();
	Assertions.assertEquals(16, iv.length);
    }

    @Test
    void testGetIVStringFromByteArray() {

	byte[] refArray = new byte[] { (byte) 0xB4, 0x6A, 0x45, (byte) 0x83, (byte) 0x9E, (byte) 0xF3, 0x79, (byte) 0xC0 };

	String ivString = EncryptionUtils.getIVStringFromByteArray(refArray);

	Assertions.assertEquals("B46A45839EF379C0", ivString);
    }

    @Test
    void testGetIVByteArrayFromString() {

	byte[] refArray = new byte[] { (byte) 0xE2, 0x47, (byte) 0xD1, (byte) 0x8B, 0x29, 0x4B, 0x38, (byte) 0xAF };

	byte[] ivArray = EncryptionUtils.getIVByteArrayFromString("E247D18B294B38AF");

	Assertions.assertArrayEquals(refArray, ivArray);
    }

    @Test
    void testEnDecryptionCFB() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

	String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

	byte[] iv = EncryptionUtils.generateIV();

	for (AESKeyLength keyLength : AESKeyLength.values()) {
	    byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
	    String encryptedMessage = EncryptionUtils.encrypt_AES_CFB(message, key, iv);
	    String decryptedMessage = EncryptionUtils.decrypt_AES_CFB(encryptedMessage, key, iv);

	    Assertions.assertNotEquals(message, encryptedMessage);
	    Assertions.assertEquals(message, decryptedMessage);
	}

    }

    @Test
    void testEnDecryptionCTR() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

	String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

	byte[] iv = EncryptionUtils.generateIV();

	for (AESKeyLength keyLength : AESKeyLength.values()) {
	    byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
	    String encryptedMessage = EncryptionUtils.encrypt_AES_CTR(message, key, iv);
	    String decryptedMessage = EncryptionUtils.decrypt_AES_CTR(encryptedMessage, key, iv);

	    Assertions.assertNotEquals(message, encryptedMessage);
	    Assertions.assertEquals(message, decryptedMessage);
	}

    }

    @Test
    void testEnDecryptionECB() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

	String message = "CONTACT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Track Alpha;R;SFAPMF---------;221333201;FA550C;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;VGVzdFRyYWNr";

	for (AESKeyLength keyLength : AESKeyLength.values()) {
	    byte[] key = Base64.decode(EncryptionUtils.generatesKey(keyLength));
	    String encryptedMessage = EncryptionUtils.encrypt_AES_ECB(message, key);
	    String decryptedMessage = EncryptionUtils.decrypt_AES_ECB(encryptedMessage, key);

	    Assertions.assertNotEquals(message, encryptedMessage);
	    Assertions.assertEquals(message, decryptedMessage);
	}

    }

}
