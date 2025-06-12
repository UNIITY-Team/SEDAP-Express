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

/**
 *
 * @author Volker Voß
 *
 */
class ACKNOWLEDGETest {

    @Test
    final void testConstructorValues() {

	final ACKNOWLEDGE status = new ACKNOWLEDGE((byte) 41, 7865454543L, "BB91", Classification.Confidential, Acknowledgement.TRUE, "93B37ACC", "Drone1", MessageType.CONTACT, (short) 43);

	Assertions.assertEquals((byte) 41, status.getNumber());
	Assertions.assertEquals(7865454543L, status.getTime());
	Assertions.assertEquals("BB91", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, status.getAcknowledgement());
	Assertions.assertEquals("93B37ACC", status.getMAC());
	Assertions.assertEquals("Drone1", status.getRecipient());
	Assertions.assertEquals(MessageType.CONTACT, status.getTypeOfTheMessage());
	Assertions.assertEquals((short) 43, status.getNumberOfTheMessage());

    }

    @Test
    final void testConstructorString() {

	String message = "ACKNOWLEDGE;18;661D64C0;129E;R;;;LASSY;COMMAND;2B";

	ACKNOWLEDGE status = new ACKNOWLEDGE(message);

	Assertions.assertEquals((byte) 0x18, status.getNumber());
	Assertions.assertEquals(0x661D64C0L, status.getTime());
	Assertions.assertEquals("129E", status.getSender());
	Assertions.assertEquals(Classification.Restricted, status.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, status.getAcknowledgement());
	Assertions.assertNull(status.getMAC());
	Assertions.assertEquals("LASSY", status.getRecipient());
	Assertions.assertEquals(MessageType.COMMAND, status.getTypeOfTheMessage());
	Assertions.assertEquals((short) 0x2B, status.getNumberOfTheMessage());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("41", "A0B0C0D0", "BB91", "C", "TRUE", "93B37ACC", "LASSY", "COMMAND", "2B").iterator();

	final ACKNOWLEDGE status = new ACKNOWLEDGE(it);

	Assertions.assertEquals((byte) 0x41, status.getNumber());
	Assertions.assertEquals(0xA0B0C0D0L, status.getTime());
	Assertions.assertEquals("BB91", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, status.getAcknowledgement());
	Assertions.assertEquals("93B37ACC", status.getMAC());
	Assertions.assertEquals("LASSY", status.getRecipient());
	Assertions.assertEquals(MessageType.COMMAND, status.getTypeOfTheMessage());
	Assertions.assertEquals((short) 0x2B, status.getNumberOfTheMessage());

    }
}
