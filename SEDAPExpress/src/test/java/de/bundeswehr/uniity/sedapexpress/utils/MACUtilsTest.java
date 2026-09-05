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

import static org.junit.jupiter.api.Assertions.*;

import java.util.HexFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.bundeswehr.uniity.sedapexpress.messages.HEARTBEAT;
import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

class MACUtilsTest {

    // Public test fixtures, not operational keys. Tags independently generated using
    // OpenSSL 3.6.3 `openssl mac` (HMAC/SHA256, CMAC/AES-128-CBC, GMAC/AES-128-GCM).
    // Input is the ASCII wire string, without a newline, with the MAC field set to 0000.
    private static final byte[] KEY = HexFormat.of().parseHex("000102030405060708090A0B0C0D0E0F");
    private static final byte[] IV = HexFormat.of().parseHex("101112131415161718191A1B");
    private static final String WIRE = "HEARTBEAT;01;0195238E25AD;QA;U;;0000";
    private static final String HMAC = "E34BD91C437B0C84185951A3BF1C25CAB18F753E6E4E3F9FEF318DB9C36056B9";
    private static final String CMAC = "3E4A223CC756F6B4CF8115D74E2B6DEC";
    private static final String GMAC = "E85B5C88B05D6F9F087AA31ECE76347B";

    private HEARTBEAT message() {
        return new HEARTBEAT(WIRE);
    }

    @Test
    void hmacMatchesIndependentFullAndTruncatedVectors() {
        HEARTBEAT message = message();
        message.setMAC("FFFFFFFF");
        assertEquals(HMAC, MACUtils.calcHMAC(KEY, message));
        assertEquals("E34BD91C", MACUtils.calc32BitHMAC(KEY, message));
        assertEquals("E34BD91C", MACUtils.setMACFieldWith32BitHMAC(KEY, message).getMAC());
    }

    @Test
    void cmacMatchesIndependentFullAndTruncatedVectors() {
        HEARTBEAT message = message();
        message.setMAC("FFFFFFFF");
        assertEquals(CMAC, MACUtils.calcCMAC(KEY, message));
        assertEquals("3E4A223C", MACUtils.calc32BitCMAC(KEY, message));
        assertEquals("3E4A223C", MACUtils.setMACFieldWith32BitCMAC(KEY, message).getMAC());
    }

    @Test
    void gmacMatchesIndependentFullAndTruncatedVectors() {
        HEARTBEAT message = message();
        message.setMAC("FFFFFFFF");
        assertEquals(GMAC, MACUtils.calcGMAC(KEY, IV, message));
        assertEquals("E85B5C88", MACUtils.calc32BitGMAC(KEY, IV, message));
        assertEquals("E85B5C88", MACUtils.setMACFieldWith32BitGMAC(KEY, IV, message).getMAC());
    }

    @Test
    void icdCmacExampleUsesFirstFourRawTagBytes() {
        // ICD v1.4.6 section III.2 public example password, encoded as ASCII bytes.
        byte[] key = HexFormat.of().parseHex("65787072657373657870726573736578");
        OWNUNIT message = new OWNUNIT("OWNUNIT;5E;0195236D151A;66A3;R;;0000;53.32;8.11;0;5.5;21;22;;;FGS Bayern");
        assertEquals("BD194FE70149EC51D9A9B758C4AD55CD", MACUtils.calcCMAC(key, message));
        assertEquals("BD194FE7", MACUtils.calc32BitCMAC(key, message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "E34BD91C",
        "3E4A223CC756F6B4CF8115D74E2B6DEC",
        "E34BD91C437B0C84185951A3BF1C25CAB18F753E6E4E3F9FEF318DB9C36056B9"
    })
    void headerPreservesEveryDocumentedTagLength(String tag) {
        for (String spelling : new String[] { tag, tag.toLowerCase(Locale.ROOT) }) {
            HEARTBEAT message = new HEARTBEAT(WIRE.replace("0000", spelling));
            assertEquals(spelling, message.getMAC());
            assertEquals(message.getMAC(), new HEARTBEAT(message.toString()).getMAC());
        }
    }

    @Test
    void constructionPlaceholderAndOmittedMacRemainSupported() {
        assertEquals("0000", message().getMAC());
        assertNull(new HEARTBEAT(WIRE.replace("0000", "")).getMAC());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 4, 7, 9, 16, 31, 33, 63, 65 })
    void headerRejectsUndocumentedTagWidths(int length) {
        String tag = "A".repeat(length);
        assertFalse(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.MAC_MATCHER, tag));
        assertNull(new HEARTBEAT(WIRE.replace("0000", tag)).getMAC());
    }

    @Test
    void genericHexFieldsRetainTheirIndependentGrammar() {
        assertTrue(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, "A"));
        assertTrue(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, "1234"));
        assertTrue(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, "A".repeat(32)));
        assertFalse(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, "A".repeat(33)));
        assertFalse(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.MAC_MATCHER, "GGGGGGGG"));
    }
}
