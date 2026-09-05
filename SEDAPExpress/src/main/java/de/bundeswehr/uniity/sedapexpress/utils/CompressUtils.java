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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.bouncycastle.util.encoders.Base64;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * 
 * @author Volker Voß
 *
 */
public class CompressUtils {

    public static final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
    public static final Inflater inflater = new Inflater(true);

    /**
     * Compresses a SEDAPExpressMessage to a byte array.
     * 
     * @param message SEDAPExpressMessage to compress
     * @return Compressed message as byte array
     */
    public static String compressMessage(SEDAPExpressMessage message) {

	synchronized (CompressUtils.deflater) {
	    CompressUtils.deflater.reset();
	    try {
		return Base64.toBase64String(new DeflaterInputStream(new ByteArrayInputStream(message.toString().getBytes(StandardCharsets.ISO_8859_1)), CompressUtils.deflater).readAllBytes());
	    } catch (final IOException e) {
		return null;
	    }
	}
    }

    /**
     * Decompresses a byte array to a SEDAPExpressMessage
     *
     * @param compressedMessage Compressed message
     * @return Decompressed message
     */
    public static SEDAPExpressMessage decompressMessage(final String compressedMessage) {

	synchronized (CompressUtils.inflater) {
	    CompressUtils.inflater.reset();
	    try {
		return SEDAPExpressMessage.deserialize(new String(new InflaterInputStream(new ByteArrayInputStream(Base64.decode(compressedMessage)), CompressUtils.inflater).readAllBytes(), StandardCharsets.ISO_8859_1));
	    } catch (final IOException e) {
		return null;
	    }
	}
    }

}
