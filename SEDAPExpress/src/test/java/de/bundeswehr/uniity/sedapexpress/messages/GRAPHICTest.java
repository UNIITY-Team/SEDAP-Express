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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GRAPHICTest {

    @ParameterizedTest
    @CsvSource({
	"00, 0, Point, 1;2;3",
	"01, 1, Path, '1,2,3#4,5,6'",
	"02, 2, Polygon, '1,2,3#4,5,6#7,8,9'",
	"03, 3, Rectangle, 1;2;3;4;5;6",
	"04, 4, Square, 1;2;3;4;5",
	"05, 5, Circle, 1;2;3;4;5;6",
	"06, 6, Ellipse, 1;2;3;4;5;6",
	"07, 7, Block, 1;2;3;4;5;6;7;8;9",
	"08, 8, Sphere, 1;2;3;4",
	"09, 9, Ellipsoid, 1;2;3;4;5;6;7;8;9",
	"0A, 10, SensorFieldOfView, '1;2;3,4,5#6,7,8'",
	"0B, 11, WeaponFieldOfFire, '1;2;3,4,5#6,7,8'"
    })
    void canonicalAndLegacyCodesPreserveGeometry(String canonical, String legacy, String typeName, String payload) {
	GRAPHIC.GraphicType expectedType = GRAPHIC.GraphicType.valueOf(typeName);
	Assertions.assertEquals(canonical, expectedType.toString());
	for (String code : new String[] { canonical, canonical.toLowerCase(java.util.Locale.ROOT), legacy }) {
	    GRAPHIC graphic = new GRAPHIC(graphicMessage(code, payload));
	    Assertions.assertEquals(expectedType, graphic.getGraphicType());
	    Assertions.assertNotNull(graphic.getGraphicObject());
	    Assertions.assertEquals(typeName, graphic.getGraphicObject().getClass().getSimpleName());
	    Assertions.assertEquals(canonical, graphic.toString().split(";", -1)[9]);
	    GRAPHIC roundTrip = new GRAPHIC(graphic.toString());
	    Assertions.assertEquals(expectedType, roundTrip.getGraphicType());
	    Assertions.assertEquals(SEDAPExpressMessage.objectToCSV(graphic.getGraphicObject()),
		    SEDAPExpressMessage.objectToCSV(roundTrip.getGraphicObject()));
	}
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "12", "0C", "FF", "000", "-1", "A", " 01" })
    void rejectsUnknownWireCodes(String code) {
	Assertions.assertThrows(IllegalArgumentException.class, () -> new GRAPHIC(graphicMessage(code, "1;2;3")));
    }

    @Test
    void rejectsUnknownNumericTypes() {
	Assertions.assertThrows(IllegalArgumentException.class, () -> GRAPHIC.GraphicType.valueOfGraphicType(-1));
	Assertions.assertThrows(IllegalArgumentException.class, () -> GRAPHIC.GraphicType.valueOfGraphicType(12));
    }

    private static String graphicMessage(String code, String payload) {
	return "GRAPHIC;01;0195238E35AD;unit;U;;;shape;;" + code + ";1;;;;;label;" + payload;
    }

    @Test
    final void testConstructorValues() {

	// TODO
    }

    @Test
    final void testConstructorString() {

	// TODO
    }

    @Test
    final void testConstructorIterator() {

	// TODO
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
