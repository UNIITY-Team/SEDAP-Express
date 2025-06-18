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
import java.util.Iterator;
import java.util.logging.Level;

/**
 * 
 * @author Volker Voß
 *
 */
public class OWNUNIT extends SEDAPExpressMessage {

    private static final long serialVersionUID = -4427343780553810732L;

    private Double latitude;
    private Double longitude;
    private Double altitude;

    private Double speed;
    private Double course;

    private Double heading;
    private Double roll;
    private Double pitch;

    private String name;

    private char[] sidc;

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

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public char[] getSIDC() {
	return this.sidc;
    }

    public void setSIDC(char[] sidc) {
	this.sidc = sidc;
    }

    /**
     * Instantiate a new OWNUNIT message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param latitude
     * @param longitude
     * @param altitude
     * @param speed
     * @param course
     * @param heading
     * @param roll
     * @param pitch
     * @param name
     * @param sidc
     */
    public OWNUNIT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, Double latitude, Double longitude, Double altitude, Double speed, Double course, Double heading,
	    Double roll, Double pitch, String name, char[] sidc) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.latitude = latitude;
	this.longitude = longitude;
	this.altitude = altitude;
	this.speed = speed;
	this.course = course;
	this.heading = heading;
	this.roll = roll;
	this.pitch = pitch;
	this.name = name;
	this.sidc = sidc;
    }

    /**
     * Instantiate a new OWNUNIT message from a serialized message
     *
     * @param message
     */
    public OWNUNIT(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     * Instantiate a new OWNUNIT message from a paramter list
     *
     * @param message
     */
    public OWNUNIT(Iterator<String> message) {

	super(message);

	String value;

	// Latitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Mandatory field \"latitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.latitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Mandatory field \"longitude\" contains invalid value!", value);
	    }
	}

	// Longitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Mandatory field \"longitude\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.longitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Mandatory field \"longitude\" contains invalid value!", value);
	    }
	}

	// Altitude
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"altitude\" is empty!");
	    } else if (!value.isEmpty() && SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.altitude = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"altitude\" contains invalid value!", value);
	    }
	}

	// Speed
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"speed\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.speed = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"speed\" contains invalid value!", value);
	    }
	}

	// Speed
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"course\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.course = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"course\" contains invalid value!", value);
	    }
	}

	// Heading
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"heading\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BEARING_MATCHER, value)) {
		this.heading = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"heading\" contains invalid value!", value);
	    }
	}

	// Roll
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"roll\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.roll = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"roll\" contains invalid value!", value);
	    }
	}

	// Pitch
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"pitch\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
		this.pitch = Double.valueOf(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"pitch\" contains invalid value!", value);
	    }
	}

	// Name
	if (message.hasNext()) {
	    this.name = message.next();
	    if (this.name.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"name\" is empty!");
	    }
	}

	// SIDC
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"SIDC\" is empty!");
	    } else if (value.length() != 15) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"SIDC\" contains invalid value - length: " + value.length() + " bytes!", value);
	    } else {
		this.sidc = value.toCharArray();
	    }
	}

    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof OWNUNIT)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (this.latitude == ((OWNUNIT) obj).latitude) && (this.longitude == ((OWNUNIT) obj).longitude) && (this.altitude == ((OWNUNIT) obj).altitude) &&

		    (this.speed == ((OWNUNIT) obj).speed) && (this.course == ((OWNUNIT) obj).course) &&

		    (this.heading == ((OWNUNIT) obj).heading) && (this.roll == ((OWNUNIT) obj).roll) && (this.pitch == ((OWNUNIT) obj).pitch) &&

		    this.name.equals(((OWNUNIT) obj).name) &&

		    Arrays.equals(this.sidc, ((OWNUNIT) obj).sidc);

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append((this.latitude != null) ? SEDAPExpressMessage.numberFormatter.format(this.latitude) : "").append(";").append(this.longitude != null ? SEDAPExpressMessage.numberFormatter.format(this.longitude) : "").append(";")
		.append(this.altitude != null ? SEDAPExpressMessage.numberFormatter.format(this.altitude) : "").append(";")

		.append(this.speed != null ? SEDAPExpressMessage.numberFormatter.format(this.speed) : "").append(";").append(this.course != null ? SEDAPExpressMessage.numberFormatter.format(this.course) : "").append(";")

		.append(this.heading != null ? SEDAPExpressMessage.numberFormatter.format(this.heading) : "").append(";").append(this.roll != null ? SEDAPExpressMessage.numberFormatter.format(this.roll) : "").append(";")
		.append(this.pitch != null ? SEDAPExpressMessage.numberFormatter.format(this.pitch) : "").append(";")

		.append(this.name != null ? this.name : "").append(";")

		.append(this.sidc != null ? String.valueOf(this.sidc) : "").toString());

    }

}
