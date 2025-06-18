/*******************************************************************************
 * Copyright (C)2012-2024, German Federal Armed Forces - All rights reserved.
 *
 * MUKdo II
 * Wibbelhofstraße 3
 * 26384 Wilhelmshaven
 * Germany
 *
 * This source code is part of the MEDAS/SEDAP Project.
 * Person of contact (POC): Volker Voß, MUKdo II A, Wilhelmshaven
 *
 * Unauthorized use, modification, redistributing, copying, selling and
 * printing of this file in source and binary form including accompanying
 * materials is STRICTLY prohibited.
 *
 * This source code and it's parts is classified as OFFEN / NATO UNCLASSIFIED!
 *******************************************************************************/
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
		return Base64.toBase64String(new DeflaterInputStream(new ByteArrayInputStream(message.toString().getBytes(StandardCharsets.US_ASCII)), CompressUtils.deflater).readAllBytes());
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
		return SEDAPExpressMessage.deserialize(new String(new InflaterInputStream(new ByteArrayInputStream(Base64.decode(compressedMessage)), CompressUtils.inflater).readAllBytes(), StandardCharsets.US_ASCII));
	    } catch (final IOException e) {
		return null;
	    }
	}
    }

}
