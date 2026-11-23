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

class GRAPHICTest {

	@Test
	final void testConstructorValues() {

		GRAPHIC graphic = new GRAPHIC((byte) 0x79, 0x0195238E35ADL, "910E",
				SEDAPExpressMessage.Classification.Unclas, SEDAPExpressMessage.Acknowledgement.FALSE, null,
				2.0, 0xFF800000L, 0x80808000L, 0xFFFFFFFFL, SEDAPExpressMessage.DataEncoding.NONE,
				"Area Alpha", new GRAPHIC.Point(53.43, 9.45, 0.0));
		Assertions.assertEquals(GRAPHIC.GraphicType.Point, graphic.getGraphicType());
		Assertions.assertEquals(2.0, graphic.getLineWidth());
		Assertions.assertEquals(0xFF800000L, graphic.getLineColor());
		Assertions.assertEquals("Area Alpha", graphic.getAnnotation());
		Assertions.assertInstanceOf(GRAPHIC.Point.class, graphic.getGraphicObject());
	}

	@Test
	final void testStringConstructorParsesFields() {

		GRAPHIC graphic = new GRAPHIC("GRAPHIC;79;0195238E35AD;910E;U;;;FFDA;;8;1;FF800000;;;BASE64;QXJlYSBBbHBoYQ==;53.43;9.45;0;1000");
		Assertions.assertEquals("FFDA", graphic.getGraphicID());
		Assertions.assertEquals(GRAPHIC.GraphicType.Sphere, graphic.getGraphicType());
		Assertions.assertEquals("Area Alpha", graphic.getAnnotation());
		Assertions.assertInstanceOf(GRAPHIC.Sphere.class, graphic.getGraphicObject());
	}

	@Test
	final void testConstructorIterator() {

		GRAPHIC parsed = new GRAPHIC(SEDAPExpressMessage.splitMessage(
				"79;0195238E35AD;910E;U;;;FFDA;;8;1;FF800000;;;BASE64;QXJlYSBBbHBoYQ==;53.43;9.45;0;1000").iterator());
		Assertions.assertEquals(GRAPHIC.GraphicType.Sphere, parsed.getGraphicType());
		Assertions.assertInstanceOf(GRAPHIC.Sphere.class, parsed.getGraphicObject());
		Assertions.assertEquals(GRAPHIC.GraphicType.Point, GRAPHIC.GraphicType.valueOfGraphicType(0));
		Assertions.assertEquals(GRAPHIC.GraphicType.WeaponFieldOfFire, GRAPHIC.GraphicType.valueOfGraphicType(11));
		Assertions.assertEquals(GRAPHIC.GraphicType.Point, GRAPHIC.GraphicType.valueOfGraphicType(99));
	}

	@Test
	final void testSamplesFromDocu() {

		String[] messages = new String[] {
				"GRAPHIC;79;0195238E35AD;910E;U;;;FFDA;;08;1;FF800000;;;BASE64;QXJlYSBBbHBoYQ==;53.43;9.45;0;1000",
				"GRAPHIC;78;0195238E45AD;910E;U;;;A327;;01;1;80808000;;FFFF0000;;Transit;54.23,12.86#54.3,12.9#54.55,13.3"
		};

		Arrays.asList(messages).forEach(msg -> Assertions.assertEquals(msg, new GRAPHIC(msg).toString()));

	}

}
