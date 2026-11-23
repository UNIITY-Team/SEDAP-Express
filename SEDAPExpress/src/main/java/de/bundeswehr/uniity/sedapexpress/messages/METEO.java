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
 * Represents or provides the meteo functionality.
 * 
 * @author Volker Voß
 *
 */
public class METEO extends SEDAPExpressMessage {

	private static final long serialVersionUID = -792741988148542864L;

	private Double speedThroughWater;
	private Double waterSpeed;
	private Double waterDirection;
	private Double waterTemperature;
	private Double waterDepth;
	private Double airTemperature;
	private Double dewPoint;
	private Double humidityRel;
	private Double pressure;
	private Double windSpeed;
	private Double windDirection;
	private Double visibility;
	private Double cloudHeight;
	private Double cloudCover;

	private String reference;

	/**
	 * Returns the speed through water.
	 * @return the speed through water
	 */
	public Double getSpeedThroughWater() {
		return this.speedThroughWater;
	}

	/**
	 * Sets the speed through water.
	 * @param speedThroughWater value for speed through water
	 */
	public void setSpeedThroughWater(Double speedThroughWater) {
		this.speedThroughWater = speedThroughWater;
	}

	/**
	 * Returns the water speed.
	 * @return the water speed
	 */
	public Double getWaterSpeed() {
		return this.waterSpeed;
	}

	/**
	 * Sets the water speed.
	 * @param waterSpeed value for water speed
	 */
	public void setWaterSpeed(Double waterSpeed) {
		this.waterSpeed = waterSpeed;
	}

	/**
	 * Returns the water direction.
	 * @return the water direction
	 */
	public Double getWaterDirection() {
		return this.waterDirection;
	}

	/**
	 * Sets the water direction.
	 * @param waterDirection value for water direction
	 */
	public void setWaterDirection(Double waterDirection) {
		this.waterDirection = waterDirection;
	}

	/**
	 * Returns the water temperature.
	 * @return the water temperature
	 */
	public Double getWaterTemperature() {
		return this.waterTemperature;
	}

	/**
	 * Sets the water temperature.
	 * @param waterTemperature value for water temperature
	 */
	public void setWaterTemperature(Double waterTemperature) {
		this.waterTemperature = waterTemperature;
	}

	/**
	 * Returns the water depth.
	 * @return the water depth
	 */
	public Double getWaterDepth() {
		return this.waterDepth;
	}

	/**
	 * Sets the water depth.
	 * @param waterDepth value for water depth
	 */
	public void setWaterDepth(Double waterDepth) {
		this.waterDepth = waterDepth;
	}

	/**
	 * Returns the air temperature.
	 * @return the air temperature
	 */
	public Double getAirTemperature() {
		return this.airTemperature;
	}

	/**
	 * Sets the air temperature.
	 * @param airTemperature value for air temperature
	 */
	public void setAirTemperature(Double airTemperature) {
		this.airTemperature = airTemperature;
	}

	/**
	 * Returns the dew point.
	 * @return the dew point
	 */
	public Double getDewPoint() {
		return this.dewPoint;
	}

	/**
	 * Sets the dew point.
	 * @param dewPoint value for dew point
	 */
	public void setDewPoint(Double dewPoint) {
		this.dewPoint = dewPoint;
	}

	/**
	 * Returns the humidity rel.
	 * @return the humidity rel
	 */
	public Double getHumidityRel() {
		return this.humidityRel;
	}

	/**
	 * Sets the humidity rel.
	 * @param humidityRel value for humidity rel
	 */
	public void setHumidityRel(Double humidityRel) {
		this.humidityRel = humidityRel;
	}

	/**
	 * Returns the pressure.
	 * @return the pressure
	 */
	public Double getPressure() {
		return this.pressure;
	}

	/**
	 * Sets the pressure.
	 * @param pressure value for pressure
	 */
	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}

	/**
	 * Returns the wind speed.
	 * @return the wind speed
	 */
	public Double getWindSpeed() {
		return this.windSpeed;
	}

	/**
	 * Sets the wind speed.
	 * @param windSpeed value for wind speed
	 */
	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}

	/**
	 * Returns the wind direction.
	 * @return the wind direction
	 */
	public Double getWindDirection() {
		return this.windDirection;
	}

	/**
	 * Sets the wind direction.
	 * @param windDirection value for wind direction
	 */
	public void setWindDirection(Double windDirection) {
		this.windDirection = windDirection;
	}

	/**
	 * Returns the visibility.
	 * @return the visibility
	 */
	public Double getVisibility() {
		return this.visibility;
	}

	/**
	 * Sets the visibility.
	 * @param visibility value for visibility
	 */
	public void setVisibility(Double visibility) {
		this.visibility = visibility;
	}

	/**
	 * Returns the cloud height.
	 * @return the cloud height
	 */
	public Double getCloudHeight() {
		return this.cloudHeight;
	}

	/**
	 * Sets the cloud height.
	 * @param cloudHeight value for cloud height
	 */
	public void setCloudHeight(Double cloudHeight) {
		this.cloudHeight = cloudHeight;
	}

	/**
	 * Returns the cloud cover.
	 * @return the cloud cover
	 */
	public Double getCloudCover() {
		return this.cloudCover;
	}

	/**
	 * Sets the cloud cover.
	 * @param cloudCover value for cloud cover
	 */
	public void setCloudCover(Double cloudCover) {
		this.cloudCover = cloudCover;
	}

	/**
	 * Returns the reference.
	 * @return the reference
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * Sets the reference.
	 * @param reference value for reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Instantiate a new default METEO message
	 */
	public METEO() {

		super(null, null, null, null, null, null);

		this.speedThroughWater = null;
		this.waterSpeed = null;
		this.waterDirection = null;
		this.waterTemperature = null;
		this.waterDepth = null;
		this.airTemperature = null;
		this.dewPoint = null;
		this.humidityRel = null;
		this.pressure = null;
		this.windSpeed = null;
		this.windDirection = null;
		this.visibility = null;
		this.cloudHeight = null;
		this.cloudCover = null;
		this.reference = null;
	}

	/**
	 * Instantiate a new METEO message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param speedThroughWater value for speed through water
	 * @param waterSpeed value for water speed
	 * @param waterDirection value for water direction
	 * @param waterTemperature value for water temperature
	 * @param waterDepth value for water depth
	 * @param airTemperature value for air temperature
	 * @param dewPoint value for dew point
	 * @param humidityRel value for humidity rel
	 * @param pressure value for pressure
	 * @param windSpeed value for wind speed
	 * @param windDirection value for wind direction
	 * @param visibility value for visibility
	 * @param cloudHeight value for cloud height
	 * @param cloudCover value for cloud cover
	 * @param reference value for reference
	 */
	public METEO(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, Double speedThroughWater, Double waterSpeed, Double waterDirection,
			Double waterTemperature, Double waterDepth, Double airTemperature, Double dewPoint, Double humidityRel, Double pressure, Double windSpeed, Double windDirection, Double visibility,
			Double cloudHeight, Double cloudCover, String reference) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.speedThroughWater = speedThroughWater;
		this.waterSpeed = waterSpeed;
		this.waterDirection = waterDirection;
		this.waterTemperature = waterTemperature;
		this.waterDepth = waterDepth;
		this.airTemperature = airTemperature;
		this.dewPoint = dewPoint;
		this.humidityRel = humidityRel;
		this.pressure = pressure;
		this.windSpeed = windSpeed;
		this.windDirection = windDirection;
		this.visibility = visibility;
		this.cloudHeight = cloudHeight;
		this.cloudCover = cloudCover;
		this.reference = reference;
	}

	/**
 * Creates a meteo instance.
	 *
	 * @param message value for message
	 */
	public METEO(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
	 * Creates a meteo instance.
	 * @param message value for message
	 */
	public METEO(Iterator<String> message) {

		super(message);

		String value;

		// SpeedThroughWater
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"speedThroughWater\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.speedThroughWater = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"speedThroughWater\" contains invalid value!", value);
			}
		}

		// WaterSpeed
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterSpeed\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.waterSpeed = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterSpeed\" contains invalid value!", value);
			}
		}

		// WaterDirection
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterDirection\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.waterDirection = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterDirection\" contains invalid value!", value);
			}
		}

		// WaterTemperature
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterTemperature\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.waterTemperature = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterTemperature\" contains invalid value!", value);
			}
		}

		// WaterDepth
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterDepth\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.waterDepth = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"waterDepth\" contains invalid value!", value);
			}
		}

		// AirTemperature
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"airTemperature\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.airTemperature = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"airTemperature\" contains invalid value!", value);
			}
		}

		// DewPoint
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"dewPoint\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.dewPoint = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"dewPoint\" contains invalid value!", value);
			}
		}

		// HumidityRel
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"humidityRel\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.humidityRel = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"humidityRel\" contains invalid value!", value);
			}
		}

		// Pressure
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"pressure\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.pressure = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"pressure\" contains invalid value!", value);
			}
		}

		// WindSpeed
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"windSpeed\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.windSpeed = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"windSpeed\" contains invalid value!", value);
			}
		}

		// WindDirection
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"windDirection\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.windDirection = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"windDirection\" contains invalid value!", value);
			}
		}

		// Visibility
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"visibility\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.visibility = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"visibility\" contains invalid value!", value);
			}
		}

		// CloudHeight
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"cloudHeight\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.cloudHeight = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"cloudHeight\" contains invalid value!", value);
			}
		}

		// CloudCover
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"cloudCover\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DOUBLE_MATCHER, value)) {
				this.cloudCover = Double.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "METEO", "METEO(Iterator<String> message)", "Optional field \"cloudCover\" contains invalid value!", value);
			}
		}

		// Reference
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"reference\" is empty!");
			} else {
				this.reference = value;
			}
		} else
			SEDAPExpressMessage.logger.logp(Level.INFO, "METEO", "METEO(Iterator<String> message)", "Optional field \"reference\" is empty!");

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

				.append((this.speedThroughWater != null) ? SEDAPExpressMessage.NumberFormatter.format(this.speedThroughWater) : "").append(";")
				.append(this.waterSpeed != null ? SEDAPExpressMessage.NumberFormatter.format(this.waterSpeed) : "").append(";")
				.append(this.waterDirection != null ? SEDAPExpressMessage.NumberFormatter.format(this.waterDirection) : "").append(";")
				.append(this.waterTemperature != null ? SEDAPExpressMessage.NumberFormatter.format(this.waterTemperature) : "").append(";")
				.append(this.waterDepth != null ? SEDAPExpressMessage.NumberFormatter.format(this.waterDepth) : "").append(";")

				.append(this.airTemperature != null ? SEDAPExpressMessage.NumberFormatter.format(this.airTemperature) : "").append(";")
				.append(this.dewPoint != null ? SEDAPExpressMessage.NumberFormatter.format(this.dewPoint) : "").append(";")
				.append(this.humidityRel != null ? SEDAPExpressMessage.NumberFormatter.format(this.humidityRel) : "").append(";")
				.append(this.pressure != null ? SEDAPExpressMessage.NumberFormatter.format(this.pressure) : "").append(";")

				.append(this.windSpeed != null ? SEDAPExpressMessage.NumberFormatter.format(this.windSpeed) : "").append(";")
				.append(this.windDirection != null ? SEDAPExpressMessage.NumberFormatter.format(this.windDirection) : "").append(";")

				.append(this.visibility != null ? SEDAPExpressMessage.NumberFormatter.format(this.visibility) : "").append(";")
				.append(this.cloudHeight != null ? SEDAPExpressMessage.NumberFormatter.format(this.cloudHeight) : "").append(";")
				.append(this.cloudCover != null ? SEDAPExpressMessage.NumberFormatter.format(this.cloudCover) : "")

				.append((this.reference != null) ? ";" + this.reference : "").toString());
	}

}
