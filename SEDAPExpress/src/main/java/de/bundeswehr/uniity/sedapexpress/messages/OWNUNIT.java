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

import java.util.Iterator;
import java.util.logging.Level;

/**
 * Represents or provides the ownunit functionality.
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

	/**
	 * Returns the latitude.
	 * @return the latitude
	 */
	public Double getLatitude() {
		return this.latitude;
	}

	/**
	 * Sets the latitude.
	 * @param latitude value for latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the longitude.
	 * @return the longitude
	 */
	public Double getLongitude() {
		return this.longitude;
	}

	/**
	 * Sets the longitude.
	 * @param longitude value for longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Returns the altitude.
	 * @return the altitude
	 */
	public Double getAltitude() {
		return this.altitude;
	}

	/**
	 * Sets the altitude.
	 * @param altitude value for altitude
	 */
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Returns the speed.
	 * @return the speed
	 */
	public Double getSpeed() {
		return this.speed;
	}

	/**
	 * Sets the speed.
	 * @param speed value for speed
	 */
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	/**
	 * Returns the course.
	 * @return the course
	 */
	public Double getCourse() {
		return this.course;
	}

	/**
	 * Sets the course.
	 * @param course value for course
	 */
	public void setCourse(Double course) {
		this.course = course;
	}

	/**
	 * Returns the heading.
	 * @return the heading
	 */
	public Double getHeading() {
		return this.heading;
	}

	/**
	 * Sets the heading.
	 * @param heading value for heading
	 */
	public void setHeading(Double heading) {
		this.heading = heading;
	}

	/**
	 * Returns the roll.
	 * @return the roll
	 */
	public Double getRoll() {
		return this.roll;
	}

	/**
	 * Sets the roll.
	 * @param roll value for roll
	 */
	public void setRoll(Double roll) {
		this.roll = roll;
	}

	/**
	 * Returns the pitch.
	 * @return the pitch
	 */
	public Double getPitch() {
		return this.pitch;
	}

	/**
	 * Sets the pitch.
	 * @param pitch value for pitch
	 */
	public void setPitch(Double pitch) {
		this.pitch = pitch;
	}

	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 * @param name value for name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the sidc.
	 * @return the sidc
	 */
	public char[] getSIDC() {
		return this.sidc;
	}

	/**
	 * Sets the sidc.
	 * @param sidc value for sidc
	 */
	public void setSIDC(char[] sidc) {
		this.sidc = sidc;
	}

	/**
	 * Instantiate a new default OWNUNIT message
	 */
	public OWNUNIT() {

		super(null, null, null, null, null, null);

		this.latitude = null;
		this.longitude = null;
		this.altitude = null;
		this.speed = null;
		this.course = null;
		this.heading = null;
		this.roll = null;
		this.pitch = null;
		this.name = null;
		this.sidc = null;
	}

	/**
	 * Instantiate a new OWNUNIT message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param latitude value for latitude
	 * @param longitude value for longitude
	 * @param altitude value for altitude
	 * @param speed value for speed
	 * @param course value for course
	 * @param heading value for heading
	 * @param roll value for roll
	 * @param pitch value for pitch
	 * @param name value for name
	 * @param sidc value for sidc
	 */
	public OWNUNIT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, Double latitude, Double longitude, Double altitude, Double speed,
			Double course, Double heading, Double roll, Double pitch, String name, char[] sidc) {

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
	 * @param message value for message
	 */
	public OWNUNIT(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
	 * Instantiate a new OWNUNIT message from a paramter list
	 *
	 * @param message value for message
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
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "OWNUNIT", "OWNUNIT(Iterator<String> message)", "Optional field \"SIDC\" contains invalid value - length: " + value.length() + " bytes!",
						value);
			} else {
				this.sidc = value.toUpperCase().toCharArray();
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

		return SEDAPExpressMessage.removeSemicolons(serializeHeader()

				.append((this.latitude != null) ? SEDAPExpressMessage.NumberFormatter.format(this.latitude) : "").append(";")
				.append(this.longitude != null ? SEDAPExpressMessage.NumberFormatter.format(this.longitude) : "").append(";")
				.append(this.altitude != null ? SEDAPExpressMessage.NumberFormatter.format(this.altitude) : "").append(";")

				.append(this.speed != null ? SEDAPExpressMessage.NumberFormatter.format(this.speed) : "").append(";")
				.append(this.course != null ? SEDAPExpressMessage.NumberFormatter.format(this.course) : "").append(";")

				.append(this.heading != null ? SEDAPExpressMessage.NumberFormatter.format(this.heading) : "").append(";")
				.append(this.roll != null ? SEDAPExpressMessage.NumberFormatter.format(this.roll) : "").append(";")
				.append(this.pitch != null ? SEDAPExpressMessage.NumberFormatter.format(this.pitch) : "").append(";")

				.append(this.name != null ? this.name : "").append(";")

				.append(this.sidc != null ? String.valueOf(this.sidc).toUpperCase() : "").toString());

	}

}
