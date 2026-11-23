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

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class METEOTest {

	@Test
	final void testConstructorValues() {

		METEO meteo = new METEO((byte) 0x1C, 0x0195238E25ADL, "74BE", SEDAPExpressMessage.Classification.Unclas, SEDAPExpressMessage.Acknowledgement.FALSE, null, 15.4, 15.5, 12.0, 18.0, 100.0, 10.2,
				8.0, 72.0, 1005.0, 25.0, 111.0, 50.0, 2500.0, 33.0, "RefPoint1");
		Assertions.assertEquals(15.4, meteo.getSpeedThroughWater());
		Assertions.assertEquals(15.5, meteo.getWaterSpeed());
		Assertions.assertEquals(12.0, meteo.getWaterDirection());
		Assertions.assertEquals(18.0, meteo.getWaterTemperature());
		Assertions.assertEquals(100.0, meteo.getWaterDepth());
		Assertions.assertEquals(10.2, meteo.getAirTemperature());
		Assertions.assertEquals(8.0, meteo.getDewPoint());
		Assertions.assertEquals(72.0, meteo.getHumidityRel());
		Assertions.assertEquals(1005.0, meteo.getPressure());
		Assertions.assertEquals(25.0, meteo.getWindSpeed());
		Assertions.assertEquals(111.0, meteo.getWindDirection());
		Assertions.assertEquals(50.0, meteo.getVisibility());
		Assertions.assertEquals(2500.0, meteo.getCloudHeight());
		Assertions.assertEquals(33.0, meteo.getCloudCover());
		Assertions.assertEquals("RefPoint1", meteo.getReference());
	}

	@Test
	final void testStringConstructorParsesFields() {

		METEO meteo = new METEO("METEO;1C;0195238E25AD;74BE;U;;;15.4;15.5;;;;10.2;72;1005;25;111;50;2500;33;;RefPoint1");
		Assertions.assertEquals(15.4, meteo.getSpeedThroughWater());
		Assertions.assertEquals(15.5, meteo.getWaterSpeed());
		Assertions.assertEquals(10.2, meteo.getAirTemperature());
		Assertions.assertEquals("RefPoint1", meteo.getReference());
	}

	@Test
	final void testConstructorIterator() {

		METEO parsed = new METEO(SEDAPExpressMessage.splitMessage("1C;0195238E25AD;74BE;U;;;15.4;15.5;;;;;10.2;72;1005;25;111;50;2500;33;RefPoint1").iterator());
		Assertions.assertEquals(15.4, parsed.getSpeedThroughWater());
		Assertions.assertEquals("RefPoint1", parsed.getReference());

		METEO meteo = new METEO();
		Assertions.assertNull(meteo.getSpeedThroughWater());
		Assertions.assertNull(meteo.getWindSpeed());
		Assertions.assertNull(meteo.getReference());
		meteo.setWindSpeed(12.5);
		meteo.setReference("Sensor-A");
		Assertions.assertEquals(12.5, meteo.getWindSpeed());
		Assertions.assertEquals("Sensor-A", meteo.getReference());
	}

	@Test
	final void testSamplesFromDocu() {

		String[] messages = new String[] { "METEO;1C;0195238E25AD;74BE;U;;;15.4;15.5;;;;;10.2;72;1005;25;111;50;2500;33;RefPoint1",
				"METEO;;0195238E25AD;;U;;;23.2;100;;;;10.2;72;80;998;20;;;500;100;1000" };

		Arrays.asList(messages).forEach(msg -> Assertions.assertEquals(msg, new METEO(msg).toString()));

	}

}
