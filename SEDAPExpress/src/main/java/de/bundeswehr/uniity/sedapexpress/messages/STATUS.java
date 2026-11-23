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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * Represents or provides the status functionality.
 * 
 * @author Volker Voß
 *
 */
public class STATUS extends SEDAPExpressMessage {

	private static final long serialVersionUID = -6681575300891302102L;

/**
 * Represents or provides the status functionality.
 */
	public enum TechnicalState {

		Off_absent(0), Initializing(1), Degraded(2), Operational(3), Fault(4);

		int state;

	/**
	 * Returns the state.
	 * @return the state
	 */
		public int getState() {
			return this.state;
		}

		TechnicalState(int state) {
			this.state = state;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param state value for state
	 * @return the result of the operation
	 */
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

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
		@Override
		public String toString() {
			return String.valueOf(this.state);
		}
	}

/**
 * Represents or provides the status functionality.
 */
	public enum OperationalState {

		Not_operational(0), Degraded(1), Operational(2), Operational_semi_autonomous(3), Operational_autonomous(4);

		int state;

		OperationalState(int state) {
			this.state = state;
		}

	/**
	 * Returns the state.
	 * @return the state
	 */
		public int getState() {
			return this.state;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param state value for state
	 * @return the result of the operation
	 */
		public static OperationalState valueOfOperationalState(int state) {

			return switch (state) {
			case 0 -> Not_operational;
			case 1 -> Degraded;
			case 2 -> Operational;
			case 3 -> Operational_semi_autonomous;
			case 4 -> Operational_autonomous;
			default -> Not_operational;
			};
		}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
		@Override
		public String toString() {
			return String.valueOf(this.state);
		}
	}

/**
 * Represents or provides the status functionality.
 */
	public enum CommandState {

		Undefined(0), Executed_successfully(1), Partially_executed_successfully(2), Executed_not_successfully(3), Execution_not_possible(4), Will_execute_at(5);

		int state;

	/**
	 * Returns the state.
	 * @return the state
	 */
		public int getState() {
			return this.state;
		}

		CommandState(int status) {
			this.state = status;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param state value for state
	 * @return the result of the operation
	 */
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

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
		@Override
		public String toString() {
			return String.valueOf(this.state);
		}
	}

	private TechnicalState tecState;
	private OperationalState opsState;

	private List<Double> ammunitionLevels;
	private List<Double> fuelLevels;
	private List<Double> batteryLevels;
	private List<Double> storageLevels;

	private List<String> ammunitionLevelNames;
	private List<String> fuelLevelNames;
	private List<String> batteryLevelNames;
	private List<String> storageLevelNames;

	private Integer cmdId;
	private CommandState cmdState;

	private String hostname;

	private List<String> mediaUrls;

	private String freeText;

	/**
	 * Returns the tec state.
	 * @return the tec state
	 */
	public TechnicalState getTecState() {
		return this.tecState;
	}

	/**
	 * Sets the tec state.
	 * @param tecState value for tec state
	 */
	public void setTecState(TechnicalState tecState) {
		this.tecState = tecState;
	}

	/**
	 * Returns the ops state.
	 * @return the ops state
	 */
	public OperationalState getOpsState() {
		return this.opsState;
	}

	/**
	 * Sets the ops state.
	 * @param opsState value for ops state
	 */
	public void setOpsState(OperationalState opsState) {
		this.opsState = opsState;
	}

	/**
	 * Returns the ammunition levels.
	 * @return the ammunition levels
	 */
	public List<Double> getAmmunitionLevels() {
		return this.ammunitionLevels;
	}

	/**
	 * Sets the ammunition levels.
	 * @param ammunitionLevels value for ammunition levels
	 */
	public void setAmmunitionLevels(List<Double> ammunitionLevels) {
		this.ammunitionLevels = ammunitionLevels;
	}

	/**
	 * Returns the fuel levels.
	 * @return the fuel levels
	 */
	public List<Double> getFuelLevels() {
		return this.fuelLevels;
	}

	/**
	 * Sets the fuel levels.
	 * @param fuelLevels value for fuel levels
	 */
	public void setFuelLevels(List<Double> fuelLevels) {
		this.fuelLevels = fuelLevels;
	}

	/**
	 * Returns the battery levels.
	 * @return the battery levels
	 */
	public List<Double> getBatteryLevels() {
		return this.batteryLevels;
	}

	/**
	 * Sets the battery levels.
	 * @param batteryLevels value for battery levels
	 */
	public void setBatteryLevels(List<Double> batteryLevels) {
		this.batteryLevels = batteryLevels;
	}

	/**
	 * Returns the ammunition level names.
	 * @return the ammunition level names
	 */
	public List<String> getAmmunitionLevelNames() {
		return this.ammunitionLevelNames;
	}

	/**
	 * Sets the ammunition level names.
	 * @param ammunitionLevelNames value for ammunition level names
	 */
	public void setAmmunitionLevelNames(List<String> ammunitionLevelNames) {
		this.ammunitionLevelNames = ammunitionLevelNames;
	}

	/**
	 * Returns the fuel level names.
	 * @return the fuel level names
	 */
	public List<String> getFuelLevelNames() {
		return this.fuelLevelNames;
	}

	/**
	 * Sets the fuel level names.
	 * @param fuelLevelNames value for fuel level names
	 */
	public void setFuelLevelNames(List<String> fuelLevelNames) {
		this.fuelLevelNames = fuelLevelNames;
	}

	/**
	 * Returns the battery level names.
	 * @return the battery level names
	 */
	public List<String> getBatteryLevelNames() {
		return this.batteryLevelNames;
	}

	/**
	 * Sets the battery level names.
	 * @param batteryLevelNames value for battery level names
	 */
	public void setBatteryLevelNames(List<String> batteryLevelNames) {
		this.batteryLevelNames = batteryLevelNames;
	}

	/**
	 * Returns the storage levels.
	 * @return the storage levels
	 */
	public List<Double> getStorageLevels() {
		return this.storageLevels;
	}

	/**
	 * Sets the storage levels.
	 * @param storageLevels value for storage levels
	 */
	public void setStorageLevels(List<Double> storageLevels) {
		this.storageLevels = storageLevels;
	}

	/**
	 * Returns the storage level names.
	 * @return the storage level names
	 */
	public List<String> getStorageLevelNames() {
		return this.storageLevelNames;
	}

	/**
	 * Sets the storage level names.
	 * @param storageLevelNames value for storage level names
	 */
	public void setStorageLevelNames(List<String> storageLevelNames) {
		this.storageLevelNames = storageLevelNames;
	}

	/**
	 * Returns the cmd id.
	 * @return the cmd id
	 */
	public Integer getCmdId() {
		return this.cmdId;
	}

	/**
	 * Sets the cmd id.
	 * @param cmdId value for cmd id
	 */
	public void setCmdId(Integer cmdId) {
		this.cmdId = cmdId;
	}

	/**
	 * Returns the cmd state.
	 * @return the cmd state
	 */
	public CommandState getCmdState() {
		return this.cmdState;
	}

	/**
	 * Sets the cmd state.
	 * @param cmdState value for cmd state
	 */
	public void setCmdState(CommandState cmdState) {
		this.cmdState = cmdState;
	}

	/**
	 * Returns the hostname.
	 * @return the hostname
	 */
	public String getHostname() {
		return this.hostname;
	}

	/**
	 * Sets the hostname.
	 * @param hostname value for hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Returns the media urls.
	 * @return the media urls
	 */
	public List<String> getMediaUrls() {
		return this.mediaUrls;
	}

	/**
	 * Sets the media urls.
	 * @param mediaUrls value for media urls
	 */
	public void setMediaUrls(List<String> mediaUrls) {
		this.mediaUrls = mediaUrls;
	}

	/**
	 * Returns the free text.
	 * @return the free text
	 */
	public String getFreeText() {
		return this.freeText;
	}

	/**
	 * Sets the free text.
	 * @param freeText value for free text
	 */
	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	/**
	 * Instantiate a new default STATUS message
	 */
	public STATUS() {

		super(null, null, null, null, null, null);

		this.tecState = null;
		this.opsState = null;
		this.ammunitionLevels = null;
		this.fuelLevels = null;
		this.batteryLevels = null;
		this.ammunitionLevelNames = null;
		this.fuelLevelNames = null;
		this.batteryLevelNames = null;
		this.cmdId = null;
		this.cmdState = null;
		this.hostname = null;
		this.mediaUrls = null;
		this.freeText = null;
	}

	/**
	 * Instantiate a new STATUS message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param tecState value for tec state
	 * @param opsState value for ops state
	 * @param ammunitionLevelNames value for ammunition level names
	 * @param ammunitionLevels value for ammunition levels
	 * @param fuelLevelNames value for fuel level names
	 * @param fuelLevels value for fuel levels
	 * @param batterieLevelNames value for batterie level names
	 * @param batterieLevels value for batterie levels
	 * @param cmdId value for cmd id
	 * @param cmdState value for cmd state
	 * @param hostname value for hostname
	 * @param mediaUrls value for media urls
	 * @param freeText value for free text
	 */
	public STATUS(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState,
			String[] ammunitionLevelNames, Double[] ammunitionLevels, String[] fuelLevelNames, Double[] fuelLevels, String[] batterieLevelNames, Double[] batterieLevels, Integer cmdId,
			CommandState cmdState, String hostname, String mediaUrls, String freeText) {

		this(number, time, sender, classification, acknowledgement, mac, tecState, opsState, Arrays.asList(ammunitionLevelNames), Arrays.asList(ammunitionLevels), Arrays.asList(fuelLevelNames),
				Arrays.asList(fuelLevels), Arrays.asList(batterieLevelNames), Arrays.asList(batterieLevels), cmdId, cmdState, hostname, Arrays.asList(mediaUrls), freeText);
	}

	/**
	 * Instantiate a new STATUS message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param tecState value for tec state
	 * @param opsState value for ops state
	 * @param ammunitionLevelName value for ammunition level name
	 * @param ammunitionLevel value for ammunition level
	 * @param fuelLevelName value for fuel level name
	 * @param fuelLevel value for fuel level
	 * @param batterieLevelName value for batterie level name
	 * @param batterieLevel value for batterie level
	 * @param cmdId value for cmd id
	 * @param cmdState value for cmd state
	 * @param hostname value for hostname
	 * @param mediaUrls value for media urls
	 * @param freeText value for free text
	 */
	public STATUS(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState,
			String ammunitionLevelName, Double ammunitionLevel, String fuelLevelName, Double fuelLevel, String batterieLevelName, Double batterieLevel, Integer cmdId, CommandState cmdState,
			String hostname, String mediaUrls, String freeText) {

		this(number, time, sender, classification, acknowledgement, mac, tecState, opsState, Arrays.asList(ammunitionLevelName), Arrays.asList(ammunitionLevel), Arrays.asList(fuelLevelName),
				Arrays.asList(fuelLevel), Arrays.asList(batterieLevelName), Arrays.asList(batterieLevel), cmdId, cmdState, hostname, Arrays.asList(mediaUrls), freeText);
	}

	/**
	 * Instantiate a new STATUS message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param tecState value for tec state
	 * @param opsState value for ops state
	 * @param ammunitionLevelNames value for ammunition level names
	 * @param ammunitionLevels value for ammunition levels
	 * @param fuelLevelNames value for fuel level names
	 * @param fuelLevels value for fuel levels
	 * @param batterieLevelNames value for batterie level names
	 * @param batterieLevels value for batterie levels
	 * @param cmdId value for cmd id
	 * @param cmdState value for cmd state
	 * @param hostname value for hostname
	 * @param mediaUrls value for media urls
	 * @param freeText value for free text
	 */
	public STATUS(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, TechnicalState tecState, OperationalState opsState,
			List<String> ammunitionLevelNames, List<Double> ammunitionLevels, List<String> fuelLevelNames, List<Double> fuelLevels, List<String> batterieLevelNames, List<Double> batterieLevels,
			Integer cmdId, CommandState cmdState, String hostname, List<String> mediaUrls, String freeText) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.tecState = tecState;
		this.opsState = opsState;
		this.ammunitionLevels = ammunitionLevels;
		this.fuelLevels = fuelLevels;
		this.batteryLevels = batterieLevels;
		this.ammunitionLevelNames = ammunitionLevelNames;
		this.fuelLevelNames = fuelLevelNames;
		this.batteryLevelNames = batterieLevelNames;
		this.cmdId = cmdId;
		this.cmdState = cmdState;
		this.hostname = hostname;
		this.mediaUrls = mediaUrls;
		this.freeText = freeText;
	}

	/**
	 * Instantiate a new STATUS message from a serialized message
	 *
	 * @param message value for message
	 */
	public STATUS(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
	 * Instantiate a new STATUS message from a paramter list
	 *
	 * @param message value for message
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

		// BatteryLevel
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.PERCENT_MATCHER, value)) {

				Iterator<String> it = Arrays.asList(value.split("#")).iterator();
				this.batteryLevelNames = new LinkedList<>();
				this.batteryLevels = new LinkedList<>();
				while (it.hasNext()) {
					this.batteryLevelNames.add(it.next());
					this.batteryLevels.add(Double.parseDouble(it.next()));
				}

			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"batteryLevels\" contains not a valid number!", value);
			}
		}

		// StorageLevel
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.PERCENT_MATCHER, value)) {

				Iterator<String> it = Arrays.asList(value.split("#")).iterator();
				this.storageLevelNames = new LinkedList<>();
				this.storageLevels = new LinkedList<>();
				while (it.hasNext()) {
					this.storageLevelNames.add(it.next());
					this.storageLevels.add(Double.parseDouble(it.next()));
				}

			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"storageLevels\" contains not a valid number!", value);
			}
		}

		// CmdID
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"cmdId\" is empty!");
			} else {
				try {
					this.cmdId = Integer.parseInt(value, 16);
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
					this.hostname = new String(Base64.decode(value), StandardCharsets.ISO_8859_1);
				} catch (DecoderException e) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"hostname\" could not be decoded from Base64 - > \"" + value + "\"!");
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
						this.mediaUrls.add(new String(Base64.decode(url), StandardCharsets.ISO_8859_1));
					}
				} catch (DecoderException e) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"mediaUrls\" could not be decoded from Base64 - > \"" + value + "\"!");
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
					this.freeText = new String(Base64.decode(value), StandardCharsets.ISO_8859_1);
				} catch (DecoderException e) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "STATUS", "STATUS(Iterator<String> message)", "Optional field \"freeText\" could not be decoded from Base64 - > \"" + value + "\"!");
				}
			}
		}

	}

	/**
	 * Compares this instance with the specified object.
	 * @param obj value for obj
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		String thisKey = equalityKey();
		String otherKey = ((SEDAPExpressMessage) obj).equalityKey();
		return thisKey != null && thisKey.equals(otherKey);
	}

	/**
	 * Returns the hash code for this instance.
	 * @return the result of the check
	 */
	@Override
	public int hashCode() {
		String key = equalityKey();
		return (key != null) ? key.hashCode() : System.identityHashCode(this);
	}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
	@Override
	public String toString() {

		String ammunitionStr = "";
		if (this.ammunitionLevelNames != null) {
			Iterator<String> itString = this.ammunitionLevelNames.iterator();
			Iterator<Double> itDouble = this.ammunitionLevels.iterator();
			while (itString.hasNext()) {
				ammunitionStr = "#" + itString.next() + "#" + SEDAPExpressMessage.NumberFormatter.format(itDouble.next());
			}
		}

		String fuelStr = "";
		if (this.fuelLevelNames != null) {
			Iterator<String> itString = this.fuelLevelNames.iterator();
			Iterator<Double> itDouble = this.fuelLevels.iterator();
			while (itString.hasNext()) {
				fuelStr = "#" + itString.next() + "#" + SEDAPExpressMessage.NumberFormatter.format(itDouble.next());
			}
		}

		String batterieStr = "";
		if (this.batteryLevelNames != null) {
			Iterator<String> itString = this.batteryLevelNames.iterator();
			Iterator<Double> itDouble = this.batteryLevels.iterator();
			while (itString.hasNext()) {
				batterieStr = "#" + itString.next() + "#" + SEDAPExpressMessage.NumberFormatter.format(itDouble.next());
			}
		}

		String storageStr = "";
		if (this.storageLevelNames != null) {
			Iterator<String> itString = this.storageLevelNames.iterator();
			Iterator<Double> itDouble = this.storageLevels.iterator();
			while (itString.hasNext()) {
				storageStr = "#" + itString.next() + "#" + SEDAPExpressMessage.NumberFormatter.format(itDouble.next());
			}
		}

		StringBuilder urls = new StringBuilder();
		if (this.mediaUrls != null) {
			this.mediaUrls.forEach(entry -> urls.append(Base64.toBase64String(entry.getBytes(StandardCharsets.ISO_8859_1)) + "#"));
		}

		return SEDAPExpressMessage.removeSemicolons(serializeHeader()

				.append((this.tecState != null) ? this.tecState : "").append(";").append((this.opsState != null) ? this.opsState : "").append(";")
				.append(ammunitionStr.isBlank() ? "" : ammunitionStr.substring(1)).append(";").append(fuelStr.isBlank() ? "" : fuelStr.substring(1)).append(";")
				.append(batterieStr.isBlank() ? "" : batterieStr.substring(1)).append(";").append(storageStr.isBlank() ? "" : storageStr.substring(1)).append(";")
				.append((this.cmdId != null) ? Integer.toHexString(this.cmdId).toUpperCase() : "").append(";").append((this.cmdState != null) ? this.cmdState : "").append(";")
				.append((this.hostname != null) ? Base64.toBase64String(this.hostname.getBytes(StandardCharsets.ISO_8859_1)) : "").append(";")
				.append((this.mediaUrls != null && !this.mediaUrls.isEmpty()) ? urls.subSequence(0, urls.length() - 1) : "").append(";")
				.append((this.freeText != null) ? Base64.toBase64String(this.freeText.getBytes(StandardCharsets.ISO_8859_1)) : "").toString());
	}
}
