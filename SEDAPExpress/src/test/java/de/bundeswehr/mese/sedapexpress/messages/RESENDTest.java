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
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.MessageType;

class RESENDTest {

    @Test
    final void testConstructorValues() {

	final RESEND resend = new RESEND((byte) 66, 641211134L, "4525", Classification.Confidential, Acknowledgement.TRUE, "4377F177", "RECI", MessageType.GRAPHIC, (short) 0x34);

	Assertions.assertEquals((byte) 66, resend.getNumber());
	Assertions.assertEquals(641211134L, resend.getTime());
	Assertions.assertEquals("4525", resend.getSender());
	Assertions.assertEquals(Classification.Confidential, resend.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, resend.getAcknowledgement());
	Assertions.assertEquals("4377F177", resend.getMAC());
	Assertions.assertEquals("RECI", resend.getRecipient());
	Assertions.assertEquals(MessageType.GRAPHIC, resend.getNameOfTheMissingMessage());
	Assertions.assertEquals((short) 0x34, resend.getNumberOfTheMissingMessage());
    }

    @Test
    final void testConstructorString() {

	String message = "RESEND;20;661D64C0;129E;R;;;FE2A;TEXT;5D";

	RESEND resend = new RESEND(message);

	Assertions.assertEquals((byte) 0x20, resend.getNumber());
	Assertions.assertEquals(0x661D64C0L, resend.getTime());
	Assertions.assertEquals("129E", resend.getSender());
	Assertions.assertEquals(Classification.Restricted, resend.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, resend.getAcknowledgement());
	Assertions.assertNull(resend.getMAC());
	Assertions.assertEquals("FE2A", resend.getRecipient());
	Assertions.assertEquals(MessageType.TEXT, resend.getNameOfTheMissingMessage());
	Assertions.assertEquals((short) 0x5D, resend.getNumberOfTheMissingMessage());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("78", "1135AA87", "2B65", "S", "TRUE", "6389F10D", "7D31", "COMMAND", "33").iterator();

	final RESEND resend = new RESEND(it);

	Assertions.assertEquals((byte) 0x78, resend.getNumber());
	Assertions.assertEquals(0x1135AA87L, resend.getTime());
	Assertions.assertEquals("2B65", resend.getSender());
	Assertions.assertEquals(Classification.Secret, resend.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, resend.getAcknowledgement());
	Assertions.assertEquals("6389F10D", resend.getMAC());
	Assertions.assertEquals("7D31", resend.getRecipient());
	Assertions.assertEquals(MessageType.COMMAND, resend.getNameOfTheMissingMessage());
	Assertions.assertEquals((short) 0x33, resend.getNumberOfTheMissingMessage());

    }
}
