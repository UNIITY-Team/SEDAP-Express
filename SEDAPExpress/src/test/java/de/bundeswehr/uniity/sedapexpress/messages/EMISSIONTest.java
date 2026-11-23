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

class EMISSIONTest {

	@Test
	final void testConstructorValues() {

		EMISSION emission = new EMISSION((byte) 0x5E, 0x0195238E15ADL, "66A3",
                SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Acknowledgement.FALSE, null,
                "100", SEDAPExpressMessage.DeleteFlag.FALSE, 53.32, 8.11, 0.0,
                null, null, null, 20.0, Arrays.asList(8725000.0, 8735000.0), 20000.0, 3.0,
                EMISSION.FreqAgility.Stable_Fixed, EMISSION.PRFAgility.Jittered,
                EMISSION.Function.ESM_Iff, 10233, null, "SA-8");
        Assertions.assertEquals("100", emission.getEmissionID());
        Assertions.assertEquals(SEDAPExpressMessage.DeleteFlag.FALSE, emission.getDeleteFlag());
        Assertions.assertEquals(53.32, emission.getSensorLatitude());
        Assertions.assertEquals(8.11, emission.getSensorLongitude());
        Assertions.assertEquals(20.0, emission.getBearing());
        Assertions.assertEquals(Arrays.asList(8725000.0, 8735000.0), emission.getFrequency());
        Assertions.assertEquals(EMISSION.FreqAgility.Stable_Fixed, emission.getFreqAgility());
        Assertions.assertEquals(EMISSION.PRFAgility.Jittered, emission.getPrfAgility());
        Assertions.assertEquals(EMISSION.Function.ESM_Iff, emission.getFunction());
        Assertions.assertEquals(10233, emission.getSpotNumber());
        Assertions.assertEquals("SA-8", emission.getComment());
	}

	@Test
	final void testStringConstructorParsesFields() {

		String payload = "5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;0;2;6;10233;;SA-8";
        EMISSION emission = new EMISSION("EMISSION;" + payload);
        Assertions.assertEquals("100", emission.getEmissionID());
        Assertions.assertEquals(Arrays.asList(8725000.0, 8735000.0), emission.getFrequency());
        Assertions.assertEquals(EMISSION.FreqAgility.Stable_Fixed, emission.getFreqAgility());
        Assertions.assertEquals(EMISSION.PRFAgility.Jittered, emission.getPrfAgility());
        Assertions.assertEquals(EMISSION.Function.ESM_Iff, emission.getFunction());
	}

	@Test
	final void testConstructorIterator() {

		String payload = "5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;0;2;6;10233;;SA-8";
        EMISSION parsed = new EMISSION(SEDAPExpressMessage.splitMessage(payload).iterator());
        Assertions.assertEquals("100", parsed.getEmissionID());
        Assertions.assertEquals(Arrays.asList(8725000.0, 8735000.0), parsed.getFrequency());
        Assertions.assertEquals(EMISSION.FreqAgility.Unknown, EMISSION.FreqAgility.valueOfFreqAgility(99));
        Assertions.assertEquals(EMISSION.PRFAgility.UnknownPRF, EMISSION.PRFAgility.valueOfPRFAgility(99));
        Assertions.assertEquals(EMISSION.Function.Unknown, EMISSION.Function.valueOfFunction(99));
	}

	@Test
	final void testSamplesFromDocu() {

		String[] messages = new String[] { "EMISSION;5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;0;2;6;10233;;SA-8",
				"EMISSION;5F;0195238E25AD;66A3;R;;;101;;54.86;9.32;0;52.12;9.8;50;233;25725.0;4000;1;5;2;0;;SNGPESR--------" };

		Arrays.asList(messages).forEach(msg -> Assertions.assertEquals(msg, new EMISSION(msg).toString()));

	}

}
