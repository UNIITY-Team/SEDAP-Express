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

/**
 *
 * @author Volker Voß
 *
 */
class OWNUNITTest {

    @Test
    final void testConstructorValues() {

	final OWNUNIT ownunit = new OWNUNIT((byte) 11, 456465543L, "22AA", Classification.Unclas, Acknowledgement.TRUE, "4389F10D", 77.88d, -10.12d, 5577.0d, 33.44d, 55.66d, 1.1d, -2.2d, 3.3d, "Ownunit", "SFGPIB----H----".toCharArray());

	Assertions.assertEquals((byte) 11, ownunit.getNumber());
	Assertions.assertEquals(456465543L, ownunit.getTime());
	Assertions.assertEquals("22AA", ownunit.getSender());
	Assertions.assertEquals(Classification.Unclas, ownunit.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, ownunit.getAcknowledgement());
	Assertions.assertEquals("4389F10D", ownunit.getMAC());
	Assertions.assertEquals(77.88d, ownunit.getLatitude());
	Assertions.assertEquals(-10.12d, ownunit.getLongitude());
	Assertions.assertEquals(5577.0d, ownunit.getAltitude());
	Assertions.assertEquals(33.44d, ownunit.getSpeed());
	Assertions.assertEquals(55.66d, ownunit.getCourse());
	Assertions.assertEquals(1.1d, ownunit.getHeading());
	Assertions.assertEquals(-2.2d, ownunit.getRoll());
	Assertions.assertEquals(3.3d, ownunit.getPitch());
	Assertions.assertEquals("Ownunit", ownunit.getName());
	Assertions.assertArrayEquals("SFGPIB----H----".toCharArray(), ownunit.getSIDC());

    }

    @Test
    final void testConstructorString() {

	String message = "OWNUNIT;11;1B351C87;22AA;U;TRUE;4389F10D;77.88;-10.12;5577.0;33.44;55.66;1.1;-2.2;3.3;Ownunit;SFGPIB----H----";

	OWNUNIT ownunit = new OWNUNIT(message);

	Assertions.assertEquals((byte) 0x11, ownunit.getNumber());
	Assertions.assertEquals(0x1B351C87L, ownunit.getTime());
	Assertions.assertEquals("22AA", ownunit.getSender());
	Assertions.assertEquals(Classification.Unclas, ownunit.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, ownunit.getAcknowledgement());
	Assertions.assertEquals("4389F10D", ownunit.getMAC());
	Assertions.assertEquals(77.88d, ownunit.getLatitude());
	Assertions.assertEquals(-10.12d, ownunit.getLongitude());
	Assertions.assertEquals(5577.0d, ownunit.getAltitude());
	Assertions.assertEquals(33.44d, ownunit.getSpeed());
	Assertions.assertEquals(55.66d, ownunit.getCourse());
	Assertions.assertEquals(1.1d, ownunit.getHeading());
	Assertions.assertEquals(-2.2d, ownunit.getRoll());
	Assertions.assertEquals(3.3d, ownunit.getPitch());
	Assertions.assertEquals("Ownunit", ownunit.getName());
	Assertions.assertArrayEquals("SFGPIB----H----".toCharArray(), ownunit.getSIDC());

	message = "OWNUNIT;5E;661D4410;66A3;R;;;53.32;8.11;0;5.5;21;22;;;FGS Bayern;SFSPFCLFF------";

	ownunit = new OWNUNIT(message);

	Assertions.assertEquals((byte) 0x5E, ownunit.getNumber());
	Assertions.assertEquals(0x661D4410L, ownunit.getTime());
	Assertions.assertEquals("66A3", ownunit.getSender());
	Assertions.assertEquals(Classification.Restricted, ownunit.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, ownunit.getAcknowledgement());
	Assertions.assertEquals(53.32d, ownunit.getLatitude());
	Assertions.assertEquals(8.11d, ownunit.getLongitude());
	Assertions.assertEquals(0d, ownunit.getAltitude());
	Assertions.assertEquals(5.5d, ownunit.getSpeed());
	Assertions.assertEquals(21d, ownunit.getCourse());
	Assertions.assertEquals(22d, ownunit.getHeading());
	Assertions.assertEquals("FGS Bayern", ownunit.getName());
	Assertions.assertArrayEquals("SFSPFCLFF------".toCharArray(), ownunit.getSIDC());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("11", "1B351C87", "22AA", "U", "FALSE", // SEDAPExpressMessage.ACKNOWLEDGE_NO
		"4389F10D", "77.88", "-10.12", "5577.0", "33.44", "55.66", "1.1", "-2.2", "3.3", "Ownunit", "SFGPIB----H----").iterator();

	OWNUNIT ownunit = new OWNUNIT(it);

	Assertions.assertEquals((byte) 0x11, ownunit.getNumber());
	Assertions.assertEquals(0x1B351C87L, ownunit.getTime());
	Assertions.assertEquals("22AA", ownunit.getSender());
	Assertions.assertEquals(Classification.Unclas, ownunit.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, ownunit.getAcknowledgement());
	Assertions.assertEquals("4389F10D", ownunit.getMAC());
	Assertions.assertEquals(77.88d, ownunit.getLatitude());
	Assertions.assertEquals(-10.12d, ownunit.getLongitude());
	Assertions.assertEquals(5577.0d, ownunit.getAltitude());
	Assertions.assertEquals(33.44d, ownunit.getSpeed());
	Assertions.assertEquals(55.66d, ownunit.getCourse());
	Assertions.assertEquals(1.1d, ownunit.getHeading());
	Assertions.assertEquals(-2.2d, ownunit.getRoll());
	Assertions.assertEquals(3.3d, ownunit.getPitch());
	Assertions.assertEquals("Ownunit", ownunit.getName());
	Assertions.assertArrayEquals("SFGPIB----H----".toCharArray(), ownunit.getSIDC());
    }
}
