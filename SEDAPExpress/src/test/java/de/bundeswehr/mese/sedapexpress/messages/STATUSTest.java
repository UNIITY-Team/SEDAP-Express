/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2025 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
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
package de.bundeswehr.mese.sedapexpress.messages;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.STATUS;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.CommandState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.OperationalState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.TechnicalState;

/**
 *
 * @author Volker Voß
 *
 */
class STATUSTest {

    @Test
    final void testConstructorValues() {

	final STATUS status = new STATUS((byte) 41, 7865454543L, "BB91", Classification.Confidential, Acknowledgement.TRUE, "93B37ACC", TechnicalState.Operational, OperationalState.Operational, Arrays.asList("MLG"), Arrays.asList(50.0),
		Arrays.asList("Tank1"), Arrays.asList(75.3), Arrays.asList("MainAkku"), Arrays.asList(10.8), 34, CommandState.Executed_successfully, "10.8.0.6", Arrays.asList("rtsp://10.8.0.6/stream1", "rtsp://10.8.0.6/stream2"),
		"This is a sample!");

	Assertions.assertEquals((byte) 41, status.getNumber());
	Assertions.assertEquals(7865454543L, status.getTime());
	Assertions.assertEquals("BB91", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, status.getAcknowledgement());
	Assertions.assertEquals("93B37ACC", status.getMAC());
	Assertions.assertEquals(TechnicalState.Operational, status.getTecState());
	Assertions.assertEquals(OperationalState.Operational, status.getOpsState());
	Assertions.assertEquals("MLG", status.getAmmunitionLevelNames().get(0));
	Assertions.assertEquals(50.0, status.getAmmunitionLevels().get(0));
	Assertions.assertEquals("Tank1", status.getFuelLevelNames().get(0));
	Assertions.assertEquals(75.3, status.getFuelLevels().get(0));
	Assertions.assertEquals("MainAkku", status.getBatterieLevelNames().get(0));
	Assertions.assertEquals(10.8, status.getBatterieLevels().get(0));
	Assertions.assertEquals(34, status.getCmdId());
	Assertions.assertEquals(CommandState.Executed_successfully, status.getCmdState());
	Assertions.assertEquals("10.8.0.6", status.getHostname());
	Assertions.assertArrayEquals(new String[] { "rtsp://10.8.0.6/stream1", "rtsp://10.8.0.6/stream2" }, status.getMediaUrls().toArray());
	Assertions.assertEquals("This is a sample!", status.getFreeText());

    }

    @Test
    final void testConstructorString() {

	String message = "STATUS;41;50505050;BB91;C;TRUE;93B37ACC;2;1;#20.3;#30.4;#40.5;;;MTAuOC4wLjY=;cnRzcDovLzEwLjguMC42L3N0cmVhbTE=;U2FtcGxlVGV4dCE=";

	STATUS status = new STATUS(message);

	Assertions.assertEquals((byte) 0x41, status.getNumber());
	Assertions.assertEquals(0x50505050L, status.getTime());
	Assertions.assertEquals("BB91", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, status.getAcknowledgement());
	Assertions.assertEquals("93B37ACC", status.getMAC());
	Assertions.assertEquals(TechnicalState.Degraded, status.getTecState());
	Assertions.assertEquals(OperationalState.Degraded, status.getOpsState());
	Assertions.assertEquals("", status.getAmmunitionLevelNames().get(0));
	Assertions.assertEquals(20.3, status.getAmmunitionLevels().get(0));
	Assertions.assertEquals("", status.getFuelLevelNames().get(0));
	Assertions.assertEquals(30.4, status.getFuelLevels().get(0));
	Assertions.assertEquals("", status.getBatterieLevelNames().get(0));
	Assertions.assertEquals(40.5, status.getBatterieLevels().get(0));
	Assertions.assertNull(status.getCmdId());
	Assertions.assertNull(status.getCmdState());
	Assertions.assertEquals("10.8.0.6", status.getHostname());
	Assertions.assertArrayEquals(new String[] { "rtsp://10.8.0.6/stream1" }, status.getMediaUrls().toArray());
	Assertions.assertEquals("SampleText!", status.getFreeText());

	message = "STATUS;15;66e2d520;LASSY;C;;;3;2;;;MainBattery#100;;;MTkyLjE2OC4xNjguMTA1;aHR0cDovLzE5Mi4xNjguMTY4LjEwNTo4MDgwL3N0cmVhbT90b3BpYz0vYXJndXMvYXIwMjM0X2Zyb250X2xlZnQvaW1hZ2VfcmF3";

	status = new STATUS(message);

	Assertions.assertEquals((byte) 0x15, status.getNumber());
	Assertions.assertEquals(0x66e2d520L, status.getTime());
	Assertions.assertEquals("LASSY", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, status.getAcknowledgement());
	Assertions.assertNull(status.getMAC());
	Assertions.assertEquals(TechnicalState.Operational, status.getTecState());
	Assertions.assertEquals(OperationalState.Operational, status.getOpsState());
	Assertions.assertNull(status.getAmmunitionLevelNames());
	Assertions.assertNull(status.getAmmunitionLevels());
	Assertions.assertNull(status.getFuelLevelNames());
	Assertions.assertNull(status.getFuelLevels());
	Assertions.assertEquals("MainBattery", status.getBatterieLevelNames().get(0));
	Assertions.assertEquals(100.0, status.getBatterieLevels().get(0));
	Assertions.assertNull(status.getCmdId());
	Assertions.assertNull(status.getCmdState());
	Assertions.assertEquals("192.168.168.105", status.getHostname());
	Assertions.assertArrayEquals(new String[] { "http://192.168.168.105:8080/stream?topic=/argus/ar0234_front_left/image_raw" }, status.getMediaUrls().toArray());
	Assertions.assertNull(status.getFreeText());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays
		.asList("41", "A0B0C0D0", "BB91", "C", "TRUE", "93B37ACC", "2", "1", "#20.3", "#30.4", "#40.5", "", "", "MTAuOC4wLjY=", "cnRzcDovLzEwLjguMC42L3N0cmVhbTE=#cnRzcDovLzEwLjguMC42L3N0cmVhbTI=", "U2FtcGxlVGV4dCE=").iterator();

	final STATUS status = new STATUS(it);

	Assertions.assertEquals((byte) 0x41, status.getNumber());
	Assertions.assertEquals(0xA0B0C0D0L, status.getTime());
	Assertions.assertEquals("BB91", status.getSender());
	Assertions.assertEquals(Classification.Confidential, status.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, status.getAcknowledgement());
	Assertions.assertEquals("93B37ACC", status.getMAC());
	Assertions.assertEquals(TechnicalState.Degraded, status.getTecState());
	Assertions.assertEquals(OperationalState.Degraded, status.getOpsState());
	Assertions.assertEquals("", status.getAmmunitionLevelNames().get(0));
	Assertions.assertEquals(20.3, status.getAmmunitionLevels().get(0));
	Assertions.assertEquals("", status.getFuelLevelNames().get(0));
	Assertions.assertEquals(30.4, status.getFuelLevels().get(0));
	Assertions.assertEquals("", status.getBatterieLevelNames().get(0));
	Assertions.assertEquals(40.5, status.getBatterieLevels().get(0));
	Assertions.assertEquals("10.8.0.6", status.getHostname());
	Assertions.assertArrayEquals(new String[] { "rtsp://10.8.0.6/stream1", "rtsp://10.8.0.6/stream2" }, status.getMediaUrls().toArray());
	Assertions.assertEquals("SampleText!", status.getFreeText());

    }
}
