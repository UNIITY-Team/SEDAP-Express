/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024 MESE POC: Volker Voß, Federal Armed Forces of Germany
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
 * CONTRIBUTORS BEn LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.mese.sedapexpress.messages;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

public class STATUS extends SEDAPExpressMessage {

    private static final long serialVersionUID = -6681575300891302102L;

    public enum TechnicalState {

	Off_absent(0), Initializing(1), Degraded(2), Operational(3), Fault(4);

	int state;

	public int getState() {
	    return this.state;
	}

	TechnicalState(int state) {
	    this.state = state;
	}

	public static TechnicalState valueOfTechnicalState(int state) {

	    return switch (state) {
	    case 0 -> Off_absent;
	    case 1 -> Initializing;
	    case 2 -> Degraded;
	    case 3 -> Operational;
	    case 4 -> Fault;
	    default -> Off_absent;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.state);
	}
    }

    public enum OperationalState {

	Not_operational(0), Degraded(1), Operational(2);

	int state;

	OperationalState(int state) {
	    this.state = state;
	}

	public int getState() {
	    return this.state;
	}

	public static OperationalState valueOfOperationalState(int state) {

	    return switch (state) {
	    case 0 -> Not_operational;
	    case 1 -> Degraded;
	    case 2 -> Operational;
	    default -> Not_operational;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.state);
	}
    }

    public enum CommandState {

	Undefined(0), Executed_successfully(1), Partially_executed_successfully(2), Executed_not_successfully(3), Execution_not_possible(4), Will_execute_at(5);

	int state;

	public int getState() {
	    return this.state;
	}

	CommandState(int status) {
	    this.state = status;
	}

	public static CommandState valueOfMessageType(int state) {

	    return switch (state) {
	    case 0 -> Undefined;
	    case 1 -> Executed_successfully;
	    case 2 -> Partially_executed_successfully;
	    case 3 -> Executed_not_successfully;
	    case 4 -> Execution_not_possible;
	    case 5 -> Will_execute_at;
	    default -> Undefined;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.state);
	}
    }

    private TechnicalState tecState;
    private OperationalState opsState;

    private List<Double> ammunitionLevels;
    private List<Double> fuelLevels;
    private List<Double> batterieLevels;

    private List<String> ammunitionLevelNames;
    private List<String> fuelLevelNames;
    private List<String> batterieLevelNames;

    private Integer cmdId;
    private CommandState cmdState;

    private String hostname;

    private List<String> mediaUrls;

    private String freeText;

    public TechnicalState getTecState() {
	return this.tecState;
    }

    public void setTecState(TechnicalState tecState) {
	this.tecState = tecState;
    }

    public OperationalState getOpsState() {
	return this.opsState;
    }

    public void setOpsState(OperationalState opsState) {
	this.opsState = opsState;
    }

    public List<Double> getAmmunitionLevels() {
	return this.ammunitionLevels;
    }

    public void setAmmunitionLevels(List<Double> ammunitionLevels) {
	this.ammunitionLevels = ammunitionLevels;
    }

    public List<Double> getFuelLevels() {
	return this.fuelLevels;
    }

    public void setFuelLevels(List<Double> fuelLevels) {
	this.fuelLevels = fuelLevels;
    }

    public List<Double> getBatterieLevels() {
	return this.batterieLevels;
    }

    public void setBatterieLevels(List<Double> batterieLevels) {
	this.batterieLevels = batterieLevels;
    }

    public List<String> getAmmunitionLevelNames() {
	return this.ammunitionLevelNames;
    }

    public void setAmmunitionLevelNames(List<String> ammunitionLevelNames) {
	this.ammunitionLevelNames = ammunitionLevelNames;
    }

    public List<String> getFuelLevelNames() {
	return this.fuelLevelNames;
    }

    public void setFuelLevelNames(List<String> fuelLevelNames) {
	this.fuelLevelNames = fuelLevelNames;
    }

    public List<String> getBatterieLevelNames() {
	return this.batterieLevelNames;
    }

    public void setBatterieLevelNames(List<String> batterieLevelNames) {
	this.batterieLevelNames = batterieLevelNames;
    }

    public Integer getCmdId() {
	return this.cmdId;
    }

    public void setCmdId(Integer cmdId) {
	this.cmdId = cmdId;
    }

    public CommandState getCmdState() {
	return this.cmdState;
    }

    public void setCmdState(CommandState cmdState) {
	this.cmdState = cmdState;
    }

    public String getHostname() {
	return this.hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public List<String> getMediaUrls() {
	return this.mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
	this.mediaUrls = mediaUrls;
    }

    public String getFreeText() {
	return this.freeText;
    }

    public void setFreeText(String freeText) {
	this.freeText = freeText;
    }

    /**
     * Instantiate a new STATUS message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param tecState
     * @param opsState
     * @param ammunitionLevelNames
     * @param ammunitionLevels
     * @param fuelLevelNames
     * @param fuelLevels
     * @param batterieLevelNames
     * @param batterieLevels
     * @param cmdId
     * @param cmdState
     * @param hostname
     * @param mediaUrls
     * @param freeText
     */
    public STATUS(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState, String[] ammunitionLevelNames,
	    Double[] ammunitionLevels, String[] fuelLevelNames, Double[] fuelLevels, String[] batterieLevelNames, Double[] batterieLevels, Integer cmdId, CommandState cmdState, String hostname, String mediaUrls, String freeText) {

	this(number, time, sender, classification, acknowledgement, mac, tecState, opsState, Arrays.asList(ammunitionLevelNames), Arrays.asList(ammunitionLevels), Arrays.asList(fuelLevelNames), Arrays.asList(fuelLevels),
		Arrays.asList(batterieLevelNames), Arrays.asList(batterieLevels), cmdId, cmdState, hostname, Arrays.asList(mediaUrls), freeText);
    }

    /**
     * Instantiate a new STATUS message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param tecState
     * @param opsState
     * @param ammunitionLevelName
     * @param ammunitionLevel
     * @param fuelLevelName
     * @param fuelLevel
     * @param batterieLevelName
     * @param batterieLevel
     * @param cmdId
     * @param cmdState
     * @param hostname
     * @param mediaUrls
     * @param freeText
     */
    public STATUS(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState, String ammunitionLevelName, Double ammunitionLevel,
	    String fuelLevelName, Double fuelLevel, String batterieLevelName, Double batterieLevel, Integer cmdId, CommandState cmdState, String hostname, String mediaUrls, String freeText) {

	this(number, time, sender, classification, acknowledgement, mac, tecState, opsState, Arrays.asList(ammunitionLevelName), Arrays.asList(ammunitionLevel), Arrays.asList(fuelLevelName), Arrays.asList(fuelLevel),
		Arrays.asList(batterieLevelName), Arrays.asList(batterieLevel), cmdId, cmdState, hostname, Arrays.asList(mediaUrls), freeText);
    }

    /**
     * Instantiate a new STATUS message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param tecState
     * @param opsState
     * @param ammunitionLevelNames
     * @param ammunitionLevels
     * @param fuelLevelNames
     * @param fuelLevels
     * @param batterieLevelNames
     * @param batterieLevels
     * @param cmdId
     * @param cmdState
     * @param hostname
     * @param mediaUrls
     * @param freeText
     */
    public STATUS(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState, List<String> ammunitionLevelNames,
	    List<Double> ammunitionLevels, List<String> fuelLevelNames, List<Double> fuelLevels, List<String> batterieLevelNames, List<Double> batterieLevels, Integer cmdId, CommandState cmdState, String hostname, List<String> mediaUrls,
	    String freeText) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.tecState = tecState;
	this.opsState = opsState;
	this.ammunitionLevels = ammunitionLevels;
	this.fuelLevels = fuelLevels;
	this.batterieLevels = batterieLevels;
	this.ammunitionLevelNames = ammunitionLevelNames;
	this.fuelLevelNames = fuelLevelNames;
	this.batterieLevelNames = batterieLevelNames;
	this.cmdId = cmdId;
	this.cmdState = cmdState;
	this.hostname = hostname;
	this.mediaUrls = mediaUrls;
	this.freeText = freeText;
    }

    /**
     * Instantiate a new STATUS message from a serialized message
     *
     * @param message
     */
    public STATUS(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     * Instantiate a new STATUS message from a paramter list
     *
     * @param message
     */
    public STATUS(Iterator<String> message) {

	super(message);

	String value;

	// TecState
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.TECSTATUS_MATCHER, value)) {
		this.tecState = TechnicalState.valueOfTechnicalState(Integer.parseInt(value));
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"tecState\" contains not a valid number!", value);
	    }
	}

	// OpsState
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.OPSSTATUS_MATCHER, value)) {
		this.opsState = OperationalState.valueOfOperationalState(Integer.parseInt(value));
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"opsState\" contains not a valid number!", value);
	    }
	}

	// AmmunitionLevel
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.PERCENT_MATCHER, value)) {

		Iterator<String> it = Arrays.asList(value.split("#")).iterator();
		this.ammunitionLevelNames = new LinkedList<>();
		this.ammunitionLevels = new LinkedList<>();
		while (it.hasNext()) {
		    this.ammunitionLevelNames.add(it.next());
		    this.ammunitionLevels.add(Double.parseDouble(it.next()));
		}
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"ammunitionLevels\" contains not a valid number!", value);
	    }
	}

	// FuelLevel
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.PERCENT_MATCHER, value)) {

		Iterator<String> it = Arrays.asList(value.split("#")).iterator();
		this.fuelLevelNames = new LinkedList<>();
		this.fuelLevels = new LinkedList<>();
		while (it.hasNext()) {
		    this.fuelLevelNames.add(it.next());
		    this.fuelLevels.add(Double.parseDouble(it.next()));
		}

	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"fuelLevels\" contains not a valid number!", value);
	    }
	}

	// BatterieLevel
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.PERCENT_MATCHER, value)) {

		Iterator<String> it = Arrays.asList(value.split("#")).iterator();
		this.batterieLevelNames = new LinkedList<>();
		this.batterieLevels = new LinkedList<>();
		while (it.hasNext()) {
		    this.batterieLevelNames.add(it.next());
		    this.batterieLevels.add(Double.parseDouble(it.next()));
		}

	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"batterieLevels\" contains not a valid number!", value);
	    }
	}

	// CmdID
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"cmdId\" is empty!");
	    } else {
		try {
		    this.cmdId = Integer.valueOf(value);
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"cmdId\" contains not a valid number!" + value);
		}
	    }
	}

	// CmdState
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"cmdState\" is empty!");
	    } else {
		try {
		    this.cmdState = CommandState.valueOfMessageType(Integer.parseInt(value));
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"cmdState\" contains not a valid number!", value);
		}
	    }
	}

	// Hostname
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"hostname\" is empty!");
	    } else {
		try {
		    this.hostname = new String(Base64.decode(value));
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"hostname\" could not be decoded from Base64!");
		}
	    }
	}

	// Media
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"mediaUrls\" is empty!");
	    } else {
		try {

		    this.mediaUrls = new LinkedList<>();
		    String[] urls = value.split("#");
		    for (String url : urls) {
			this.mediaUrls.add(new String(Base64.decode(url)));
		    }
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"mediaUrls\" could not be decoded from Base64!");
		}
	    }
	}

	// FreeText
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"freeText\" is empty!");
	    } else {
		try {
		    this.freeText = new String(Base64.decode(value));
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"freeText\" could not be decoded from Base64!");
		}
	    }
	}

    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof STATUS)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (this.tecState == ((STATUS) obj).tecState) && (this.opsState == ((STATUS) obj).opsState) &&

		    (this.ammunitionLevelNames.equals(((STATUS) obj).ammunitionLevelNames)) && (this.fuelLevelNames.equals(((STATUS) obj).fuelLevelNames)) && (this.batterieLevelNames.equals(((STATUS) obj).batterieLevelNames)) &&

		    (this.ammunitionLevels.equals(((STATUS) obj).ammunitionLevels)) && (this.fuelLevels.equals(((STATUS) obj).fuelLevels)) && (this.batterieLevels.equals(((STATUS) obj).batterieLevels)) &&

		    (((this.hostname == null) && (((STATUS) obj).hostname == null)) || ((this.hostname != null) && this.hostname.equals(((STATUS) obj).hostname))) &&

		    (((this.mediaUrls == null) && (((STATUS) obj).mediaUrls == null)) || ((this.mediaUrls != null) && this.mediaUrls.equals(((STATUS) obj).mediaUrls))) &&

		    (((this.freeText == null) && (((STATUS) obj).freeText == null)) || ((this.freeText != null) && this.freeText.equals(((STATUS) obj).freeText)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	String ammunitionStr = "";
	if (this.ammunitionLevelNames != null) {
	    Iterator<String> itString = this.ammunitionLevelNames.iterator();
	    Iterator<Double> itDouble = this.ammunitionLevels.iterator();
	    while (itString.hasNext()) {
		ammunitionStr = "#" + itString.next() + "#" + itDouble.next();
	    }
	}

	String fuelStr = "";
	if (this.fuelLevelNames != null) {
	    Iterator<String> itString = this.fuelLevelNames.iterator();
	    Iterator<Double> itDouble = this.fuelLevels.iterator();
	    while (itString.hasNext()) {
		fuelStr = "#" + itString.next() + "#" + itDouble.next();
	    }
	}

	String batterieStr = "";
	if (this.batterieLevelNames != null) {
	    Iterator<String> itString = this.batterieLevelNames.iterator();
	    Iterator<Double> itDouble = this.batterieLevels.iterator();
	    while (itString.hasNext()) {
		batterieStr = "#" + itString.next() + "#" + itDouble.next();
	    }
	}

	StringBuilder urls = new StringBuilder();
	if (this.mediaUrls != null) {
	    this.mediaUrls.forEach(entry -> urls.append(Base64.toBase64String(entry.getBytes()) + "#"));
	}

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append((this.tecState != null) ? this.tecState : "").append(";").append((this.opsState != null) ? this.opsState : "").append(";").append(ammunitionStr.isBlank() ? "" : ammunitionStr.substring(2)).append(";")
		.append(fuelStr.isBlank() ? "" : fuelStr.substring(2)).append(";").append(batterieStr.isBlank() ? "" : batterieStr.substring(2)).append(";").append((this.cmdId != null) ? this.cmdId : "").append(";")
		.append((this.cmdState != null) ? this.cmdState : "").append(";").append((this.hostname != null) ? Base64.toBase64String(this.hostname.getBytes()) : "").append(";")
		.append((this.mediaUrls != null) ? urls.subSequence(0, urls.length() - 1) : "").append(";").append((this.freeText != null) ? Base64.toBase64String(this.freeText.getBytes()) : "").toString());
    }
}
