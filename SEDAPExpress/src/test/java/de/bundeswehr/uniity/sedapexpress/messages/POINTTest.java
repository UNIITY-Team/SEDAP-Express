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
package de.bundeswehr.uniity.sedapexpress.messages;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DeleteFlag;

/**
 *
 * @author Volker Voß
 *
 */
class POINTTest {

    private final byte imageData[] = { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x48, (byte) 0x44,
	    (byte) 0x52, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x08, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x73, (byte) 0x7A, (byte) 0x7A,
	    (byte) 0xF4, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x70, (byte) 0x48, (byte) 0x59, (byte) 0x73, (byte) 0x00, (byte) 0x00, (byte) 0x2E, (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x2E, (byte) 0x23,
	    (byte) 0x01, (byte) 0x78, (byte) 0xA5, (byte) 0x3F, (byte) 0x76, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x70, (byte) 0x49, (byte) 0x44, (byte) 0x41, (byte) 0x54, (byte) 0x58, (byte) 0xC3, (byte) 0xED, (byte) 0x56,
	    (byte) 0xB1, (byte) 0x8A, (byte) 0xC2, (byte) 0x50, (byte) 0x10, (byte) 0x9C, (byte) 0xC8, (byte) 0xB5, (byte) 0x22, (byte) 0x36, (byte) 0x29, (byte) 0xC4, (byte) 0x2F, (byte) 0x50, (byte) 0x14, (byte) 0xC4, (byte) 0xDA,
	    (byte) 0x4A, (byte) 0x82, (byte) 0x82, (byte) 0xD8, (byte) 0x8A, (byte) 0xA5, (byte) 0xA0, (byte) 0xF8, (byte) 0x0B, (byte) 0xE6, (byte) 0x13, (byte) 0xFC, (byte) 0x00, (byte) 0x4B, (byte) 0x4B, (byte) 0x0B, (byte) 0xB1,
	    (byte) 0xB1, (byte) 0x11, (byte) 0x0B, (byte) 0x85, (byte) 0x20, (byte) 0x16, (byte) 0x29, (byte) 0x04, (byte) 0xA3, (byte) 0x08, (byte) 0x6A, (byte) 0xA9, (byte) 0x8D, (byte) 0x16, (byte) 0x41, (byte) 0x14, (byte) 0x04,
	    (byte) 0xB1, (byte) 0x4A, (byte) 0xF6, (byte) 0x8A, (byte) 0x2D, (byte) 0x0E, (byte) 0x4F, (byte) 0x39, (byte) 0x2E, (byte) 0xB9, (byte) 0x4B, (byte) 0xC2, (byte) 0x1D, (byte) 0x59, (byte) 0x08, (byte) 0xFB, (byte) 0x76,
	    (byte) 0xF7, (byte) 0x11, (byte) 0x86, (byte) 0x99, (byte) 0x1D, (byte) 0x78, (byte) 0x02, (byte) 0x11, (byte) 0x11, (byte) 0x3C, (byte) 0x8C, (byte) 0x00, (byte) 0x3C, (byte) 0x0E, (byte) 0x1F, (byte) 0x80, (byte) 0x0F,
	    (byte) 0xE0, (byte) 0x0F, (byte) 0x03, (byte) 0xE8, (byte) 0x74, (byte) 0x00, (byte) 0x41, (byte) 0x00, (byte) 0xB6, (byte) 0x5B, (byte) 0x0F, (byte) 0x00, (byte) 0x98, (byte) 0x26, (byte) 0xD0, (byte) 0xED, (byte) 0xF2,
	    (byte) 0x79, (byte) 0x36, (byte) 0xFB, (byte) 0x19, (byte) 0x05, (byte) 0x64, (byte) 0x27, (byte) 0x76, (byte) 0x3B, (byte) 0x22, (byte) 0x80, (byte) 0x48, (byte) 0x96, (byte) 0x89, (byte) 0x24, (byte) 0x89, (byte) 0xC8,
	    (byte) 0x30, (byte) 0xC8, (byte) 0x6E, (byte) 0xD8, (byte) 0x63, (byte) 0x40, (byte) 0xD3, (byte) 0x00, (byte) 0x51, (byte) 0x04, (byte) 0xF2, (byte) 0x79, (byte) 0x60, (byte) 0x3C, (byte) 0x06, (byte) 0xF6, (byte) 0x7B,
	    (byte) 0x97, (byte) 0x25, (byte) 0xE8, (byte) 0xF7, (byte) 0x81, (byte) 0x5A, (byte) 0x0D, (byte) 0x88, (byte) 0xC7, (byte) 0xB9, (byte) 0x9E, (byte) 0xCF, (byte) 0x5D, (byte) 0x94, (byte) 0xE0, (byte) 0x78, (byte) 0x64,
	    (byte) 0xFA, (byte) 0x07, (byte) 0x03, (byte) 0xAE, (byte) 0x4B, (byte) 0x25, (byte) 0xFE, (byte) 0x4C, (byte) 0xD3, (byte) 0x25, (byte) 0x09, (byte) 0x96, (byte) 0x4B, (byte) 0xCE, (byte) 0x89, (byte) 0x04, (byte) 0xE7,
	    (byte) 0x62, (byte) 0x11, (byte) 0xE8, (byte) 0xF5, (byte) 0x80, (byte) 0xC3, (byte) 0xC1, (byte) 0x25, (byte) 0x09, (byte) 0x86, (byte) 0x43, (byte) 0x40, (byte) 0x92, (byte) 0x80, (byte) 0x68, (byte) 0x94, (byte) 0xEB,
	    (byte) 0x64, (byte) 0xF2, (byte) 0x11, (byte) 0x98, (byte) 0xA3, (byte) 0x12, (byte) 0x9C, (byte) 0x4E, (byte) 0x4C, (byte) 0x7F, (byte) 0xBB, (byte) 0xFD, (byte) 0xD1, (byte) 0xBB, (byte) 0xDD, (byte) 0x88, (byte) 0x82,
	    (byte) 0x41, (byte) 0xA2, (byte) 0x7A, (byte) 0xDD, (byte) 0x96, (byte) 0x04, (byte) 0xD6, (byte) 0x00, (byte) 0x28, (byte) 0x0A, (byte) 0x03, (byte) 0x58, (byte) 0x2C, (byte) 0x1E, (byte) 0xFB, (byte) 0xB2, (byte) 0xCC,
	    (byte) 0x7D, (byte) 0x5D, (byte) 0xB7, (byte) 0x0C, (byte) 0xE0, (byte) 0xCD, (byte) 0x12, (byte) 0x5D, (byte) 0x8A, (byte) 0xC2, (byte) 0x39, (byte) 0x95, (byte) 0x7A, (byte) 0x3D, (byte) 0x5F, (byte) 0xAD, (byte) 0x80,
	    (byte) 0x6C, (byte) 0xD6, (byte) 0xD2, (byte) 0x2F, (byte) 0x85, (byte) 0x6F, (byte) 0xBF, (byte) 0x88, (byte) 0xAE, (byte) 0x57, (byte) 0x20, (byte) 0x14, (byte) 0x02, (byte) 0x2A, (byte) 0x15, (byte) 0xA0, (byte) 0x50,
	    (byte) 0x78, (byte) 0x9E, (byte) 0x55, (byte) 0xAB, (byte) 0x40, (byte) 0xA3, (byte) 0x01, (byte) 0x34, (byte) 0x9B, (byte) 0x0E, (byte) 0xED, (byte) 0x80, (byte) 0xAA, (byte) 0x32, (byte) 0xCD, (byte) 0xA3, (byte) 0xD1,
	    (byte) 0xF3, (byte) 0xCC, (byte) 0x30, (byte) 0x88, (byte) 0x32, (byte) 0x19, (byte) 0x9E, (byte) 0x5F, (byte) 0x2E, (byte) 0x0E, (byte) 0xD9, (byte) 0x70, (byte) 0x3A, (byte) 0xE5, (byte) 0x1C, (byte) 0x8B, (byte) 0xBD,
	    (byte) 0xF0, (byte) 0x52, (byte) 0x00, (byte) 0x28, (byte) 0x97, (byte) 0xF9, (byte) 0xBC, (byte) 0x5E, (byte) 0x3B, (byte) 0x60, (byte) 0xC3, (byte) 0xFB, (byte) 0x1D, (byte) 0x68, (byte) 0xB5, (byte) 0x80, (byte) 0x5C,
	    (byte) 0x0E, (byte) 0x88, (byte) 0x44, (byte) 0x5E, (byte) 0xDF, (byte) 0x49, (byte) 0xA7, (byte) 0x39, (byte) 0x4F, (byte) 0x26, (byte) 0x0E, (byte) 0x48, (byte) 0xA0, (byte) 0x69, (byte) 0xCF, (byte) 0xF6, (byte) 0xFB,
	    (byte) 0x1C, (byte) 0xE7, (byte) 0x33, (byte) 0xDF, (byte) 0x11, (byte) 0x45, (byte) 0xB6, (byte) 0xE6, (byte) 0xAF, (byte) 0x4A, (byte) 0xA0, (byte) 0xAA, (byte) 0x5F, (byte) 0x6F, (byte) 0x3F, (byte) 0x00, (byte) 0x84,
	    (byte) 0xC3, (byte) 0xBC, (byte) 0x88, (byte) 0xBA, (byte) 0x0E, (byte) 0x6C, (byte) 0x36, (byte) 0x0E, (byte) 0xB8, (byte) 0xC0, (byte) 0x7F, (byte) 0x92, (byte) 0xF9, (byte) 0x00, (byte) 0x7C, (byte) 0x00, (byte) 0xFF,
	    (byte) 0x15, (byte) 0xC0, (byte) 0x3B, (byte) 0x49, (byte) 0x28, (byte) 0x1C, (byte) 0xD9, (byte) 0x5A, (byte) 0x36, (byte) 0x42, (byte) 0x96, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x45,
	    (byte) 0x4E, (byte) 0x44, (byte) 0xAE, (byte) 0x42, (byte) 0x60, (byte) 0x82 };

    @Test
    final void testConstructorValues() {

	final POINT point = new POINT((byte) 66, 85435438782L, "59CE", Classification.Unclas, Acknowledgement.FALSE, "FFAA327B", "1000", DeleteFlag.FALSE, 43.21, -111.22, 10011.0, 1.0, 2.0, 3.0, 200.0, 275.0, 10.0, 20.0, 30.0, 33.0,
		22.0, 11.0, "Datum", "gfgpgpuud------".toCharArray(), this.imageData, "This is a datum");

	Assertions.assertEquals((byte) 66, point.getNumber());
	Assertions.assertEquals(85435438782L, point.getTime());
	Assertions.assertEquals("59CE", point.getSender());
	Assertions.assertEquals(Classification.Unclas, point.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, point.getAcknowledgement());
	Assertions.assertEquals("FFAA327B", point.getMAC());
	Assertions.assertEquals("1000", point.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, point.getDeleteFlag());
	Assertions.assertEquals(43.21, point.getLatitude());
	Assertions.assertEquals(-111.22d, point.getLongitude());
	Assertions.assertEquals(10011.0d, point.getAltitude());
	Assertions.assertEquals(1d, point.getRelativeXDistance());
	Assertions.assertEquals(2d, point.getRelativeYDistance());
	Assertions.assertEquals(3d, point.getRelativeZDistance());
	Assertions.assertEquals(200d, point.getSpeed());
	Assertions.assertEquals(275d, point.getCourse());
	Assertions.assertEquals(10d, point.getHeading());
	Assertions.assertEquals(20d, point.getRoll());
	Assertions.assertEquals(30d, point.getPitch());
	Assertions.assertEquals(33d, point.getWidth());
	Assertions.assertEquals(22d, point.getLength());
	Assertions.assertEquals(11d, point.getHeight());
	Assertions.assertEquals("Datum", point.getName());
	Assertions.assertArrayEquals("gfgpgpuud------".toCharArray(), point.getSIDC());
	Assertions.assertArrayEquals(this.imageData, point.getMultimediaData());
	Assertions.assertEquals("This is a datum", point.getComment());

	final POINT contact2 = new POINT((byte) 0x6A, 85435438782L, "59CE", Classification.Unclas, Acknowledgement.FALSE, "FFAA327B", "1000", DeleteFlag.FALSE, 43.21, -111.22, 10011.0, 1.0, 2.0, 3.0, 200.0, 275.0, 10.0, 20.0, 30.0,
		33.0,
		22.0, 11.0, "Person in water", "gfopep---------".toCharArray(), this.imageData, "This is person in water");

	Assertions.assertEquals((byte) 0x6A, contact2.getNumber());
	Assertions.assertEquals(85435438782L, contact2.getTime());
	Assertions.assertEquals("59CE", contact2.getSender());
	Assertions.assertEquals(Classification.Unclas, contact2.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, contact2.getAcknowledgement());
	Assertions.assertEquals("FFAA327B", contact2.getMAC());
	Assertions.assertEquals("1000", contact2.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, contact2.getDeleteFlag());
	Assertions.assertEquals(43.21, contact2.getLatitude());
	Assertions.assertEquals(-111.22d, contact2.getLongitude());
	Assertions.assertEquals(10011.0d, contact2.getAltitude());
	Assertions.assertEquals(1d, contact2.getRelativeXDistance());
	Assertions.assertEquals(2d, contact2.getRelativeYDistance());
	Assertions.assertEquals(3d, contact2.getRelativeZDistance());
	Assertions.assertEquals(200d, contact2.getSpeed());
	Assertions.assertEquals(275d, contact2.getCourse());
	Assertions.assertEquals(10d, contact2.getHeading());
	Assertions.assertEquals(20d, contact2.getRoll());
	Assertions.assertEquals(30d, contact2.getPitch());
	Assertions.assertEquals(33d, contact2.getWidth());
	Assertions.assertEquals(22d, contact2.getLength());
	Assertions.assertEquals(11d, contact2.getHeight());
	Assertions.assertEquals("Person in water", contact2.getName());
	Assertions.assertArrayEquals("gfopep---------".toCharArray(), contact2.getSIDC());
	Assertions.assertArrayEquals(this.imageData, contact2.getMultimediaData());
	Assertions.assertEquals("This is person in water", contact2.getComment());

    }

    @Test
    final void testConstructorString() {

	String message = "POINT;66;1B351C87;59CE;U;TRUE;FFAA327B;1000;;43.21;-111.22;10011.0;1.0;2.0;3.0;200.0;275.0;10.0;20.0;30.0;33.0;22.0;11.0;Rendezvous;gfgpgpoz-------;iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=;UmVuZGV6dm91cw==";

	POINT point = new POINT(message);

	Assertions.assertEquals((byte) 0x66, point.getNumber());
	Assertions.assertEquals(0x1B351C87L, point.getTime());
	Assertions.assertEquals("59CE", point.getSender());
	Assertions.assertEquals(Classification.Unclas, point.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, point.getAcknowledgement());
	Assertions.assertEquals("FFAA327B", point.getMAC());
	Assertions.assertEquals("1000", point.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, point.getDeleteFlag());
	Assertions.assertEquals(43.21, point.getLatitude());
	Assertions.assertEquals(-111.22d, point.getLongitude());
	Assertions.assertEquals(10011.0d, point.getAltitude());
	Assertions.assertEquals(1d, point.getRelativeXDistance());
	Assertions.assertEquals(2d, point.getRelativeYDistance());
	Assertions.assertEquals(3d, point.getRelativeZDistance());
	Assertions.assertEquals(200d, point.getSpeed());
	Assertions.assertEquals(275d, point.getCourse());
	Assertions.assertEquals(10d, point.getHeading());
	Assertions.assertEquals(20d, point.getRoll());
	Assertions.assertEquals(30d, point.getPitch());
	Assertions.assertEquals(33d, point.getWidth());
	Assertions.assertEquals(22d, point.getLength());
	Assertions.assertEquals(11d, point.getHeight());
	Assertions.assertEquals("Rendezvous", point.getName());
	Assertions.assertArrayEquals("gfgpgpoz-------".toCharArray(), point.getSIDC());
	Assertions.assertArrayEquals(this.imageData, point.getMultimediaData());
	Assertions.assertEquals("Rendezvous", point.getComment());

	message = "POINT;5E;661D4410;66A3;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;Target Alpha;gffppt---------;;VGFyZ2V0IHBvaW50";

	point = new POINT(message);

	Assertions.assertEquals((byte) 0x5E, point.getNumber());
	Assertions.assertEquals(0x661D4410L, point.getTime());
	Assertions.assertEquals("66A3", point.getSender());
	Assertions.assertEquals(Classification.Restricted, point.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, point.getAcknowledgement());
	Assertions.assertNull(point.getMAC());
	Assertions.assertEquals("100", point.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, point.getDeleteFlag());
	Assertions.assertEquals(53.32d, point.getLatitude());
	Assertions.assertEquals(8.11d, point.getLongitude());
	Assertions.assertEquals(0d, point.getAltitude());
	Assertions.assertNull(point.getRelativeXDistance());
	Assertions.assertNull(point.getRelativeYDistance());
	Assertions.assertNull(point.getRelativeZDistance());
	Assertions.assertEquals(120d, point.getSpeed());
	Assertions.assertEquals(275d, point.getCourse());
	Assertions.assertNull(point.getHeading());
	Assertions.assertNull(point.getRoll());
	Assertions.assertNull(point.getPitch());
	Assertions.assertNull(point.getWidth());
	Assertions.assertNull(point.getLength());
	Assertions.assertNull(point.getHeight());
	Assertions.assertEquals("Target Alpha", point.getName());
	Assertions.assertArrayEquals("gffppt---------".toCharArray(), point.getSIDC());
	Assertions.assertNull(point.getMultimediaData());
	Assertions.assertEquals("Target point", point.getComment());

	message = "POINT;5F;661D5420;83C5;U;;;101;FALSE;36.32;12.11;2000;;;;44;;;;;;;;Unknown;;;UG9zcyBOZXRoZXJsYW5kcw==";

	point = new POINT(message);

	Assertions.assertEquals((byte) 0x5f, point.getNumber());
	Assertions.assertEquals(0x661D5420L, point.getTime());
	Assertions.assertEquals("83C5", point.getSender());
	Assertions.assertEquals(Classification.Unclas, point.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, point.getAcknowledgement());
	Assertions.assertNull(point.getMAC());
	Assertions.assertEquals("101", point.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, point.getDeleteFlag());
	Assertions.assertEquals(36.32, point.getLatitude());
	Assertions.assertEquals(12.11d, point.getLongitude());
	Assertions.assertEquals(2000d, point.getAltitude());
	Assertions.assertNull(point.getRelativeXDistance());
	Assertions.assertNull(point.getRelativeYDistance());
	Assertions.assertNull(point.getRelativeZDistance());
	Assertions.assertEquals(44d, point.getSpeed());
	Assertions.assertNull(point.getCourse());
	Assertions.assertNull(point.getHeading());
	Assertions.assertNull(point.getRoll());
	Assertions.assertNull(point.getPitch());
	Assertions.assertNull(point.getWidth());
	Assertions.assertNull(point.getLength());
	Assertions.assertNull(point.getHeight());
	Assertions.assertEquals("Unknown", point.getName());
	Assertions.assertNull(point.getSIDC());
	Assertions.assertNull(point.getMultimediaData());
	Assertions.assertEquals("Poss Netherlands", point.getComment());

	message = "POINT;60;54742310;4371;S;TRUE;;102;TRUE;53.32;8.11";

	point = new POINT(message);

	Assertions.assertEquals((byte) 0x60, point.getNumber());
	Assertions.assertEquals(0x54742310L, point.getTime());
	Assertions.assertEquals("4371", point.getSender());
	Assertions.assertEquals(Classification.Secret, point.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, point.getAcknowledgement());
	Assertions.assertNull(point.getMAC());
	Assertions.assertEquals("102", point.getPointID());
	Assertions.assertEquals(DeleteFlag.TRUE, point.getDeleteFlag());
	Assertions.assertEquals(53.32, point.getLatitude());
	Assertions.assertEquals(8.11, point.getLongitude());
	Assertions.assertNull(point.getAltitude());
	Assertions.assertNull(point.getRelativeXDistance());
	Assertions.assertNull(point.getRelativeYDistance());
	Assertions.assertNull(point.getRelativeZDistance());
	Assertions.assertNull(point.getSpeed());
	Assertions.assertNull(point.getCourse());
	Assertions.assertNull(point.getHeading());
	Assertions.assertNull(point.getRoll());
	Assertions.assertNull(point.getPitch());
	Assertions.assertNull(point.getWidth());
	Assertions.assertNull(point.getLength());
	Assertions.assertNull(point.getHeight());
	Assertions.assertNull(point.getName());
	Assertions.assertNull(point.getSIDC());
	Assertions.assertNull(point.getMultimediaData());
	Assertions.assertNull(point.getComment());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("66", "1B351C87", "59CE", "U", "FALSE", // SEDAPExpressMessage.ACKNOWLEDGE_NO
		"FFAA327B", "1000", "FALSE", "43.21", "-111.22", "10011.0", "1.0", "2.0", "3.0", "200.0", "275.0", "10.0", "20.0", "30.0", "33.0", "22.0", "11.0", "Track Alpha", "sfapmf---------",
		"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=",
		"VGhpcyBpcyBhIHRlc3QgdHJhY2s=").iterator();

	POINT point = new POINT(it);

	Assertions.assertEquals((byte) 0x66, point.getNumber());
	Assertions.assertEquals(0x1B351C87L, point.getTime());
	Assertions.assertEquals("59CE", point.getSender());
	Assertions.assertEquals(Classification.Unclas, point.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, point.getAcknowledgement());
	Assertions.assertEquals("FFAA327B", point.getMAC());
	Assertions.assertEquals("1000", point.getPointID());
	Assertions.assertEquals(DeleteFlag.FALSE, point.getDeleteFlag());
	Assertions.assertEquals(43.21, point.getLatitude());
	Assertions.assertEquals(-111.22d, point.getLongitude());
	Assertions.assertEquals(10011.0d, point.getAltitude());
	Assertions.assertEquals(1d, point.getRelativeXDistance());
	Assertions.assertEquals(2d, point.getRelativeYDistance());
	Assertions.assertEquals(3d, point.getRelativeZDistance());
	Assertions.assertEquals(200d, point.getSpeed());
	Assertions.assertEquals(275d, point.getCourse());
	Assertions.assertEquals(10d, point.getHeading());
	Assertions.assertEquals(20d, point.getRoll());
	Assertions.assertEquals(30d, point.getPitch());
	Assertions.assertEquals(33d, point.getWidth());
	Assertions.assertEquals(22d, point.getLength());
	Assertions.assertEquals(11d, point.getHeight());
	Assertions.assertEquals("Track Alpha", point.getName());
	Assertions.assertArrayEquals("sfapmf---------".toCharArray(), point.getSIDC());
	Assertions.assertEquals(
		"iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAABcElEQVRYw+1WsYrCUBCcyLUiNinEL1AUxNpKgoLYiqWg+AvmE/wAS0sLsbERC4UgFikEowhqqY0WQRQEsUr2ii0OTzkuuUvCHVkI+3b3EYaZHXgCERE8jAA8Dh+AD+APA+h0AEEAtlsPAJgm0O3yeTb7GQVkJ3Y7IoBIlokkicgwyG7YY0DTAFEE8nlgPAb2e5cl6PeBWg2Ix7mez12U4Hhk+gcDrksl/kzTJQmWS86JBOdiEej1gMPBJQmGQ0CSgGiU62TyEZijEpxOTH+7/dG73YiCQaJ63ZYE1gAoCgNYLB77ssx9XbcM4M0SXYrCOZV6PV+tgGzW0i+Fb7+IrlcgFAIqFaBQeJ5Vq0CjATSbDu2AqjLNo9HzzDCIMhmeXy4O2XA65RyLvfBSACiX+bxeO2DD+x1otYBcDohEXt9JpzlPJg5IoGnP9vsc5zPfEUW25q9KoKpfbz8AhMO8iLoObDYOuMB/kvkAfAD/FcA7SSgc2Vo2QpYAAAAASUVORK5CYII=",
		org.bouncycastle.util.encoders.Base64.toBase64String(point.getMultimediaData()));
	Assertions.assertEquals("This is a test track", point.getComment());
    }

    @Test
    final void testSamplesFromDocu() {

	String[] messages = new String[] {
		"POINT;6A;0013E45956BE;59CE;U;;FFAA327B;1000;;;;;100;130;0;2;30;;;;;;;Person in water;gfopep---------",
		"POINT;5E;0000661D4410;66A3;R;;;100;;53.32;8.11;0;;;;120;275;;;;;;;Target Alpha;gffppt---------;;VGFyZ2V0IHBvaW50"
	};

	Arrays.asList(messages).forEach(msg -> Assertions.assertEquals(msg, new POINT(msg).toString()));

    }
}
