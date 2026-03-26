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

import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.GenericAction;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.HoldEngagement;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.MoveTo;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.RecordVideo;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.SyncTime;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;

/**
 *
 * @author Volker Voß
 *
 */
class COMMANDTest {

    @Test
    final void testConstructorValues() {

	COMMAND command = new COMMAND((byte) 55, 0x019D25300FC0L, "8F3A", Classification.Secret, Acknowledgement.TRUE, "4389F10D", "7D31", (short) 0x3311, COMMAND.CommandFlag.CancelAll,
		641255534L, new SyncTime("10.8.0.6"));

	Assertions.assertEquals((byte) 55, command.getNumber());
	Assertions.assertEquals(0x019D25300FC0L, command.getTime());
	Assertions.assertEquals("8F3A", command.getSender());
	Assertions.assertEquals(Classification.Secret, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertEquals("4389F10D", command.getMAC());
	Assertions.assertEquals("7D31", command.getRecipient());
	Assertions.assertEquals((short) 0x3311, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.CancelAll, command.getCmdFlag());
	Assertions.assertEquals(641255534L, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.SyncTime, command.getCmdType());
	Assertions.assertEquals(COMMAND.SyncTime.class, command.getCmdObject().getClass());
	Assertions.assertEquals("10.8.0.6", ((SyncTime) command.getCmdObject()).ntpServer());

	command = new COMMAND((byte) 55, 0x641211134L, "B377", Classification.Restricted, Acknowledgement.FALSE, "43545332", "4433", (short) 0x3311, COMMAND.CommandFlag.Replace,
		0x666655534L, new MoveTo(55.5, 11.1, 5000.0, 20.0, 0x019D25300FC0L));

	Assertions.assertEquals((byte) 55, command.getNumber());
	Assertions.assertEquals(0x641211134L, command.getTime());
	Assertions.assertEquals("B377", command.getSender());
	Assertions.assertEquals(Classification.Restricted, command.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, command.getAcknowledgement());
	Assertions.assertEquals("43545332", command.getMAC());
	Assertions.assertEquals("4433", command.getRecipient());
	Assertions.assertEquals((short) 0x3311, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.Replace, command.getCmdFlag());
	Assertions.assertEquals(0x666655534L, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.MoveTo, command.getCmdType());
	Assertions.assertEquals(COMMAND.MoveTo.class, command.getCmdObject().getClass());
	Assertions.assertEquals(55.5, ((MoveTo) command.getCmdObject()).latitude());
	Assertions.assertEquals(11.1, ((MoveTo) command.getCmdObject()).longitude());
	Assertions.assertEquals(5000, ((MoveTo) command.getCmdObject()).altitude());
	Assertions.assertEquals(20, ((MoveTo) command.getCmdObject()).tolerance());
	Assertions.assertEquals(0x019D25300FC0L, ((MoveTo) command.getCmdObject()).timestamp());

	command = new COMMAND((byte) 0x55, 0x019D25300FC0L, "B377", Classification.Restricted, Acknowledgement.FALSE, "43545332", "4433", (short) 0x3311, COMMAND.CommandFlag.Replace,
		0x019D75300FFFL, new RecordVideo("CAM1", COMMAND.PowerState.ON, 3600));

	Assertions.assertEquals((byte) 0x55, command.getNumber());
	Assertions.assertEquals(0x019D25300FC0L, command.getTime());
	Assertions.assertEquals("B377", command.getSender());
	Assertions.assertEquals(Classification.Restricted, command.getClassification());
	Assertions.assertEquals(Acknowledgement.FALSE, command.getAcknowledgement());
	Assertions.assertEquals("43545332", command.getMAC());
	Assertions.assertEquals("4433", command.getRecipient());
	Assertions.assertEquals((short) 0x3311, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.Replace, command.getCmdFlag());
	Assertions.assertEquals(0x019D75300FFFL, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.RecordVideo, command.getCmdType());
	Assertions.assertEquals(COMMAND.RecordVideo.class, command.getCmdObject().getClass());
	Assertions.assertEquals("CAM1", ((RecordVideo) command.getCmdObject()).cameraId());
	Assertions.assertEquals(COMMAND.PowerState.ON, ((RecordVideo) command.getCmdObject()).powerState());
	Assertions.assertEquals(3600, ((RecordVideo) command.getCmdObject()).duration());

	Assertions.assertEquals("COMMAND;55;019D25300FC0;B377;R;;43545332;4433;3311;01;019D75300FFF;34;CAM1;ON;3600", command.toString());
    }

    @Test
    final void testConstructorString() {

	String message = "COMMAND;55;019D25300FC0;5BCD;S;TRUE;4389F10D;7D31;1221;01;019D25300FFF;55;Gun;1000";

	COMMAND command = new COMMAND(message);

	Assertions.assertEquals((byte) 0x55, command.getNumber());
	Assertions.assertEquals(0x019D25300FC0L, command.getTime());
	Assertions.assertEquals("5BCD", command.getSender());
	Assertions.assertEquals(Classification.Secret, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertEquals("4389F10D", command.getMAC());
	Assertions.assertEquals("7D31", command.getRecipient());
	Assertions.assertEquals((short) 0x1221, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.Replace, command.getCmdFlag());
	Assertions.assertEquals(0x019D25300FFFL, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.HoldEngagement, command.getCmdType());
	Assertions.assertEquals(COMMAND.HoldEngagement.class, command.getCmdObject().getClass());
	Assertions.assertEquals("Gun", ((HoldEngagement) command.getCmdObject()).weaponId());
	Assertions.assertEquals("1000", ((HoldEngagement) command.getCmdObject()).targetId());

	message = "COMMAND;29;019D25300FC0;E4B3;C;TRUE;;ORKA;0331;00;;24;53.5143;8.1574;50;5;019D25300FC0";

	command = new COMMAND(message);

	Assertions.assertEquals((byte) 0x29, command.getNumber());
	Assertions.assertEquals(0x019D25300FC0L, command.getTime());
	Assertions.assertEquals("E4B3", command.getSender());
	Assertions.assertEquals(Classification.Confidential, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertNull(command.getMAC());
	Assertions.assertEquals("ORKA", command.getRecipient());
	Assertions.assertEquals((short) 0x0331, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.Add, command.getCmdFlag());
	Assertions.assertNull(command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.MoveTo, command.getCmdType());
	Assertions.assertEquals(COMMAND.MoveTo.class, command.getCmdObject().getClass());
	Assertions.assertEquals(53.5143, ((MoveTo) command.getCmdObject()).latitude());
	Assertions.assertEquals(8.1574, ((MoveTo) command.getCmdObject()).longitude());
	Assertions.assertEquals(50, ((MoveTo) command.getCmdObject()).altitude());
	Assertions.assertEquals(5, ((MoveTo) command.getCmdObject()).tolerance());
	Assertions.assertEquals(0x019D25300FC0L, ((MoveTo) command.getCmdObject()).timestamp());

	message = "COMMAND;29;0195238F55AD;E4B3;C;TRUE;;Drone2;0000;03";

	command = new COMMAND(message);

	Assertions.assertEquals((byte) 0x29, command.getNumber());
	Assertions.assertEquals(0x0195238F55ADL, command.getTime());
	Assertions.assertEquals("E4B3", command.getSender());
	Assertions.assertEquals(Classification.Confidential, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertEquals(null, command.getMAC());
	Assertions.assertEquals("Drone2", command.getRecipient());
	Assertions.assertEquals(COMMAND.CommandFlag.CancelAll, command.getCmdFlag());
	Assertions.assertNull(command.getCmdExTime());

	message = "COMMAND;29;019D25300FBB;E4B3;C;TRUE;;Drone1;;00;1111;FF;BombStorage;OPEN_BAY";

	command = new COMMAND(message);

	Assertions.assertEquals((byte) 0x29, command.getNumber());
	Assertions.assertEquals(0x019D25300FBBL, command.getTime());
	Assertions.assertEquals("E4B3", command.getSender());
	Assertions.assertEquals(Classification.Confidential, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertEquals(null, command.getMAC());
	Assertions.assertEquals("Drone1", command.getRecipient());
	Assertions.assertEquals(COMMAND.CommandFlag.Add, command.getCmdFlag());
	Assertions.assertEquals(0x1111, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.GenericAction, command.getCmdType());
	Assertions.assertEquals(COMMAND.GenericAction.class, command.getCmdObject().getClass());
	Assertions.assertEquals("BombStorage;OPEN_BAY", ((GenericAction) command.getCmdObject()).kindOfAction());

    }

    @Test
    final void testConstructorIterator() {

	Iterator<String> it = Arrays.asList("55", "1B351C87", "5BCD", "S", "TRUE", "4389F10D", "7D31", "2892", "00", "019D25300FBB", "3", "10.0.0.1").iterator();

	final COMMAND command = new COMMAND(it);

	Assertions.assertEquals((byte) 0x55, command.getNumber());
	Assertions.assertEquals(0x1B351C87L, command.getTime());
	Assertions.assertEquals("5BCD", command.getSender());
	Assertions.assertEquals(Classification.Secret, command.getClassification());
	Assertions.assertEquals(Acknowledgement.TRUE, command.getAcknowledgement());
	Assertions.assertEquals("4389F10D", command.getMAC());
	Assertions.assertEquals("7D31", command.getRecipient());
	Assertions.assertEquals((short) 0x2892, command.getCmdId());
	Assertions.assertEquals(COMMAND.CommandFlag.Add, command.getCmdFlag());
	Assertions.assertEquals(0x019D25300FBBL, command.getCmdExTime());
	Assertions.assertEquals(COMMAND.CommandType.SyncTime, command.getCmdType());
	Assertions.assertEquals(COMMAND.SyncTime.class, command.getCmdObject().getClass());
	Assertions.assertEquals("10.0.0.1", ((SyncTime) command.getCmdObject()).ntpServer());
    }

    @Test
    final void testSamplesFromDocu() {

	String[] messages = new String[] {
		"COMMAND;29;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.5143;8.1574;50;5",
		"COMMAND;2A;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4897;8.1908;50;5",
		"COMMAND;2B;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4694;8.2131;50;5",
		"COMMAND;2C;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4397;8.2262;50;5;19D25300FC0",
		"COMMAND;2C;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;01;019D75300FFF;34;CAM1;ON;3600",
		"COMMAND;2E;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;30;53.4397;8.2262;1000;200"
	};

	Arrays.asList(messages).forEach(msg -> Assertions.assertEquals(msg, new COMMAND(msg).toString()));

    }
}
