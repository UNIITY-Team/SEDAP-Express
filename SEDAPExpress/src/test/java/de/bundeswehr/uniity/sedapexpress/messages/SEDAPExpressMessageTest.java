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
package de.bundeswehr.uniity.sedapexpress.messages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SEDAPExpressMessageTest {

	@Test
	final void testRemoveSemicolons() {

		Assertions.assertEquals("HEARTBEAT;42;661D5420;89AD;U;;;FE2A", SEDAPExpressMessage.removeSemicolons("HEARTBEAT;42;661D5420;89AD;U;;;FE2A"));
		Assertions.assertEquals("HEARTBEAT;;;89AD;U", SEDAPExpressMessage.removeSemicolons("HEARTBEAT;;;89AD;U;;;"));
		Assertions.assertEquals("HEARTBEAT;;661D5420", SEDAPExpressMessage.removeSemicolons("HEARTBEAT;;661D5420;;;;;"));
	}

	@Test
	void splitMessagePreservesInteriorAndTrailingEmptyFields() {
		Assertions.assertEquals(java.util.List.of("A", "", "C", ""), SEDAPExpressMessage.splitMessage("A;;C;"));
		Assertions.assertEquals(java.util.List.of(""), SEDAPExpressMessage.splitMessage(""));
	}

	@Test
	void hashSeparatedListsPreserveEmptyEntries() {
		Assertions.assertEquals(java.util.Arrays.asList("A", null, "C"), SEDAPExpressMessage.splitStringDataHashTag("A##C"));
		Assertions.assertEquals(java.util.Arrays.asList(1, null, -3), SEDAPExpressMessage.splitIntegerDataHashTag("1# #-3".replace(" ", "")));
		Assertions.assertEquals(java.util.Arrays.asList(1.5, null, -2.25), SEDAPExpressMessage.splitDoubleDataHashTag("1.5##-2.25"));
	}

	@Test
	void classificationConversionsAndOrderingAreConsistent() {
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Classification.getValueOfClassification('R'));
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Secret, SEDAPExpressMessage.getClassificationFromFullName("NATO Secret"));
		Assertions.assertEquals("confidential", SEDAPExpressMessage.getClassificationFullNameFromClassification(SEDAPExpressMessage.Classification.Confidential));
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Secret,
				SEDAPExpressMessage.getMaxClassification(SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Classification.Secret));
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Restricted,
				SEDAPExpressMessage.getMinClassification(SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Classification.Secret));
		Assertions.assertTrue(SEDAPExpressMessage.isClassificationIncluded(SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Classification.Secret));
		Assertions.assertFalse(SEDAPExpressMessage.isClassificationIncluded(SEDAPExpressMessage.Classification.Secret, SEDAPExpressMessage.Classification.Restricted));
	}

	@Test
	void messageFactoryAndSerializerRoundTripAcknowledge() {
		String wire = "ACKNOWLEDGE;18;0195238E25AD;129E;R;;;LASSY;COMMAND;2B";
		SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(wire);
		Assertions.assertInstanceOf(ACKNOWLEDGE.class, parsed);
		Assertions.assertEquals(wire, parsed.toString());
		Assertions.assertEquals(wire + "\n", SEDAPExpressMessage.serialize(parsed));
	}

	@Test
	void messageFactoryReturnsNullForUnknownOrEmptyMessageType() {
		Assertions.assertNull(SEDAPExpressMessage.deserialize(""));
		Assertions.assertNull(SEDAPExpressMessage.deserialize("NOT_A_MESSAGE;01"));
	}

	@Test
	void matchPatternHandlesNullAndValidInputAccordingToPatternContract() {
		Assertions.assertTrue(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, "7F"));
		Assertions.assertFalse(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, "80"));
		Assertions.assertFalse(SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, "GG"));
	}

	@Test
	void splitDoubleArrayHashTagParsesEachCoordinateTuple() {
		java.util.List<double[]> values = SEDAPExpressMessage.splitDoubleArrayDataHashTag("1.0,2.0#3.5,4.5");
		Assertions.assertEquals(2, values.size());
		Assertions.assertArrayEquals(new double[] { 1.0, 2.0 }, values.get(0), 0.0);
		Assertions.assertArrayEquals(new double[] { 3.5, 4.5 }, values.get(1), 0.0);
	}

	@Test
	void classificationIndexMappingIncludesFallback() {
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Public, SEDAPExpressMessage.getClassificationFromIndex(0));
		Assertions.assertEquals(SEDAPExpressMessage.Classification.Top_Secret, SEDAPExpressMessage.getClassificationFromIndex(5));
		Assertions.assertEquals(SEDAPExpressMessage.Classification.None, SEDAPExpressMessage.getClassificationFromIndex(-1));
	}

	@Test
	void listFormattingUsesHashAsSeparator() {
		GENERIC message = new GENERIC();
		Assertions.assertEquals("1#2#3", message.getStringFromList(java.util.List.of(1, 2, 3)));
		Assertions.assertEquals("alpha#beta", message.getStringFromList(java.util.List.of("alpha", "beta")));
	}

	@Test
	void recordToCsvFormatsCommandParameters() {
		Assertions.assertEquals("ntp.example.org", SEDAPExpressMessage.objectToCSV(new COMMAND.SyncTime("ntp.example.org")));
		Assertions.assertEquals("", SEDAPExpressMessage.objectToCSV(null));
		Assertions.assertEquals("", SEDAPExpressMessage.objectToCSV(new COMMAND.Restart()));
	}

	@Test
	void equalityUsesCanonicalWireRepresentationAndConsistentHashCode() {
		String wire = "HEARTBEAT;18;0195238E25AD;129E;R;;;LASSY";
		HEARTBEAT first = new HEARTBEAT(wire);
		HEARTBEAT second = new HEARTBEAT(wire);

		Assertions.assertNotSame(first, second);
		Assertions.assertEquals(first, second);
		Assertions.assertEquals(first.hashCode(), second.hashCode());
		Assertions.assertFalse(first.equals(new TIMESYNC()));
	}

}
