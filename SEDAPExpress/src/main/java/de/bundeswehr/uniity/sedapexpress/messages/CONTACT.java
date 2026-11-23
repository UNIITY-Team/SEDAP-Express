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
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * Represents or provides the contact functionality.
 * 
 * @author Volker Voß
 *
 */
public class CONTACT extends SEDAPExpressMessage {

	private static final long serialVersionUID = 5990524206762624628L;

/**
 * Represents or provides the contact functionality.
 */
	public enum Source {

		None(' '), Radar('R'), AIS('A'), IFF('I'), Sonar('S'), EW('E'), Optical('O'), Synthetic('Y'), Manual('M');

		char source;

	/**
	 * Returns the source value.
	 * @return the source value
	 */
		public char getSourceValue() {

			return this.source;
		}

		Source(char source) {
			this.source = source;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param source value for source
	 * @return the result of the operation
	 */
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

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
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
	private TreeSet<Source> source;
	private char[] sidc;
	private String mmsi;
	private String icao;

	private byte[] multimediaData;

	private String comment;

	private Comparator<Source> sourceComparator = new Comparator<Source>() {
	/**
	 * Returns the result of the compare operation.
	 * @param o1 value for o1
	 * @param o2 value for o2
	 * @return the result of the operation
	 */
		@Override
		public int compare(Source o1, Source o2) {
			return Integer.compare(o1.source, o2.source);
		}
	};

	/**
	 * Returns the contact id.
	 * @return the contact id
	 */
	public String getContactID() {
		return this.contactID;
	}

	/**
	 * Sets the contact id.
	 * @param contactID value for contact id
	 */
	public void setContactID(String contactID) {
		this.contactID = contactID;
	}

	/**
	 * Returns the delete flag.
	 * @return the delete flag
	 */
	public DeleteFlag getDeleteFlag() {
		return this.deleteFlag;
	}

	/**
	 * Sets the delete flag.
	 * @param deleteFlag value for delete flag
	 */
	public void setDeleteFlag(DeleteFlag deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

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
	 * Returns the relative xdistance.
	 * @return the relative xdistance
	 */
	public Double getRelativeXDistance() {
		return this.relativeXDistance;
	}

	/**
	 * Sets the relative xdistance.
	 * @param relativeXDistance value for relative xdistance
	 */
	public void setRelativeXDistance(Double relativeXDistance) {
		this.relativeXDistance = relativeXDistance;
	}

	/**
	 * Returns the relative ydistance.
	 * @return the relative ydistance
	 */
	public Double getRelativeYDistance() {
		return this.relativeYDistance;
	}

	/**
	 * Sets the relative ydistance.
	 * @param relativeYDistance value for relative ydistance
	 */
	public void setRelativeYDistance(Double relativeYDistance) {
		this.relativeYDistance = relativeYDistance;
	}

	/**
	 * Returns the relative zdistance.
	 * @return the relative zdistance
	 */
	public Double getRelativeZDistance() {
		return this.relativeZDistance;
	}

	/**
	 * Sets the relative zdistance.
	 * @param relativeZDistance value for relative zdistance
	 */
	public void setRelativeZDistance(Double relativeZDistance) {
		this.relativeZDistance = relativeZDistance;
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
	 * Returns the width.
	 * @return the width
	 */
	public Double getWidth() {
		return this.width;
	}

	/**
	 * Sets the width.
	 * @param width value for width
	 */
	public void setWidth(Double width) {
		this.width = width;
	}

	/**
	 * Returns the length.
	 * @return the length
	 */
	public Double getLength() {
		return this.length;
	}

	/**
	 * Sets the length.
	 * @param length value for length
	 */
	public void setLength(Double length) {
		this.length = length;
	}

	/**
	 * Returns the height.
	 * @return the height
	 */
	public Double getHeight() {
		return this.height;
	}

	/**
	 * Sets the height.
	 * @param height value for height
	 */
	public void setHeight(Double height) {
		this.height = height;
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
	 * Returns the source.
	 * @return the source
	 */
	public TreeSet<Source> getSource() {
		return this.source;
	}

	/**
	 * Sets the source.
	 * @param source value for source
	 */
	public void setSource(TreeSet<Source> source) {
		this.source = source;
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
	 * Returns the mmsi.
	 * @return the mmsi
	 */
	public String getMMSI() {
		return this.mmsi;
	}

	/**
	 * Sets the mmsi.
	 * @param mmsi value for mmsi
	 */
	public void setMMSI(String mmsi) {
		this.mmsi = mmsi;
	}

	/**
	 * Returns the icao.
	 * @return the icao
	 */
	public String getICAO() {
		return this.icao;
	}

	/**
	 * Sets the icao.
	 * @param icao value for icao
	 */
	public void setICAO(String icao) {
		this.icao = icao;
	}

	/**
	 * Returns the multimedia data.
	 * @return the multimedia data
	 */
	public byte[] getMultimediaData() {
		return this.multimediaData;
	}

	/**
	 * Sets the multimedia data.
	 * @param multimediaData value for multimedia data
	 */
	public void setMultimediaData(byte[] multimediaData) {
		this.multimediaData = multimediaData;
	}

	/**
	 * Returns the comment.
	 * @return the comment
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * Sets the comment.
	 * @param comment value for comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Instantiate a new default CONTACT message
	 */
	public CONTACT() {

		super(null, null, null, null, null, null);

		this.contactID = null;
		this.deleteFlag = null;
		this.latitude = null;
		this.longitude = null;
		this.altitude = null;
		this.relativeXDistance = null;
		this.relativeYDistance = null;
		this.relativeZDistance = null;
		this.speed = null;
		this.course = null;
		this.heading = null;
		this.roll = null;
		this.pitch = null;
		this.width = null;
		this.length = null;
		this.height = null;
		this.name = null;
		this.source = null;
		this.sidc = null;
		this.mmsi = null;
		this.icao = null;
		this.multimediaData = null;
		this.comment = null;
	}

	/**
	 * Instantiate a new CONTACT message
	 * 
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param contactID value for contact id
	 * @param deleteFlag value for delete flag
	 * @param latitude value for latitude
	 * @param longitude value for longitude
	 * @param altitude value for altitude
	 * @param relativeXDistance value for relative xdistance
	 * @param relativeYDistance value for relative ydistance
	 * @param relativeZDistance value for relative zdistance
	 * @param speed value for speed
	 * @param course value for course
	 * @param heading value for heading
	 * @param roll value for roll
	 * @param pitch value for pitch
	 * @param width value for width
	 * @param length value for length
	 * @param height value for height
	 * @param name value for name
	 * @param source value for source
	 * @param sidc value for sidc
	 * @param mmsi value for mmsi
	 * @param icao value for icao
	 * @param multimediaData value for multimedia data
	 * @param comment value for comment
	 */
	public CONTACT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String contactID, DeleteFlag deleteFlag, Double latitude,
			Double longitude, Double altitude, Double relativeXDistance, Double relativeYDistance, Double relativeZDistance, Double speed, Double course, Double heading, Double roll, Double pitch,
			Double width, Double length, Double height, String name, Source source, char[] sidc, String mmsi, String icao, byte[] multimediaData, String comment) {
		this(number, time, sender, classification, acknowledgement, mac, contactID, deleteFlag, latitude, longitude, altitude, relativeXDistance, relativeYDistance, relativeZDistance, speed, course,
				heading, roll, pitch, width, length, height, name, source.toString(), sidc, mmsi, icao, multimediaData, comment);

	}

	/**
	 * 
	 * Instantiate a new CONTACT message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param contactID value for contact id
	 * @param deleteFlag value for delete flag
	 * @param latitude value for latitude
	 * @param longitude value for longitude
	 * @param altitude value for altitude
	 * @param relativeXDistance value for relative xdistance
	 * @param relativeYDistance value for relative ydistance
	 * @param relativeZDistance value for relative zdistance
	 * @param speed value for speed
	 * @param course value for course
	 * @param heading value for heading
	 * @param roll value for roll
	 * @param pitch value for pitch
	 * @param width value for width
	 * @param length value for length
	 * @param height value for height
	 * @param name value for name
	 * @param source value for source
	 * @param sidc value for sidc
	 * @param mmsi value for mmsi
	 * @param icao value for icao
	 * @param multimediaData value for multimedia data
	 * @param comment value for comment
	 */
	public CONTACT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String contactID, DeleteFlag deleteFlag, Double latitude,
			Double longitude, Double altitude, Double relativeXDistance, Double relativeYDistance, Double relativeZDistance, Double speed, Double course, Double heading, Double roll, Double pitch,
			Double width, Double length, Double height, String name, String source, char[] sidc, String mmsi, String icao, byte[] multimediaData, String comment) {

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
		this.source = new TreeSet<Source>(this.sourceComparator);
		source.chars().forEach(ch -> this.source.add(Source.valueOfSource((char) ch)));
		this.sidc = sidc;
		this.mmsi = mmsi;
		this.icao = icao;
		this.multimediaData = multimediaData;
		this.comment = comment;
	}

	/**
	 * Instantiate a new CONTACT message from a serialized message
	 *
	 * @param message value for message
	 */
	public CONTACT(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
	 * Instantiate a new CONTACT message from a paramter list
	 *
	 * @param message value for message
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
			value = message.next().toUpperCase();
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
				this.source = new TreeSet<>(this.sourceComparator);
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
				this.sidc = value.toUpperCase().toCharArray();
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"SIDC\" contains invalid value - length: " + value.length() + " bytes!",
						value);
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

		// MultimediaData
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"imageData\" is empty!");
			} else {
				try {
					this.multimediaData = Base64.decode(value);
					if (this.multimediaData.length > 65000) {
						SEDAPExpressMessage.logger.logp(Level.WARNING, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"multimediaData\" exceeds 65000 bytes!");
					}
				} catch (DecoderException e) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)",
							"Optional field \"multimediaData\" could not be decoded from Base64 -> \"" + value + "\"!");
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
					this.comment = new String(Base64.decode(value), StandardCharsets.ISO_8859_1);
				} catch (DecoderException e) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "CONTACT", "CONTACT(Iterator<String> message)", "Optional field \"comment\" could not be decoded from Base64 -> \"" + value + "\"!");
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

		StringBuilder sourceStr = new StringBuilder();
		if (this.source != null)
			this.source.forEach(s -> sourceStr.append(s));

		return SEDAPExpressMessage.removeSemicolons(serializeHeader()

				.append(this.contactID).append(";")

				.append((this.deleteFlag != null && this.deleteFlag != DeleteFlag.FALSE) ? this.deleteFlag : "").append(";")

				.append((this.latitude != null) ? SEDAPExpressMessage.NumberFormatter.format(this.latitude) : "").append(";")
				.append(this.longitude != null ? SEDAPExpressMessage.NumberFormatter.format(this.longitude) : "").append(";")
				.append(this.altitude != null ? SEDAPExpressMessage.NumberFormatter.format(this.altitude) : "").append(";")

				.append(this.relativeXDistance != null ? SEDAPExpressMessage.NumberFormatter.format(this.relativeXDistance) : "").append(";")
				.append(this.relativeYDistance != null ? SEDAPExpressMessage.NumberFormatter.format(this.relativeYDistance) : "").append(";")
				.append(this.relativeZDistance != null ? SEDAPExpressMessage.NumberFormatter.format(this.relativeZDistance) : "").append(";")

				.append(this.speed != null ? SEDAPExpressMessage.NumberFormatter.format(this.speed) : "").append(";")
				.append(this.course != null ? SEDAPExpressMessage.NumberFormatter.format(this.course) : "").append(";")

				.append(this.heading != null ? SEDAPExpressMessage.NumberFormatter.format(this.heading) : "").append(";")
				.append(this.roll != null ? SEDAPExpressMessage.NumberFormatter.format(this.roll) : "").append(";")
				.append(this.pitch != null ? SEDAPExpressMessage.NumberFormatter.format(this.pitch) : "").append(";")

				.append(this.width != null ? SEDAPExpressMessage.NumberFormatter.format(this.width) : "").append(";")
				.append(this.length != null ? SEDAPExpressMessage.NumberFormatter.format(this.length) : "").append(";")
				.append(this.height != null ? SEDAPExpressMessage.NumberFormatter.format(this.height) : "").append(";")

				.append(this.name != null ? this.name : "").append(";").append(!sourceStr.isEmpty() ? sourceStr.toString() : "").append(";")

				.append(this.sidc != null ? String.valueOf(this.sidc).toUpperCase() : "").append(";").append(this.mmsi != null ? this.mmsi : "").append(";").append(this.icao != null ? this.icao : "")
				.append(";")

				.append(this.multimediaData != null ? Base64.toBase64String(this.multimediaData) : "").append(";")

				.append((this.comment != null) ? Base64.toBase64String(this.comment.getBytes(StandardCharsets.ISO_8859_1)) : "").toString());
	}

}
