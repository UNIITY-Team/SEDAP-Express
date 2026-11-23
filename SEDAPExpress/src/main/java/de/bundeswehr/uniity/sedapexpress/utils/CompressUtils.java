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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.bouncycastle.util.encoders.Base64;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * Provides raw-DEFLATE compression and decompression for SEDAP-Express
 * messages. Each operation uses its own codec instance, allowing concurrent
 * use without a global compression lock. Decompression is limited to
 * {@link #MAX_DECOMPRESSED_SIZE} bytes; invalid or oversized input is rejected.
 *
 * @author Volker Voß
 */
public class CompressUtils {

	/** Maximum uncompressed payload accepted by {@link #decompressMessage(String)} (32 MiB). */
	public static final int MAX_DECOMPRESSED_SIZE = 32 * 1024 * 1024;

	/**
	 * @deprecated Legacy shared compressor instance retained for source compatibility. Compression
	 *             methods now use independent instances and do not access this field.
	 */
	@Deprecated(forRemoval = false)
	public static final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);

	/**
	 * @deprecated Legacy shared inflater instance retained for source compatibility. Decompression
	 *             methods now use independent instances and do not access this field.
	 */
	@Deprecated(forRemoval = false)
	public static final Inflater inflater = new Inflater(true);

	/**
	 * Compresses a SEDAP-Express message to a Base64-encoded raw DEFLATE payload.
	 * A new compressor is used for every call, so concurrent calls do not serialize on
	 * shared compression state.
	 *
	 * @param message SEDAP-Express message to compress
	 * @return compressed message encoded as Base64, or {@code null} if compression fails
	 * @throws NullPointerException if {@code message} is {@code null}
	 */
	public static String compressMessage(SEDAPExpressMessage message) {
		byte[] input = message.toString().getBytes(StandardCharsets.ISO_8859_1);
		try (Deflater localDeflater = new Deflater(Deflater.BEST_COMPRESSION, true)) {

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try (DeflaterOutputStream stream = new DeflaterOutputStream(output, localDeflater)) {
				stream.write(input);
			}
			return Base64.toBase64String(output.toByteArray());
		} catch (IOException exception) {
			return null;
		}
	}

	/**
	 * Decompresses a Base64-encoded raw DEFLATE payload into a SEDAP-Express message.
	 * Decompression is limited to {@value #MAX_DECOMPRESSED_SIZE} bytes to prevent
	 * unbounded memory consumption from compressed input. Invalid Base64, invalid
	 * compressed data, oversized output, and unparsable messages return {@code null}.
	 *
	 * @param compressedMessage Base64-encoded compressed message
	 * @return decompressed message, or {@code null} if the input is invalid or exceeds the size limit
	 */
	public static SEDAPExpressMessage decompressMessage(final String compressedMessage) {
		if (compressedMessage == null) {
			return null;
		}
		try (Inflater localInflater = new Inflater(true)) {

			byte[] compressed = Base64.decode(compressedMessage);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try (InflaterInputStream stream = new InflaterInputStream(new ByteArrayInputStream(compressed), localInflater)) {
				byte[] buffer = new byte[8192];
				int total = 0;
				int read;
				while ((read = stream.read(buffer)) != -1) {
					if (read > MAX_DECOMPRESSED_SIZE - total) {
						return null;
					}
					total += read;
					output.write(buffer, 0, read);
				}
			}
			if (!localInflater.finished()) {
				return null;
			}
			return SEDAPExpressMessage.deserialize(output.toString(StandardCharsets.ISO_8859_1));
		} catch (IOException | RuntimeException exception) {
			return null;
		}
	}

}
