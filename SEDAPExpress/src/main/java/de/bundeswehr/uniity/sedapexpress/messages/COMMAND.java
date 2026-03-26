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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * 
 * @author Volker Voß
 *
 */
public class COMMAND extends SEDAPExpressMessage {

    private static final long serialVersionUID = -5662357406861380560L;

    public enum PowerState {
	ON, OFF;

	public static PowerState parseState(String input) {
	    if (input == null)
		return OFF;
	    String val = input.trim().toUpperCase();
	    if (val.equals("1") || val.equals("ON") || val.equals("TRUE")) {
		return ON;
	    }
	    return OFF;
	}
    }

    public enum CommandFlag {

	Add((byte) 0), Replace((byte) 1), CancelLast((byte) 2), CancelAll((byte) 3);

	byte flag;

	public byte getFlagValue() {

	    return this.flag;
	}

	CommandFlag(byte flag) {
	    this.flag = flag;
	}

	public static CommandFlag valueOfCommandFlag(byte type) {

	    return switch (type) {
	    case 0 -> Add;
	    case 1 -> Replace;
	    case 2 -> CancelLast;
	    case 3 -> CancelAll;
	    default -> Add;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.flag);
	}

    }

    public enum CommandType {

	// 00 Group: System & Basics
	PowerOff((byte) 0x00),
	Restart((byte) 0x01),
	Standby((byte) 0x02),
	SyncTime((byte) 0x03),
	CalibrateGyro((byte) 0x04),
	CalibrateCompass((byte) 0x05),
	SendStatus((byte) 0x06),
	SetManualMode((byte) 0x07),
	SetSemiAutonomousMode((byte) 0x08),
	SetAutonomousMode((byte) 0x09),
	SetFailsafeMode((byte) 0x0A),

	// 10 Group: Propulsion & Hardware
	StartEngine((byte) 0x10),
	TestEngine((byte) 0x11),
	SetEnginePower((byte) 0x12),
	StopEngine((byte) 0x13),
	StopMovement((byte) 0x14),
	ToggleLights((byte) 0x15),
	DeployParachute((byte) 0x16),

	// 20 Group: Navigation & Movement
	SetHeading((byte) 0x20),
	SetAltitude((byte) 0x21),
	SetSpeed((byte) 0x22),
	Rotate((byte) 0x23),
	MoveTo((byte) 0x24),
	FollowContact((byte) 0x25),
	ReturnHome((byte) 0x26),
	SetHomeLocation((byte) 0x27),
	TakeOff((byte) 0x28),
	Land((byte) 0x29),
	Submerge((byte) 0x2A),
	Surface((byte) 0x2B),
	Dock((byte) 0x2C),

	// 30 Group: Mission & Sensors
	LoiterOrbiting((byte) 0x30),
	Scan((byte) 0x31),
	ScanArea((byte) 0x32),
	TakePhoto((byte) 0x33),
	RecordVideo((byte) 0x34),
	StreamVideo((byte) 0x35),
	SetCameraParameters((byte) 0x36),
	SetOrientationOfCamera((byte) 0x37),

	// 40 Groupe: Actuatorics
	ActuatorCheck((byte) 0x40),
	SetOrientationOfActuator((byte) 0x41),
	ActuatorPickUpObject((byte) 0x42),
	ActuatorReleaseObject((byte) 0x43),

	// 50 Group: Combat & Arming
	PreArmCheck((byte) 0x50),
	Arm((byte) 0x51),
	Disarm((byte) 0x52),
	SetOrientationOfWeapon((byte) 0x53),
	StartEngagement((byte) 0x54),
	HoldEngagement((byte) 0x55),
	StopEngagement((byte) 0x56),

	// Special & Emergency
	SanitizeSystem((byte) 0xEE),
	SelfDestruction((byte) 0xEF),
	GenericAction((byte) 0xFF);

	private final byte type;
	private static final Map<Byte, CommandType> LOOKUP = new HashMap<>();

	static {
	    for (CommandType ct : CommandType.values()) {
		CommandType.LOOKUP.put(ct.type, ct);
	    }
	}

	CommandType(byte type) {
	    this.type = type;
	}

	public byte getTypeValue() {
	    return this.type;
	}

	/**
	 * Ermittelt den CommandType anhand des Byte-Wertes. Liefert GenericAction zurück, falls der Wert unbekannt ist.
	 */
	public static CommandType valueOfCommandType(byte type) {
	    return CommandType.LOOKUP.getOrDefault(type, GenericAction);
	}

	@Override
	public String toString() {
	    return String.valueOf(this.type);
	}

    }

    public enum CameraMode {
	DL, // Day Light
	IR, // Infra Red
	LI // Light Intensifier
    }

    public interface CommandObject {
    };

    // 00 Group: System & Basics
    public record PowerOff(long powerOnTimestamp) implements CommandObject {
    }

    public record Restart() implements CommandObject {
    }

    public record Standby(long wakeupTimestamp) implements CommandObject {
    }

    public record SyncTime(String ntpServer) implements CommandObject {
    }

    public record CalibrateGyro() implements CommandObject {
    }

    public record CalibrateCompass() implements CommandObject {
    }

    public record SendStatus() implements CommandObject {
    }

    public record SetManualMode() implements CommandObject {
    }

    public record SetSemiAutonomousMode() implements CommandObject {
    }

    public record SetAutonomousMode() implements CommandObject {
    }

    public record SetFailsafeMode() implements CommandObject {
    }

    // 10 Group: Propulsion & Hardware
    public record StartEngine() implements CommandObject {
    }

    public record TestEngine() implements CommandObject {
    }

    /**
     * @param powerLevel 0-100%
     */
    public record SetEnginePower(Double powerLevel) implements CommandObject {
    }

    public record StopEngine() implements CommandObject {
    }

    public record StopMovement() implements CommandObject {
    }

    /**
     * @param brightness 0-100%
     */
    public record ToggleLights(PowerState powerState, Double brightness, Double strobePeriod) implements CommandObject {
    }

    public record DeployParachute() implements CommandObject {
    }

    // 20 Group: Navigation & Movement
    public record SetHeading(Double headingAngle) implements CommandObject {
    }

    public record SetAltitude(Double altitude) implements CommandObject {
    }

    public record SetSpeed(Double speed) implements CommandObject {
    }

    public record Rotate(Double heading, Double roll, Double pitch) implements CommandObject {
    }

    public record MoveTo(Double latitude, Double longitude, Double altitude, Double tolerance, Long timestamp) implements CommandObject {
    }

    public record FollowContact(String contactId) implements CommandObject {
    }

    public record ReturnHome(Double tolerance, long timestamp) implements CommandObject {
    }

    public record SetHomeLocation(Double latitude, Double longitude, Double altitude, Double tolerance) implements CommandObject {
    }

    public record TakeOff(Double latitude, Double longitude, Double direction) implements CommandObject {
    }

    public record Land(Double latitude, Double longitude, Double direction) implements CommandObject {
    }

    public record Submerge(Double depth) implements CommandObject {
    }

    public record Surface() implements CommandObject {
    }

    public record Dock(Double latitude, Double longitude, Double direction) implements CommandObject {
    }

    // 30 Group: Mission & Sensors
    public record Loiter(Double latitude, Double longitude, Double altitude, Double radius) implements CommandObject {
    }

    public record Scan(Double latitude, Double longitude, Double altitude) implements CommandObject {
    }

    public record ScanArea(Double latitude1, Double lon1, Double latitude2, Double lon2, Double altitude, Double rotation) implements CommandObject {
    }

    public record TakePhoto(String cameraId) implements CommandObject {
    }

    public record RecordVideo(String cameraId, PowerState powerState, Integer duration) implements CommandObject {
    }

    public record StreamVideo(String cameraId, PowerState powerState) implements CommandObject {
    }

    /**
     * 
     * @param zoom 0-100%
     *
     */
    public record SetCameraParameters(String cameraId, Double zoom, CameraMode mode) implements CommandObject {
    } // Mode: DayLight, InfraRed, etc.

    public record SetOrientationOfCamera(String cameraId, Double azimuth, Double elevation) implements CommandObject {
    }

    // 40 Groupe: Actuatorics
    public record ActuatorCheck(String actuatorId) implements CommandObject {
    }

    public record SetOrientationOfActuator(String actuatorId, Double azimuth, Double elevation) implements CommandObject {
    }

    public record ActuatorPickUpObject(String actuatorId) implements CommandObject {
    }

    public record ActuatorReleaseObject(String actuatorId) implements CommandObject {
    }

    // 50 Group: Combat & Arming
    public record PreArmCheck(String weaponId) implements CommandObject {
    }

    public record Arm(String weaponId) implements CommandObject {
    }

    public record Disarm(String weaponId) implements CommandObject {
    }

    public record SetOrientationOfWeapon(String weaponId, Double azimuth, Double elevation) implements CommandObject {
    }

    public record StartEngagement(String weaponId, String targetId) implements CommandObject {
    }

    public record HoldEngagement(String weaponId, String targetId) implements CommandObject {
    }

    public record StopEngagement(String weaponId, String targetId) implements CommandObject {
    }

    // // Special & Emergency
    public record SanitizeSystem() implements CommandObject {
    }

    public record SelfDestruction() implements CommandObject {
    }

    public record GenericAction(String kindOfAction) implements CommandObject {
    }

    private String recipient;

    private Short cmdId;

    private CommandFlag cmdFlag;

    private Long cmdExTime;

    private CommandType cmdType;

    private CommandObject cmdObject;

    public String getRecipient() {
	return this.recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public Short getCmdId() {
	return this.cmdId;
    }

    public void setCmdId(Short cmdId) {
	this.cmdId = cmdId;
    }

    public CommandFlag getCmdFlag() {
	return this.cmdFlag;
    }

    public void setCmdFlag(CommandFlag cmdFlag) {
	this.cmdFlag = cmdFlag;
    }

    public Long getCmdExTime() {
	return this.cmdExTime;
    }

    public void setCmdExTime(Long cmdExTime) {
	this.cmdExTime = cmdExTime;
    }

    public CommandType getCmdType() {
	return this.cmdType;
    }

    public void setCmdType(CommandType cmdType) {
	this.cmdType = cmdType;
    }

    public CommandObject getCmdObject() {
	return this.cmdObject;
    }

    public void setCmdObject(CommandObject cmdObject) {
	this.cmdObject = cmdObject;
    }

    /**
     * Instantiate a new default COMMAND message
     */
    public COMMAND() {

	super(null, null, null, null, null, null);

	this.recipient = null;
	this.cmdId = null;
	this.cmdFlag = null;
	this.cmdType = null;
	this.cmdObject = null;
    }

    /**
     * Instantiate a new COMMAND message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param cmdId
     * @param cmdFlag
     * @param cmdExTime
     * @param cmdType
     */
    public COMMAND(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient,
	    Short cmdId, CommandFlag cmdFlag, Long cmdExTime, CommandType cmdType) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.cmdId = cmdId;
	this.cmdFlag = cmdFlag;
	this.cmdExTime = cmdExTime;
	this.cmdType = cmdType;
	this.cmdObject = null;
    }

    /**
     * Instantiate a new COMMAND message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param cmdId
     * @param cmdFlag
     * @param cmdExTime
     * @param cmdObject
     */
    public COMMAND(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient,
	    Short cmdId, CommandFlag cmdFlag, Long cmdExTime, CommandObject cmdObject) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.cmdId = cmdId;
	this.cmdFlag = cmdFlag;
	this.cmdExTime = cmdExTime;
	this.cmdObject = cmdObject;

	// JDK 17 compatibility without pattern matching switch
	if (cmdObject instanceof PowerOff)
	    this.cmdType = CommandType.PowerOff;
	else if (cmdObject instanceof Restart)
	    this.cmdType = CommandType.Restart;
	else if (cmdObject instanceof Standby)
	    this.cmdType = CommandType.Standby;
	else if (cmdObject instanceof SyncTime)
	    this.cmdType = CommandType.SyncTime;
	else if (cmdObject instanceof CalibrateGyro)
	    this.cmdType = CommandType.CalibrateGyro;
	else if (cmdObject instanceof CalibrateCompass)
	    this.cmdType = CommandType.CalibrateCompass;
	else if (cmdObject instanceof SendStatus)
	    this.cmdType = CommandType.SendStatus;
	else if (cmdObject instanceof SetManualMode)
	    this.cmdType = CommandType.SetManualMode;
	else if (cmdObject instanceof SetSemiAutonomousMode)
	    this.cmdType = CommandType.SetSemiAutonomousMode;
	else if (cmdObject instanceof SetAutonomousMode)
	    this.cmdType = CommandType.SetAutonomousMode;
	else if (cmdObject instanceof SetFailsafeMode)
	    this.cmdType = CommandType.SetFailsafeMode;

	else if (cmdObject instanceof StartEngine)
	    this.cmdType = CommandType.StartEngine;
	else if (cmdObject instanceof TestEngine)
	    this.cmdType = CommandType.TestEngine;
	else if (cmdObject instanceof SetEnginePower)
	    this.cmdType = CommandType.SetEnginePower;
	else if (cmdObject instanceof StopEngine)
	    this.cmdType = CommandType.StopEngine;
	else if (cmdObject instanceof StopMovement)
	    this.cmdType = CommandType.StopMovement;
	else if (cmdObject instanceof ToggleLights)
	    this.cmdType = CommandType.ToggleLights;
	else if (cmdObject instanceof DeployParachute)
	    this.cmdType = CommandType.DeployParachute;

	else if (cmdObject instanceof SetHeading)
	    this.cmdType = CommandType.SetHeading;
	else if (cmdObject instanceof SetAltitude)
	    this.cmdType = CommandType.SetAltitude;
	else if (cmdObject instanceof SetSpeed)
	    this.cmdType = CommandType.SetSpeed;
	else if (cmdObject instanceof Rotate)
	    this.cmdType = CommandType.Rotate;
	else if (cmdObject instanceof MoveTo)
	    this.cmdType = CommandType.MoveTo;
	else if (cmdObject instanceof FollowContact)
	    this.cmdType = CommandType.FollowContact;
	else if (cmdObject instanceof ReturnHome)
	    this.cmdType = CommandType.ReturnHome;
	else if (cmdObject instanceof SetHomeLocation)
	    this.cmdType = CommandType.SetHomeLocation;
	else if (cmdObject instanceof TakeOff)
	    this.cmdType = CommandType.TakeOff;
	else if (cmdObject instanceof Land)
	    this.cmdType = CommandType.Land;
	else if (cmdObject instanceof Submerge)
	    this.cmdType = CommandType.Submerge;
	else if (cmdObject instanceof Surface)
	    this.cmdType = CommandType.Surface;
	else if (cmdObject instanceof Dock)
	    this.cmdType = CommandType.Dock;

	else if (cmdObject instanceof Loiter)
	    this.cmdType = CommandType.LoiterOrbiting;
	else if (cmdObject instanceof Scan)
	    this.cmdType = CommandType.Scan;
	else if (cmdObject instanceof ScanArea)
	    this.cmdType = CommandType.ScanArea;
	else if (cmdObject instanceof TakePhoto)
	    this.cmdType = CommandType.TakePhoto;
	else if (cmdObject instanceof RecordVideo)
	    this.cmdType = CommandType.RecordVideo;
	else if (cmdObject instanceof StreamVideo)
	    this.cmdType = CommandType.StreamVideo;
	else if (cmdObject instanceof SetCameraParameters)
	    this.cmdType = CommandType.SetCameraParameters;
	else if (cmdObject instanceof SetOrientationOfCamera)
	    this.cmdType = CommandType.SetOrientationOfCamera;

	else if (cmdObject instanceof ActuatorCheck)
	    this.cmdType = CommandType.ActuatorCheck;
	else if (cmdObject instanceof SetOrientationOfActuator)
	    this.cmdType = CommandType.SetOrientationOfActuator;
	else if (cmdObject instanceof ActuatorPickUpObject)
	    this.cmdType = CommandType.ActuatorPickUpObject;
	else if (cmdObject instanceof ActuatorReleaseObject)
	    this.cmdType = CommandType.ActuatorReleaseObject;

	else if (cmdObject instanceof PreArmCheck)
	    this.cmdType = CommandType.PreArmCheck;
	else if (cmdObject instanceof Arm)
	    this.cmdType = CommandType.Arm;
	else if (cmdObject instanceof Disarm)
	    this.cmdType = CommandType.Disarm;
	else if (cmdObject instanceof SetOrientationOfWeapon)
	    this.cmdType = CommandType.SetOrientationOfWeapon;
	else if (cmdObject instanceof StartEngagement)
	    this.cmdType = CommandType.StartEngagement;
	else if (cmdObject instanceof HoldEngagement)
	    this.cmdType = CommandType.HoldEngagement;
	else if (cmdObject instanceof StopEngagement)
	    this.cmdType = CommandType.StopEngagement;

	else if (cmdObject instanceof SanitizeSystem)
	    this.cmdType = CommandType.SanitizeSystem;
	else if (cmdObject instanceof SelfDestruction)
	    this.cmdType = CommandType.SelfDestruction;

	else if (cmdObject instanceof GenericAction)
	    this.cmdType = CommandType.GenericAction;

	else
	    this.cmdType = CommandType.GenericAction; // Fallback for unknown objects

    }

    /**
     * Instantiate a new COMMAND message from a serialized message
     *
     * @param message
     */
    public COMMAND(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     * Instantiate a new COMMAND message from a paramter list
     *
     * @param message
     */
    public COMMAND(Iterator<String> message) {

	super(message);

	String value;

	// Recipient
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "COMMAND", "COMMAND(Iterator<String> message)", "Optional field \"recipient\" is empty!");
	    } else {
		this.recipient = value;
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete message!");
	}

	// CmdID
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, value)) {
		this.cmdId = (short) HexFormat.fromHexDigits(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.INFO, "COMMAND", "COMMAND(Iterator<String> message)", "Optional field \"CmdID\" contains not a valid number!", value);
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete message!");
	}

	// CmdFlag
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Mandatory field \"cmdFlag\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.CMDTYPE_MATCHER, value)) {
		this.cmdFlag = CommandFlag.valueOfCommandFlag((byte) HexFormat.fromHexDigits(value));
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Mandatory field \"cmdFlag\" contains invalid value!", value);
	    }
	}

	// CmdExTime
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "COMMAND", "COMMAND(Iterator<String> message)", "Optional field \"cmdExTime\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BIGINTEGER_MATCHER, value)) {
		this.cmdExTime = Long.parseLong(value, 16);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Mandatory field \"cmdExTime\" contains invalid value!", value);
	    }
	}

	// CmdType
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Mandatory field \"cmdType\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.CMDTYPE_MATCHER, value)) {
		this.cmdType = CommandType.valueOfCommandType((byte) HexFormat.fromHexDigits(value));
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Mandatory field \"cmdType\" contains invalid value!", value);
	    }
	}

	// Variable part
	if (!message.hasNext()) {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete message!");
	} else {

	    // Variable part
	    if (!message.hasNext()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete message!");
	    } else {

		List<String> strList = new ArrayList<String>();
		message.forEachRemaining(strList::add);
		String[] parts = strList.toArray(String[]::new);

		switch (this.cmdType) {

		// --- 00 Group: System & Basics ---
		case PowerOff -> this.cmdObject = new PowerOff(parts.length > 0 ? Long.parseLong(parts[0], 16) : 0L);
		case Restart -> this.cmdObject = new Restart();
		case Standby -> this.cmdObject = new Standby(parts.length > 0 ? Long.parseLong(parts[0], 16) : 0L);
		case SyncTime -> {
		    if (parts.length >= 1)
			this.cmdObject = new SyncTime(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SyncTime values!");
		}
		case CalibrateGyro -> this.cmdObject = new CalibrateGyro();
		case CalibrateCompass -> this.cmdObject = new CalibrateCompass();
		case SendStatus -> this.cmdObject = new SendStatus();
		case SetManualMode -> this.cmdObject = new SetManualMode();
		case SetSemiAutonomousMode -> this.cmdObject = new SetSemiAutonomousMode();
		case SetAutonomousMode -> this.cmdObject = new SetAutonomousMode();
		case SetFailsafeMode -> this.cmdObject = new SetFailsafeMode();

		// --- 10 Group: Propulsion & Hardware ---
		case StartEngine -> this.cmdObject = new StartEngine();
		case TestEngine -> this.cmdObject = new TestEngine();
		case SetEnginePower -> {
		    if (parts.length >= 1)
			this.cmdObject = new SetEnginePower(Double.parseDouble(parts[0]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetEnginePower values!");
		}
		case StopEngine -> this.cmdObject = new StopEngine();
		case StopMovement -> this.cmdObject = new StopMovement();
		case ToggleLights -> {
		    if (parts.length >= 3)
			this.cmdObject = new ToggleLights(PowerState.parseState(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ToggleLights values!");
		}
		case DeployParachute -> this.cmdObject = new DeployParachute();

		// --- 20 Group: Navigation & Movement ---
		case SetHeading -> {
		    if (parts.length >= 1)
			this.cmdObject = new SetHeading(Double.parseDouble(parts[0]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetHeading values!");
		}
		case SetAltitude -> {
		    if (parts.length >= 1)
			this.cmdObject = new SetAltitude(Double.parseDouble(parts[0]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetAltitude values!");
		}
		case SetSpeed -> {
		    if (parts.length >= 1)
			this.cmdObject = new SetSpeed(Double.parseDouble(parts[0]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetSpeed values!");
		}
		case Rotate -> {
		    if (parts.length >= 3)
			this.cmdObject = new Rotate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Rotate values!");
		}
		case MoveTo -> {
		    if (parts.length == 5)
			this.cmdObject = new MoveTo(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Long.parseLong(parts[4], 16));
		    else if (parts.length == 4)
			this.cmdObject = new MoveTo(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), null);
		    else if (parts.length == 3)
			this.cmdObject = new MoveTo(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), null, null);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete MoveTo values!");
		}
		case FollowContact -> {
		    if (parts.length >= 1)
			this.cmdObject = new FollowContact(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete FollowContact values!");
		}
		case ReturnHome -> {
		    if (parts.length >= 2)
			this.cmdObject = new ReturnHome(Double.parseDouble(parts[0]), Long.parseLong(parts[1], 16));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ReturnHome values!");
		}
		case SetHomeLocation -> {
		    if (parts.length >= 4)
			this.cmdObject = new SetHomeLocation(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetHomeLocation values!");
		}
		case TakeOff -> {
		    if (parts.length >= 3)
			this.cmdObject = new TakeOff(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete TakeOff values!");
		}
		case Land -> {
		    if (parts.length >= 3)
			this.cmdObject = new Land(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Land values!");
		}
		case Submerge -> {
		    if (parts.length >= 1)
			this.cmdObject = new Submerge(Double.parseDouble(parts[0]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Submerge values!");
		}
		case Surface -> this.cmdObject = new Surface();
		case Dock -> {
		    if (parts.length >= 3)
			this.cmdObject = new Dock(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Dock values!");
		}

		// --- 30 Group: Mission & Sensors ---
		case LoiterOrbiting -> {
		    if (parts.length >= 4)
			this.cmdObject = new Loiter(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Loiter/Orbiting values!");
		}
		case Scan -> {
		    if (parts.length >= 3)
			this.cmdObject = new Scan(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Scan values!");
		}
		case ScanArea -> {
		    if (parts.length >= 6)
			this.cmdObject = new ScanArea(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ScanArea values!");
		}
		case TakePhoto -> {
		    if (parts.length >= 1)
			this.cmdObject = new TakePhoto(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete TakePhoto values!");
		}
		case RecordVideo -> {
		    if (parts.length >= 3)
			this.cmdObject = new RecordVideo(parts[0], PowerState.parseState(parts[1]), Integer.parseInt(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete RecordVideo values!");
		}
		case StreamVideo -> {
		    if (parts.length >= 2)
			this.cmdObject = new StreamVideo(parts[0], PowerState.parseState(parts[1]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete StreamVideo values!");
		}
		case SetCameraParameters -> {
		    if (parts.length >= 3)
			this.cmdObject = new SetCameraParameters(parts[0], Double.parseDouble(parts[1]), CameraMode.valueOf(parts[2].toUpperCase()));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetCameraParameters values!");
		}
		case SetOrientationOfCamera -> {
		    if (parts.length >= 3)
			this.cmdObject = new SetOrientationOfCamera(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetOrientationOfCamera values!");
		}

		// --- 40 Group: Actuatorics ---
		case ActuatorCheck -> {
		    if (parts.length >= 1)
			this.cmdObject = new ActuatorCheck(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ActuatorCheck values!");
		}
		case SetOrientationOfActuator -> {
		    if (parts.length >= 3)
			this.cmdObject = new SetOrientationOfActuator(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetOrientationOfActuator values!");
		}
		case ActuatorPickUpObject -> {
		    if (parts.length >= 1)
			this.cmdObject = new ActuatorPickUpObject(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ActuatorPickUpObject values!");
		}
		case ActuatorReleaseObject -> {
		    if (parts.length >= 1)
			this.cmdObject = new ActuatorReleaseObject(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete ActuatorReleaseObject values!");
		}

		// --- 50 Group: Combat & Arming ---
		case PreArmCheck -> {
		    if (parts.length >= 1)
			this.cmdObject = new PreArmCheck(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete PreArmCheck values!");
		}
		case Arm -> {
		    if (parts.length >= 1)
			this.cmdObject = new Arm(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Arm values!");
		}
		case Disarm -> {
		    if (parts.length >= 1)
			this.cmdObject = new Disarm(parts[0]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete Disarm values!");
		}
		case SetOrientationOfWeapon -> {
		    if (parts.length >= 3)
			this.cmdObject = new SetOrientationOfWeapon(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete SetOrientationOfWeapon values!");
		}
		case StartEngagement -> {
		    if (parts.length >= 2)
			this.cmdObject = new StartEngagement(parts[0], parts[1]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete StartEngagement values!");
		}
		case HoldEngagement -> {
		    if (parts.length >= 2)
			this.cmdObject = new HoldEngagement(parts[0], parts[1]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete HoldEngagement values!");
		}
		case StopEngagement -> {
		    if (parts.length >= 2)
			this.cmdObject = new StopEngagement(parts[0], parts[1]);
		    else
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "COMMAND", "COMMAND(Iterator<String> message)", "Incomplete StopEngagement values!");
		}

		case SanitizeSystem -> this.cmdObject = new SanitizeSystem();
		case SelfDestruction -> this.cmdObject = new SelfDestruction();
		case GenericAction -> this.cmdObject = new GenericAction(parts.length > 0 ? String.join(";", strList) : "unknown");

		default -> SEDAPExpressMessage.logger.logp(Level.WARNING, "COMMAND", "COMMAND", "Unknown CommandType!");
		}
	    }

	}

    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof COMMAND)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.recipient == null) && (((COMMAND) obj).recipient == null)) || ((this.recipient != null) && this.recipient.equals(((COMMAND) obj).recipient))) &&

		    (this.cmdFlag.equals(((COMMAND) obj).cmdFlag)) &&

		    (this.cmdExTime == (((COMMAND) obj).cmdExTime)) &&

		    (this.cmdType == (((COMMAND) obj).cmdType)) &&

		    (((this.cmdObject == null) && (((COMMAND) obj).cmdObject == null))
			    || ((this.cmdObject != null) && this.cmdObject.equals(((COMMAND) obj).cmdObject)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.cmdId != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdId) : "")
		.append(";").append((this.cmdFlag != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdFlag.getFlagValue()) : "").append(";")
		.append((this.cmdExTime != null && this.cmdExTime != 0) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdExTime, 12) : "").append(";")
		.append((this.cmdType != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdType.getTypeValue()) : "").append(";")
		.append((this.cmdObject != null) ? SEDAPExpressMessage.objectToCSV(this.cmdObject) : "").toString());
    }

}
