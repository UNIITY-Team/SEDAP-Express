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
package de.bundeswehr.mese.sedapexpress.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.mese.sedapexpress.messages.TEXT;

public class CompressUtilsText {

    @Test
    void testMessageCompression() {

	TEXT text = (TEXT) SEDAPExpressMessage.deserialize("TEXT;D3;0195238E15AD;324E;S;TRUE;;;1;NONE;\"This is an alert!\";1000");

	String compressedMessage = CompressUtils.compressMessage(text);

	Assertions.assertEquals("C3GNCLF2MbY2MLQ0NTK2cDU0dXSxNjYycbUOtg4JCnW1trY2tPbz93O1VgrJyCxWAKLEPIXEnNSiEkUla0MDAwMA", compressedMessage);

    }

    @Test
    void testMessageDecompression() {

	TEXT text = (TEXT) CompressUtils.decompressMessage("C3GNCLF2MbY2MLQ0NTK2cDU0dXSxNjYycbUOtg4JCnW1trY2tPbz93O1VgrJyCxWAKLEPIXEnNSiEkUla0MDAwMA");

	Assertions.assertEquals("TEXT;D3;0195238E15AD;324E;S;TRUE;;;1;NONE;\"This is an alert!\";1000", text.toString());
    }

}
