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
import java.util.List;
import java.util.logging.Level;

public class EMISSION extends SEDAPExpressMessage {

    private static final long serialVersionUID = -421710238402316680L;

    public static final Integer FREQAGILITY_Stable_Fixed = 0;
    public static final Integer FREQAGILITY_Agile = 1;
    public static final Integer FREQAGILITY_Periodic = 2;
    public static final Integer FREQAGILITY_Hopper = 3;
    public static final Integer FREQAGILITY_Batch_Hopper = 4;
    public static final Integer FREQAGILITY_Unknown = 5;

    public static final Integer PRFAgility_Fixed_periodic = 0;
    public static final Integer PRFAgility_Staggered = 1;
    public static final Integer PRFAgility_Jittered = 2;
    public static final Integer PRFAgility_Wobbulated = 3;
    public static final Integer PRFAgility_Sliding = 4;
    public static final Integer PRFAgility_Dwell_switch = 5;
    public static final Integer PRFAgility_UnknownPRF = 6;
    public static final Integer PRFAgility_CW = 7;

    public static final Integer FUNCTION_ESM_Beacon_Transponder = 1;
    public static final Integer FUNCTION_ESM_Navigation = 2;
    public static final Integer FUNCTION_ESM_Voice_Communication = 3;
    public static final Integer FUNCTION_ESM_Data_Communication = 4;
    public static final Integer FUNCTION_ESM_Radar = 5;
    public static final Integer FUNCTION_ESM_Iff = 6;
    public static final Integer FUNCTION_ESM_Guidance = 7;
    public static final Integer FUNCTION_ESM_Weapon = 8;
    public static final Integer FUNCTION_ESM_Jammer = 9;
    public static final Integer FUNCTION_ESM_Natural = 10;
    public static final Integer FUNCTION_ACOUSTIC_Object = 11;
    public static final Integer FUNCTION_ACOUSTIC_Submarine = 12;
    public static final Integer FUNCTION_ACOUSTIC_Variable_Depth_Sonar = 13;
    public static final Integer FUNCTION_ACOUSTIC_Array_Sonar = 14;
    public static final Integer FUNCTION_ACOUSTIC_Active_Sonar = 15;
    public static final Integer FUNCTION_ACOUSTIC_Torpedo_Sonar = 16;
    public static final Integer FUNCTION_ACOUSTIC_Sono_Buoy = 17;
    public static final Integer FUNCTION_ACOUSTIC_Decoy_Signal = 18;
    public static final Integer FUNCTION_ACOUSTIC_Hit_Noise = 19;
    public static final Integer FUNCTION_ACOUSTIC_Propeller_Noise = 20;
    public static final Integer FUNCTION_ACOUSTIC_Underwater_Telephone = 21;
    public static final Integer FUNCTION_ACOUSTIC_Communication = 22;
    public static final Integer FUNCTION_ACOUSTIC_Noise = 23;
    public static final Integer FUNCTION_LASER_Range_Finder = 24;
    public static final Integer FUNCTION_LASER_Designator = 25;
    public static final Integer FUNCTION_LASER_Beam_Rider = 26;
    public static final Integer FUNCTION_LASER_Dazzler = 27;
    public static final Integer FUNCTION_LASER_Lidar = 28;

    private String emissionID;
    private DeleteFlag deleteFlag;
    private Double sensorLatitude;
    private Double sensorLongitude;
    private Double sensorAltitude;
    private Double emitterLatitude;
    private Double emitterLongitude;
    private Double emitterAltitude;
    private Double bearing;
    private List<Double> frequencies;
    private Double bandwidth;
    private Double power;
    private Integer freqAgility;
    private Integer prfAgility;
    private Integer function;
    private Integer spotNumber;
    private char[] sidc;
    private String comment;

    public String getEmissionID() {
	return this.emissionID;
    }

    public void setEmissionID(String emissionID) {
	this.emissionID = emissionID;
    }

    public DeleteFlag getDeleteFlag() {
	return this.deleteFlag;
    }

    public void setDeleteFlag(DeleteFlag deleteFlag) {
	this.deleteFlag = deleteFlag;
    }

    public Double getSensorLatitude() {
	return this.sensorLatitude;
    }

    public void setSensorLatitude(Double sensorLatitude) {
	this.sensorLatitude = sensorLatitude;
    }

    public Double getSensorLongitude() {
	return this.sensorLongitude;
    }

    public void setSensorLongitude(Double sensorLongitude) {
	this.sensorLongitude = sensorLongitude;
    }

    public Double getSensorAltitude() {
	return this.sensorAltitude;
    }

    public void setSensorAltitude(Double sensorAltitude) {
	this.sensorAltitude = sensorAltitude;
    }

    public Double getEmitterLatitude() {
	return this.emitterLatitude;
    }

    public void setEmitterLatitude(Double emitterLatitude) {
	this.emitterLatitude = emitterLatitude;
    }

    public Double getEmitterLongitude() {
	return this.emitterLongitude;
    }

    public void setEmitterLongitude(Double emitterLongitude) {
	this.emitterLongitude = emitterLongitude;
    }

    public Double getEmitterAltitude() {
	return this.emitterAltitude;
    }

    public void setEmitterAltitude(Double emitterAltitude) {
	this.emitterAltitude = emitterAltitude;
    }

    public Double getBearing() {
	return this.bearing;
    }

    public void setBearing(Double bearing) {
	this.bearing = bearing;
    }

    public List<Double> getFrequency() {
	return this.frequencies;
    }

    public void setFrequency(List<Double> frequency) {
	this.frequencies = frequency;
    }

    public Double getBandwidth() {
	return this.bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
	this.bandwidth = bandwidth;
    }

    public Double getPower() {
	return this.power;
    }

    public void setPower(Double power) {
	this.power = power;
    }

    public Integer getFreqAgility() {
	return this.freqAgility;
    }

    public void setFreqAgility(Integer freqAgility) {
	this.freqAgility = freqAgility;
    }

    public Integer getPrfAgility() {
	return this.prfAgility;
    }

    public void setPrfAgility(Integer prfAgility) {
	this.prfAgility = prfAgility;
    }

    public Integer getFunction() {
	return this.function;
    }

    public void setFunction(Integer function) {
	this.function = function;
    }

    public Integer getSpotNumber() {
	return this.spotNumber;
    }

    public void setSpotNumber(Integer spotNumber) {
	this.spotNumber = spotNumber;
    }

    public char[] getSidc() {
	return this.sidc;
    }

    public void setSidc(char[] sidc) {
	this.sidc = sidc;
    }

    public String getComment() {
	return this.comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    /**
     * Instantiate a new EMISSION message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param emissionID
     * @param deleteFlag
     * @param sensorLatitude
     * @param sensorLongitude
     * @param sensorAltitude
     * @param emitterLatitude
     * @param emitterLongitude
     * @param emitterAltitude
     * @param bearing
     * @param frequency
     * @param bandwidth
     * @param power
     * @param freqAgility
     * @param prfAgility
     * @param function
     * @param spotNumber
     * @param sidc
     * @param comment
     */
    public EMISSION(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String emissionID, DeleteFlag deleteFlag, Double sensorLatitude, Double sensorLongitude,
	    Double sensorAltitude, Double emitterLatitude, Double emitterLongitude, Double emitterAltitude, Double bearing, List<Double> frequency, Double bandwidth, Double power, Integer freqAgility, Integer prfAgility, Integer function,
	    Integer spotNumber, char[] sidc, String comment) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.emissionID = emissionID;
	this.deleteFlag = deleteFlag;
	this.sensorLatitude = sensorLatitude;
	this.sensorLongitude = sensorLongitude;
	this.sensorAltitude = sensorAltitude;
	this.emitterLatitude = emitterLatitude;
	this.emitterLongitude = emitterLongitude;
	this.emitterAltitude = emitterAltitude;
	this.bearing = bearing;
	this.frequencies = frequency;
	this.bandwidth = bandwidth;
	this.power = power;
	this.freqAgility = freqAgility;
	this.prfAgility = prfAgility;
	this.function = function;
	this.spotNumber = spotNumber;
	this.sidc = sidc;
	this.comment = comment;
    }

    /**
     *
     * @param message
     */
    public EMISSION(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
     */
    public EMISSION(Iterator<String> message) {

	super(message);

	String value;

	// EmissionID
	if (message.hasNext()) {
	    this.emissionID = message.next();
	    if (this.emissionID.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"emissionID\" is empty!");
	    }
	}

	// DeleteFlag
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.YES_NO_FLAG_MATCHER, value)) {
		this.deleteFlag = DeleteFlag.valueOf(value);
	    } else if (value.isBlank()) {
		this.deleteFlag = DeleteFlag.FALSE;
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"deleteFlag\" invalid value: \"" + value + "\"");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Incomplete message!");
	}

	// SensorLatitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"sensorLatitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.sensorLatitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"sensorLatitude\" contains invalid value!", value);
	    }
	}

	// SensorLongitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"sensorLongitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.sensorLongitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"sensorLongitude\" contains invalid value!", value);
	    }
	}

	// SensorAltitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"sensorAltitude\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.sensorAltitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"sensorAltitude\" contains invalid value!", value);
	    }
	}

	// EmitterLatitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterLatitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.emitterLatitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterLatitude\" contains invalid value!", value);
	    }
	}

	// EmitterLongitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterLongitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.emitterLongitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterLongitude\" contains invalid value!", value);
	    }
	}

	// EmitterAltitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterAltitude\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.emitterAltitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"emitterAltitude\" contains invalid value!", value);
	    }
	}

	// Bearing
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"bearing\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BEARING_MATCHER, value)) {
		this.bearing = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Mandatory field \"bearing\" contains invalid value!", value);
	    }
	}

	// Frequencies
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"frequencies\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_LIST_MATCHER, value)) {
		this.frequencies = SEDAPExpressMessage.splitDoubleDataHashTag(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"frequencies\" contains invalid value!", value);
	    }

	}

// Bandwidth
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"bandwidth\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.bandwidth = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"bandwidth\" contains invalid value!", value);
	    }
	}

	// Power
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"power\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.power = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"power\" contains invalid value!", value);
	    }
	}

	// FreqAgility
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"freqAgility\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.INTEGER_MATCHER, value)) {
		this.freqAgility = Integer.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"freqAgility\" contains invalid value!", value);
	    }
	}

	// PrfAgility
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"prfAgility\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.INTEGER_MATCHER, value)) {
		this.prfAgility = Integer.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"prfAgility\" contains invalid value!", value);
	    }
	}

	// Function
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"function\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.INTEGER_MATCHER, value)) {
		this.function = Integer.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"function\" contains invalid value!", value);
	    }
	}

	// SpotNumber
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"spotNumber\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.INTEGER_MATCHER, value)) {
		this.spotNumber = Integer.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"spotNumber\" contains invalid value!", value);
	    }
	}

	// SIDC
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"SIDC\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.SIDC_MATCHER, value)) {
		this.sidc = value.toCharArray();
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"SIDC\" contains invalid value - length: " + this.sidc.length + " bytes!", value);
	    }
	}

	// Comment
	if (message.hasNext()) {
	    this.comment = message.next();
	    if (this.comment.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "EMISSION", "EMISSION(Iterator<String> message)", "Optional field \"comment\" is empty!");
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	} else if (!(obj instanceof EMISSION)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.emissionID == null) && (((EMISSION) obj).emissionID == null)) || ((this.emissionID != null) && this.emissionID.equals(((EMISSION) obj).emissionID))) &&

		    (this.deleteFlag == ((EMISSION) obj).deleteFlag) && (this.sensorLatitude == ((EMISSION) obj).sensorLatitude) && (this.sensorLongitude == ((EMISSION) obj).sensorLongitude)
		    && (this.sensorAltitude == ((EMISSION) obj).sensorAltitude) && (this.emitterLatitude == ((EMISSION) obj).emitterLatitude) && (this.emitterLongitude == ((EMISSION) obj).emitterLongitude)
		    && (this.emitterAltitude == ((EMISSION) obj).emitterAltitude) && (this.bearing == ((EMISSION) obj).bearing) && (this.frequencies == ((EMISSION) obj).frequencies) && (this.bandwidth == ((EMISSION) obj).bandwidth)
		    && (this.power == ((EMISSION) obj).power) && (this.freqAgility == ((EMISSION) obj).freqAgility) && (this.prfAgility == ((EMISSION) obj).prfAgility) && (this.function == ((EMISSION) obj).function)
		    && (this.spotNumber == ((EMISSION) obj).spotNumber) && Arrays.equals(this.sidc, ((EMISSION) obj).sidc) &&

		    (((this.comment == null) && (((EMISSION) obj).comment == null)) || ((this.comment != null) && this.comment.equals(((EMISSION) obj).comment)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append((this.emissionID != null) ? this.emissionID : "").append(";")

		.append(this.deleteFlag != null ? this.deleteFlag : "").append(";")

		.append(this.sensorLatitude != null ? SEDAPExpressMessage.numberFormatter.format(this.sensorLatitude) : "").append(";")
		.append(this.sensorLongitude != null ? SEDAPExpressMessage.numberFormatter.format(this.sensorLongitude) : "").append(";")
		.append(this.sensorAltitude != null ? SEDAPExpressMessage.numberFormatter.format(this.sensorAltitude) : "").append(";")

		.append(this.emitterLatitude != null ? SEDAPExpressMessage.numberFormatter.format(this.emitterLatitude) : "").append(";")
		.append(this.emitterLongitude != null ? SEDAPExpressMessage.numberFormatter.format(this.emitterLongitude) : "").append(";")
		.append(this.emitterAltitude != null ? SEDAPExpressMessage.numberFormatter.format(this.emitterAltitude) : "").append(";")

		.append(this.bearing != null ? SEDAPExpressMessage.numberFormatter.format(this.bearing) : "").append(";")

		.append(this.frequencies != null ? getStringFromList(this.frequencies) : "").append(";").append(this.bandwidth != null ? SEDAPExpressMessage.numberFormatter.format(this.bandwidth) : "").append(";")
		.append(this.power != null ? SEDAPExpressMessage.numberFormatter.format(this.power) : "").append(";").append(this.freqAgility != null ? this.freqAgility : "").append(";")
		.append(this.prfAgility != null ? this.prfAgility : "").append(";")

		.append(this.function != null ? this.function : "").append(";")

		.append(this.spotNumber != null ? this.spotNumber : "").append(";")

		.append(this.sidc != null ? String.valueOf(this.sidc) : "").append(";")

		.append(this.comment != null ? this.comment : "").toString());
    }

}
