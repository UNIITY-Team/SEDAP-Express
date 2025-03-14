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
package de.bundeswehr.mese.sedapexpress.messages;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
import de.bundeswehr.mese.sedapexpress.messages.TEXT.TextType;

class TEXTTest {

    @Test
    final void testConstructorValues() {

	final TEXT text = new TEXT((short) 55, 641244434L, "8F3A", Classification.Secret, Acknowledgement.TRUE, "4389F10D", "ORKA", TextType.Chat, DataEncoding.NONE, "TESTTEST", "5533");

	Assertions.assertEquals((short) 55, text.getNumber());
	Assertions.assertEquals(641244434L, text.getTime());
	Assertions.assertEquals("8F3A", text.getSender());
	Assertions.assertEquals(Classification.Secret, text.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, text.getAcknowledgement());
	Assertions.assertEquals("4389F10D", text.getMAC());
	Assertions.assertEquals("ORKA", text.getRecipient());
	Assertions.assertEquals(TextType.Chat, text.getType());
	Assertions.assertEquals(DataEncoding.NONE, text.getEncoding());
	Assertions.assertEquals("TESTTEST", text.getTextContent());
	Assertions.assertEquals("5533", text.getReference());
    }

    @Test
    final void testConstructorString() {

	String message = "TEXT;D3;661D44D2;324E;S;TRUE;;;1;NONE;\"This is an alert!\"";

	TEXT text = new TEXT(message);

	Assertions.assertEquals((short) 0xD3, text.getNumber());
	Assertions.assertEquals(0x661D44D2L, text.getTime());
	Assertions.assertEquals("324E", text.getSender());
	Assertions.assertEquals(Classification.Secret, text.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, text.getAcknowledgement());
	Assertions.assertNull(text.getMAC());
	Assertions.assertNull(text.getRecipient());
	Assertions.assertEquals(TextType.Alert, text.getType());
	Assertions.assertEquals(DataEncoding.NONE, text.getEncoding());
	Assertions.assertEquals("\"This is an alert!\"", text.getTextContent());
	Assertions.assertNull(text.getReference());

	message = "TEXT;D4;661D458E;324E;C;TRUE;;;2;NONE;\"This is a warning!\"";

	text = new TEXT(message);

	Assertions.assertEquals((short) 0xD4, text.getNumber());
	Assertions.assertEquals(0x661D458EL, text.getTime());
	Assertions.assertEquals("324E", text.getSender());
	Assertions.assertEquals(Classification.Confidential, text.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, text.getAcknowledgement());
	Assertions.assertNull(text.getMAC());
	Assertions.assertNull(text.getRecipient());
	Assertions.assertEquals(TextType.Warning, text.getType());
	Assertions.assertEquals(DataEncoding.NONE, text.getEncoding());
	Assertions.assertEquals("\"This is a warning!\"", text.getTextContent());
	Assertions.assertNull(text.getReference());

	message = "TEXT;D5;661D6565;324E;R;;;;3;;\"This is a notice!\";1133";

	text = new TEXT(message);

	Assertions.assertEquals((short) 0xD5, text.getNumber());
	Assertions.assertEquals(0x661D6565L, text.getTime());
	Assertions.assertEquals("324E", text.getSender());
	Assertions.assertEquals(Classification.Restricted, text.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, text.getAcknowledgement());
	Assertions.assertNull(text.getMAC());
	Assertions.assertNull(text.getRecipient());
	Assertions.assertEquals(TextType.Notice, text.getType());
	Assertions.assertEquals(DataEncoding.NONE, text.getEncoding());
	Assertions.assertEquals("\"This is a notice!\"", text.getTextContent());
	Assertions.assertEquals("1133", text.getReference());

	message = "TEXT;D6;661D7032;324E;U;;;E4F1;4;BASE64;IlRoaXMgaXMgYSBjaGF0IG1lc3NhZ2UhIg==";

	text = new TEXT(message);

	Assertions.assertEquals((short) 0xD6, text.getNumber());
	Assertions.assertEquals(0x661D7032L, text.getTime());
	Assertions.assertEquals("324E", text.getSender());
	Assertions.assertEquals(Classification.Unclas, text.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, text.getAcknowledgement());
	Assertions.assertNull(text.getMAC());
	Assertions.assertEquals("E4F1", text.getRecipient());
	Assertions.assertEquals(TextType.Chat, text.getType());
	Assertions.assertEquals(DataEncoding.BASE64, text.getEncoding());
	Assertions.assertEquals("\"This is a chat message!\"", text.getTextContent());
	Assertions.assertNull(text.getReference());
    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("55", "1B351C87", "5BCD", "S", "TRUE", "4389F10D", "7D31", "3", "NONE", "10.0.0.1").iterator();

	final TEXT text = new TEXT(it);

	Assertions.assertEquals((short) 0x55, text.getNumber());
	Assertions.assertEquals(0x1B351C87L, text.getTime());
	Assertions.assertEquals("5BCD", text.getSender());
	Assertions.assertEquals(Classification.Secret, text.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, text.getAcknowledgement());
	Assertions.assertEquals("4389F10D", text.getMAC());
	Assertions.assertEquals(TEXT.TextType.Notice, text.getType());
	Assertions.assertEquals(DataEncoding.NONE, text.getEncoding());
	Assertions.assertEquals("10.0.0.1", text.getTextContent());
	Assertions.assertEquals("7D31", text.getRecipient());
	Assertions.assertNull(text.getReference());

    }
}
