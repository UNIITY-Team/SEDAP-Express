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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * 
 * @author Volker Voß
 *
 */
public class CONTACT extends SEDAPExpressMessage {

    private static final long serialVersionUID = 5990524206762624628L;

    public enum Source {

	None(' '), Radar('R'), AIS('A'), IFF('I'), Sonar('S'), EW('E'), Optical('O'), Synthetic('Y'), Manual('M');

	char source;

	public char getSourceValue() {

	    return this.source;
	}

	Source(char source) {
	    this.source = source;
	}

	public static Source valueOfSource(char source) {

	    return switch (source) {
	    case 'R' -> Radar;
	    case 'A' -> AIS;
	    case 'I' -> IFF;
	    case 'S' -> Sonar;
	    case 'E' -> EW;
	    case 'O' -> Optical;
	    case 'Y' -> Synthetic;
	    case 'M' -> Manual;
	    case ' ' -> None;
	    default -> None;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.source);
	}

    }

    private String contactID;

    private DeleteFlag deleteFlag;

    private Double latitude;
    private Double longitude;
    private Double altitude;

    private Double relativeXDistance;
    private Double relativeYDistance;
    private Double relativeZDistance;

    private Double speed;
    private Double course;

    private Double heading;
    private Double roll;
    private Double pitch;

    private Double width;
    private Double length;
    private Double height;

    private String name;
    private Set<Source> source;
    private char[] sidc;
    private String mmsi;
    private String icao;

    private byte[] imageData;

    private String comment;

    public String getContactID() {
	return this.contactID;
    }

    public void setContactID(String contactID) {
	this.contactID = contactID;
    }

    public DeleteFlag getDeleteFlag() {
	return this.deleteFlag;
    }

    public void setDeleteFlag(DeleteFlag deleteFlag) {
	this.deleteFlag = deleteFlag;
    }

    public Double getLatitude() {
	return this.latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    public Double getLongitude() {
	return this.longitude;
    }

    public void setLongitude(Double longitude) {
	this.longitude = longitude;
    }

    public Double getAltitude() {
	return this.altitude;
    }

    public void setAltitude(Double altitude) {
	this.altitude = altitude;
    }

    public Double getRelativeXDistance() {
	return this.relativeXDistance;
    }

    public void setRelativeXDistance(Double relativeXDistance) {
	this.relativeXDistance = relativeXDistance;
    }

    public Double getRelativeYDistance() {
	return this.relativeYDistance;
    }

    public void setRelativeYDistance(Double relativeYDistance) {
	this.relativeYDistance = relativeYDistance;
    }

    public Double getRelativeZDistance() {
	return this.relativeZDistance;
    }

    public void setRelativeZDistance(Double relativeZDistance) {
	this.relativeZDistance = relativeZDistance;
    }

    public Double getSpeed() {
	return this.speed;
    }

    public void setSpeed(Double speed) {
	this.speed = speed;
    }

    public Double getCourse() {
	return this.course;
    }

    public void setCourse(Double course) {
	this.course = course;
    }

    public Double getHeading() {
	return this.heading;
    }

    public void setHeading(Double heading) {
	this.heading = heading;
    }

    public Double getRoll() {
	return this.roll;
    }

    public void setRoll(Double roll) {
	this.roll = roll;
    }

    public Double getPitch() {
	return this.pitch;
    }

    public void setPitch(Double pitch) {
	this.pitch = pitch;
    }

    public Double getWidth() {
	return this.width;
    }

    public void setWidth(Double width) {
	this.width = width;
    }

    public Double getLength() {
	return this.length;
    }

    public void setLength(Double length) {
	this.length = length;
    }

    public Double getHeight() {
	return this.height;
    }

    public void setHeight(Double height) {
	this.height = height;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Set<Source> getSource() {
	return this.source;
    }

    public void setSource(Set<Source> source) {
	this.source = source;
    }

    public char[] getSIDC() {
	return this.sidc;
    }

    public void setSIDC(char[] sidc) {
	this.sidc = sidc;
    }

    public String getMMSI() {
	return this.mmsi;
    }

    public void setMMSI(String mmsi) {
	this.mmsi = mmsi;
    }

    public String getICAO() {
	return this.icao;
    }

    public void setICAO(String icao) {
	this.icao = icao;
    }

    public byte[] getImageData() {
	return this.imageData;
    }

    public void setImageData(byte[] imageData) {
	this.imageData = imageData;
    }

    public String getComment() {
	return this.comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    /**
     * Instantiate a new CONTACT message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param contactID
     * @param deleteFlag
     * @param latitude
     * @param longitude
     * @param altitude
     * @param relativeXDistance
     * @param relativeYDistance
     * @param relativeZDistance
     * @param speed
     * @param course
     * @param heading
     * @param roll
     * @param pitch
     * @param width
     * @param length
     * @param height
     * @param name
     * @param source
     * @param sidc
     * @param mmsi
     * @param icao
     * @param imageData
     * @param comment
     */
    public CONTACT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String contactID, DeleteFlag deleteFlag, Double latitude, Double longitude, Double altitude,
	    Double relativeXDistance, Double relativeYDistance, Double relativeZDistance, Double speed, Double course, Double heading, Double roll, Double pitch, Double width, Double length, Double height, String name, Source source,
	    char[] sidc, String mmsi, String icao, byte[] imageData, String comment) {
	this(number, time, sender, classification, acknowledgement, mac, contactID, deleteFlag, latitude, longitude, altitude, relativeXDistance, relativeYDistance, relativeZDistance, speed, course, heading, roll, pitch, width, length,
		height, name, source.toString(), sidc, mmsi, icao, imageData, comment);

    }

    /**
     * 
     * Instantiate a new CONTACT message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param contactID
     * @param deleteFlag
     * @param latitude
     * @param longitude
     * @param altitude
     * @param relativeXDistance
     * @param relativeYDistance
     * @param relativeZDistance
     * @param speed
     * @param course
     * @param heading
     * @param roll
     * @param pitch
     * @param width
     * @param length
     * @param height
     * @param name
     * @param source
     * @param sidc
     * @param mmsi
     * @param icao
     * @param imageData
     * @param comment
     */
    public CONTACT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String contactID, DeleteFlag deleteFlag, Double latitude, Double longitude, Double altitude,
	    Double relativeXDistance, Double relativeYDistance, Double relativeZDistance, Double speed, Double course, Double heading, Double roll, Double pitch, Double width, Double length, Double height, String name, String source,
	    char[] sidc, String mmsi, String icao, byte[] imageData, String comment) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.contactID = contactID;
	this.deleteFlag = deleteFlag;
	this.latitude = latitude;
	this.longitude = longitude;
	this.altitude = altitude;
	this.relativeXDistance = relativeXDistance;
	this.relativeYDistance = relativeYDistance;
	this.relativeZDistance = relativeZDistance;
	this.speed = speed;
	this.course = course;
	this.heading = heading;
	this.roll = roll;
	this.pitch = pitch;
	this.width = width;
	this.length = length;
	this.height = height;
	this.name = name;
	this.source = new HashSet<Source>();
	source.chars().forEach(ch -> this.source.add(Source.valueOfSource((char) ch)));
	this.sidc = sidc;
	this.mmsi = mmsi;
	this.icao = icao;
	this.imageData = imageData;
	this.comment = comment;
    }

    /**
     * Instantiate a new CONTACT message from a serialized message
     *
     * @param message
     */
    public CONTACT(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     * Instantiate a new CONTACT message from a paramter list
     *
     * @param message
     */
    public CONTACT(Iterator<String> message) {

	super(message);

	String value;

	// ContactID
	if (message.hasNext()) {
	    this.contactID = message.next();
	    if (this.contactID.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Mandatory field \"contactID\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Incomplete message!");
	}

	// DeleteFlag
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.YES_NO_FLAG_MATCHER, value)) {
		this.deleteFlag = DeleteFlag.valueOf(value);
	    } else if (value.isBlank()) {
		this.deleteFlag = DeleteFlag.FALSE;
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Mandatory field \"deleteFlag\" invalid value: \"" + value + "\"");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Incomplete message!");
	}

	byte latWarning = 0;
	String latValue = "";
	byte lonWarning = 0;
	String lonValue = "";
	// Latitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		latWarning = 1;
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.latitude = Double.valueOf(value);
	    } else {
		latWarning = 2;
		latValue = value;
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Incomplete message!");
	}

	// Longitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		lonWarning = 1;
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.longitude = Double.valueOf(value);
	    } else {
		lonWarning = 2;
		lonValue = value;
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Incomplete message!");
	}

	// Altitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"altitude\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.altitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"altitude\" contains invalid value!", value);
	    }
	}

	byte relXWarning = 0;
	byte relYWarning = 0;
	byte relZWarning = 0;

	// Relative-X-Distance
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		relXWarning = 1;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeXDistance\" is empty, but Lat/Lon exists!");
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeXDistance\" is empty!");
		}
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.relativeXDistance = Double.valueOf(value);
	    } else {
		relXWarning = 2;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeXDistance\" contains invalid value!", value);
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeXDistance\" contains invalid value!", value);
		}
	    }
	}

	// Relative-Y-Distance
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		relYWarning = 1;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeYDistance\" is empty, but Lat/Lon exists!");
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeYDistance\" is empty!");
		}
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.relativeYDistance = Double.valueOf(value);
	    } else {
		relYWarning = 2;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeYDistance\" contains invalid value!", value);
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeYDistance\" contains invalid value!", value);
		}
	    }
	}

	// Relative-Z-Distance
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		relZWarning = 1;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeZDistance\" is empty, but Lat/Lon exists!");
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Field \"relativeZDistance\" is empty!");
		}
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.relativeZDistance = Double.valueOf(value);
	    } else {
		relZWarning = 2;
		if (latWarning == 0 && lonWarning == 0) {
		    SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeZDistance\" contains invalid value!", value);
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"relativeZDistance\" contains invalid value!", value);
		}
	    }
	}

	// Neither Lat/Lon nor relative position information
	if ((latWarning > 0 || lonWarning > 0) && (relXWarning > 0 || relYWarning > 0 || relZWarning > 0)) {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Neither valid Lat/Lon nor relative position information!");

	} else if (latWarning == 0 && lonWarning == 0 && relXWarning == 0 && relYWarning == 0 && relZWarning == 0) {
	    SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)",
		    "Please specify either longitude/latitude OR relative positions! Relative positions will be ignored if longitude/latitude have been specified.");

	}

	if (relXWarning == 0 && relYWarning == 0 && relZWarning == 0) {

	    if (latWarning == 1) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "field \"latitude\" is empty, but relative position exists!");
	    } else if (latWarning == 2) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "field \"latitude\" contains invalid value, but relative position exists!", latValue);
	    }

	    if (lonWarning == 1) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "field \"longitude\" is empty, but relative position exists!");
	    } else if (lonWarning == 2) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "field \"longitude\" contains invalid value, but relative position exists!", lonValue);
	    }
	} else {

	    if (latWarning == 1) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "field \"latitude\" is empty!");
	    } else if (latWarning == 2) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "field \"latitude\" contains invalid value!", latValue);
	    }

	    if (lonWarning == 1) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "field \"longitude\" is empty!");
	    } else if (lonWarning == 2) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "field \"longitude\" contains invalid value!", lonValue);
	    }
	}

	// Speed
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"speed\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.POSITIVE_DOUBLE_MATCHER, value)) {
		this.speed = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"speed\" contains invalid value!", value);
	    }
	}

	// Course
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"course\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BEARING_MATCHER, value)) {
		this.course = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"course\" contains invalid value!", value);
	    }
	}

	// Heading
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"heading\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BEARING_MATCHER, value)) {
		this.heading = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"heading\" contains invalid value!", value);
	    }
	}

	// Roll
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"roll\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.roll = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"roll\" contains invalid value!", value);
	    }
	}

	// Pitch
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"pitch\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.pitch = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"pitch\" contains invalid value!", value);
	    }
	}

	// Width
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"width\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.POSITIVE_DOUBLE_MATCHER, value)) {
		this.width = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"width\" contains invalid value!", value);
	    }
	}

	// Length
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"length\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.POSITIVE_DOUBLE_MATCHER, value)) {
		this.length = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"length\" contains invalid value!", value);
	    }
	}

	// Height
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"height\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.POSITIVE_DOUBLE_MATCHER, value)) {
		this.height = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"height\" contains invalid value!", value);
	    }
	}

	// Name
	if (message.hasNext()) {
	    this.name = message.next();
	    if (this.name.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"name\" is empty!");
	    }
	}

	// Source
	if (message.hasNext()) {
	    value = message.next().trim();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"source\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.SOURCE_MATCHER, value)) {
		this.source = new HashSet<>();
		value.chars().forEach(ch -> this.source.add(Source.valueOfSource((char) ch)));
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"source\" contains invalid value!", value);
	    }
	}

	// SIDC
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"SIDC\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.SIDC_MATCHER, value)) {
		this.sidc = value.toCharArray();
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"SIDC\" contains invalid value - length: " + value.length() + " bytes!", value);
	    }
	}

	// MMSI
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"MMSI\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.MMSI_MATCHER, value)) {
		this.mmsi = value;
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"MMSI\" contains invalid value!", value);
	    }
	}

	// ICAO
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"ICAO\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.ICAO_MATCHER, value)) {
		this.icao = value;
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"ICAO\" contains invalid value!", value);
	    }
	}

	// ImageData
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"imageData\" is empty!");
	    } else {
		try {
		    this.imageData = Base64.decode(value);
		    if (this.imageData.length > 65000) {
			SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"imageData\" exceeds 32768 bytes!");
		    }
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"imageData\" could not be decoded from Base64!");
		}
	    }
	}

	// Comment
	if (message.hasNext()) {
	    value = message.next();
	    if ((value == null) || value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"comment\" is empty!");
	    } else {
		try {
		    this.comment = new String(Base64.decode(value));
		} catch (DecoderException e) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"comment\" could not be decoded from Base64!");
		}
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof CONTACT)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.contactID == null) && (((CONTACT) obj).contactID == null)) || ((this.contactID != null) && this.contactID.equals(((CONTACT) obj).contactID))) &&

		    (this.deleteFlag == ((CONTACT) obj).deleteFlag) &&

		    (this.latitude == ((CONTACT) obj).latitude) && (this.longitude == ((CONTACT) obj).longitude) && (this.altitude == ((CONTACT) obj).altitude) &&

		    (this.relativeXDistance == ((CONTACT) obj).relativeXDistance) && (this.relativeYDistance == ((CONTACT) obj).relativeYDistance) && (this.relativeZDistance == ((CONTACT) obj).relativeZDistance) &&

		    (this.speed == ((CONTACT) obj).speed) && (this.course == ((CONTACT) obj).course) &&

		    (this.heading == ((CONTACT) obj).heading) && (this.roll == ((CONTACT) obj).roll) && (this.pitch == ((CONTACT) obj).pitch) &&

		    (this.width == ((CONTACT) obj).width) && (this.length == ((CONTACT) obj).length) && (this.height == ((CONTACT) obj).height) &&

		    (((this.name == null) && (((CONTACT) obj).name == null)) || ((this.name != null) && this.name.equals(((CONTACT) obj).name))) &&

		    (((this.source == null) && (((CONTACT) obj).source == null)) || ((this.source != null) && this.source.equals(((CONTACT) obj).source))) &&

		    Arrays.equals(this.sidc, ((CONTACT) obj).sidc) &&

		    (((this.mmsi == null) && (((CONTACT) obj).mmsi == null)) || ((this.mmsi != null) && this.mmsi.equals(((CONTACT) obj).mmsi))) &&

		    (((this.icao == null) && (((CONTACT) obj).icao == null)) || ((this.icao != null) && this.icao.equals(((CONTACT) obj).icao))) &&

		    Arrays.equals(this.imageData, ((CONTACT) obj).imageData) &&

		    (((this.comment == null) && (((CONTACT) obj).comment == null)) || ((this.comment != null) && this.comment.equals(((CONTACT) obj).comment)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	StringBuilder sourceStr = new StringBuilder();
	if (this.source != null)
	    this.source.forEach(s -> sourceStr.append(s));

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append(this.contactID).append(";")

		.append((this.deleteFlag != null) ? this.deleteFlag : "").append(";")

		.append((this.latitude != null) ? SEDAPExpressMessage.numberFormatter.format(this.latitude) : "").append(";").append(this.longitude != null ? SEDAPExpressMessage.numberFormatter.format(this.longitude) : "").append(";")
		.append(this.altitude != null ? SEDAPExpressMessage.numberFormatter.format(this.altitude) : "").append(";")

		.append(this.relativeXDistance != null ? SEDAPExpressMessage.numberFormatter.format(this.relativeXDistance) : "").append(";")
		.append(this.relativeYDistance != null ? SEDAPExpressMessage.numberFormatter.format(this.relativeYDistance) : "").append(";")
		.append(this.relativeZDistance != null ? SEDAPExpressMessage.numberFormatter.format(this.relativeZDistance) : "").append(";")

		.append(this.speed != null ? SEDAPExpressMessage.numberFormatter.format(this.speed) : "").append(";").append(this.course != null ? SEDAPExpressMessage.numberFormatter.format(this.course) : "").append(";")

		.append(this.heading != null ? SEDAPExpressMessage.numberFormatter.format(this.heading) : "").append(";").append(this.roll != null ? SEDAPExpressMessage.numberFormatter.format(this.roll) : "").append(";")
		.append(this.pitch != null ? SEDAPExpressMessage.numberFormatter.format(this.pitch) : "").append(";")

		.append(this.width != null ? SEDAPExpressMessage.numberFormatter.format(this.width) : "").append(";").append(this.length != null ? SEDAPExpressMessage.numberFormatter.format(this.length) : "").append(";")
		.append(this.height != null ? SEDAPExpressMessage.numberFormatter.format(this.height) : "").append(";")

		.append(this.name != null ? this.name : "").append(";").append(!sourceStr.isEmpty() ? sourceStr.toString() : "").append(";")

		.append(this.sidc != null ? String.valueOf(this.sidc) : "").append(";").append(this.mmsi != null ? this.mmsi : "").append(";").append(this.icao != null ? this.icao : "").append(";")

		.append(this.imageData != null ? Base64.toBase64String(this.imageData) : "").append(";")

		.append((this.comment != null) ? Base64.toBase64String(this.comment.getBytes()) : "").toString());
    }

}
