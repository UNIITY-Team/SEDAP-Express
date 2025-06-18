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
package de.bundeswehr.uniity.sedapexpress.messages;

import java.util.HexFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * 
 * @author Volker Voß
 *
 */
public class COMMAND extends SEDAPExpressMessage {

    private static final long serialVersionUID = -5662357406861380560L;

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

	Poweroff((byte) 0x00),
	Restart((byte) 0x01),
	Standby((byte) 0x02),
	Sync_time((byte) 0x03),
	Send_status((byte) 0x04),
	Move((byte) 0x05),
	Rotate((byte) 0x06),
	Loiter((byte) 0x07),
	Scan_area((byte) 0x08),
	Take_photo((byte) 0x09),
	Make_video((byte) 0x0A),
	Live_video((byte) 0x0B),
	Engagement((byte) 0x0C),
	Sanitize((byte) 0xEE),
	Generic_action((byte) 0xFF);

	byte type;

	public byte getTypeValue() {

	    return this.type;
	}

	CommandType(byte type) {
	    this.type = type;
	}

	public static CommandType valueOfCommandType(byte type) {

	    return switch (type) {
	    case 0 -> Poweroff;
	    case 1 -> Restart;
	    case 2 -> Standby;
	    case 3 -> Sync_time;
	    case 4 -> Send_status;
	    case 5 -> Move;
	    case 6 -> Rotate;
	    case 7 -> Loiter;
	    case 8 -> Scan_area;
	    case 9 -> Take_photo;
	    case 10 -> Make_video;
	    case 11 -> Live_video;
	    case 12 -> Engagement;
	    case (byte) 0xFF -> Generic_action;
	    default -> Generic_action;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.type);
	}

    }

    public static final String CMDTYPE_ENGAGEMENT_CMD_Start = "start-engagement";
    public static final String CMDTYPE_ENGAGEMENT_CMD_Hold = "hold-engagement";
    public static final String CMDTYPE_ENGAGEMENT_CMD_Stop = "stop-engagement";

    private String recipient;

    private Short cmdId;

    private CommandFlag cmdFlag;

    private CommandType cmdType;

    private List<String> cmdTypeDependentParameters;

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

    public CommandType getCmdType() {
	return this.cmdType;
    }

    public void setCmdType(CommandType cmdType) {
	this.cmdType = cmdType;
    }

    public List<String> getCmdTypeDependentParameters() {
	return this.cmdTypeDependentParameters;
    }

    public void setCmdTypeDependentParameters(List<String> cmdTypeDependentParameters) {
	this.cmdTypeDependentParameters = cmdTypeDependentParameters;
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
     * @param cmdType
     * @param cmdTypeDependentParameters
     */
    public COMMAND(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, Short cmdId, CommandFlag cmdFlag, CommandType cmdType,
	    List<String> cmdTypeDependentParameters) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.cmdId = cmdId;
	this.cmdFlag = cmdFlag;
	this.cmdType = cmdType;
	this.cmdTypeDependentParameters = cmdTypeDependentParameters;
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

	// CmdTypeDependentParameters
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "COMMAND", "COMMAND(Iterator<String> message)", "Optional field \"cmdTypeDependentParameters\" is empty!");
	    } else {
		// Put all remaining elements into the list
		this.cmdTypeDependentParameters = new LinkedList<>();
		this.cmdTypeDependentParameters.add(value);
		while (message.hasNext()) {
		    this.cmdTypeDependentParameters.add(message.next());
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

		    (this.cmdType == (((COMMAND) obj).cmdType)) &&

		    (((this.cmdTypeDependentParameters == null) && (((COMMAND) obj).cmdTypeDependentParameters == null))
			    || ((this.cmdTypeDependentParameters != null) && this.cmdTypeDependentParameters.equals(((COMMAND) obj).cmdTypeDependentParameters)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	if (this.cmdTypeDependentParameters != null) {

	    StringBuilder parameters = new StringBuilder();
	    this.cmdTypeDependentParameters.forEach(entry -> parameters.append(entry + ";"));

	    return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.cmdId != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdId) : "")
		    .append(";").append((this.cmdFlag != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdFlag.getFlagValue()) : "").append(";")
		    .append((this.cmdType != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.cmdType.getTypeValue()) : "").append(";").append((this.cmdTypeDependentParameters != null) ? parameters : "").toString());
	} else {
	    return SEDAPExpressMessage.removeSemicolons(
		    serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.cmdId != null) ? this.cmdId : "").append(";").append((this.cmdType != null) ? this.cmdType : "").toString());
	}

    }

}
