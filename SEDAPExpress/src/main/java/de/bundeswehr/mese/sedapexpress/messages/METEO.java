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

import java.util.Iterator;
import java.util.logging.Level;

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

    public Double getSpeedThroughWater() {
	return this.speedThroughWater;
    }

    public void setSpeedThroughWater(Double speedThroughWater) {
	this.speedThroughWater = speedThroughWater;
    }

    public Double getWaterSpeed() {
	return this.waterSpeed;
    }

    public void setWaterSpeed(Double waterSpeed) {
	this.waterSpeed = waterSpeed;
    }

    public Double getWaterDirection() {
	return this.waterDirection;
    }

    public void setWaterDirection(Double waterDirection) {
	this.waterDirection = waterDirection;
    }

    public Double getWaterTemperature() {
	return this.waterTemperature;
    }

    public void setWaterTemperature(Double waterTemperature) {
	this.waterTemperature = waterTemperature;
    }

    public Double getWaterDepth() {
	return this.waterDepth;
    }

    public void setWaterDepth(Double waterDepth) {
	this.waterDepth = waterDepth;
    }

    public Double getAirTemperature() {
	return this.airTemperature;
    }

    public void setAirTemperature(Double airTemperature) {
	this.airTemperature = airTemperature;
    }

    public Double getDewPoint() {
	return this.dewPoint;
    }

    public void setDewPoint(Double dewPoint) {
	this.dewPoint = dewPoint;
    }

    public Double getHumidityRel() {
	return this.humidityRel;
    }

    public void setHumidityRel(Double humidityRel) {
	this.humidityRel = humidityRel;
    }

    public Double getPressure() {
	return this.pressure;
    }

    public void setPressure(Double pressure) {
	this.pressure = pressure;
    }

    public Double getWindSpeed() {
	return this.windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
	this.windSpeed = windSpeed;
    }

    public Double getWindDirection() {
	return this.windDirection;
    }

    public void setWindDirection(Double windDirection) {
	this.windDirection = windDirection;
    }

    public Double getVisibility() {
	return this.visibility;
    }

    public void setVisibility(Double visibility) {
	this.visibility = visibility;
    }

    public Double getCloudHeight() {
	return this.cloudHeight;
    }

    public void setCloudHeight(Double cloudHeight) {
	this.cloudHeight = cloudHeight;
    }

    public Double getCloudCover() {
	return this.cloudCover;
    }

    public void setCloudCover(Double cloudCover) {
	this.cloudCover = cloudCover;
    }

    /**
     * Instantiate a new METEO message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param speedThroughWater
     * @param waterSpeed
     * @param waterDirection
     * @param waterTemperature
     * @param waterDepth
     * @param airTemperature
     * @param dewPoint
     * @param humidityRel
     * @param pressure
     * @param windSpeed
     * @param windDirection
     * @param visibility
     * @param cloudHeight
     * @param cloudCover
     */
    public METEO(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, Double speedThroughWater, Double waterSpeed, Double waterDirection, Double waterTemperature,
	    Double waterDepth, Double airTemperature, Double dewPoint, Double humidityRel, Double pressure, Double windSpeed, Double windDirection, Double visibility, Double cloudHeight, Double cloudCover) {

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
    }

    /**
     *
     * @param message
     */
    public METEO(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

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
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	} else if (!(obj instanceof METEO)) {
	    return false;
	} else {
	    return super.equals(obj) && (this.speedThroughWater == ((METEO) obj).speedThroughWater) && (this.waterSpeed == ((METEO) obj).waterSpeed) && (this.waterDirection == ((METEO) obj).waterDirection)
		    && (this.waterTemperature == ((METEO) obj).waterTemperature) && (this.waterDepth == ((METEO) obj).waterDepth) && (this.airTemperature == ((METEO) obj).airTemperature) && (this.dewPoint == ((METEO) obj).dewPoint)
		    && (this.humidityRel == ((METEO) obj).humidityRel) && (this.pressure == ((METEO) obj).pressure) && (this.windSpeed == ((METEO) obj).windSpeed) && (this.windDirection == ((METEO) obj).windDirection)
		    && (this.visibility == ((METEO) obj).visibility) && (this.cloudHeight == ((METEO) obj).cloudHeight) && (this.cloudCover == ((METEO) obj).cloudCover);
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append((this.speedThroughWater != null) ? SEDAPExpressMessage.numberFormatter.format(this.speedThroughWater) : "").append(";")
		.append(this.waterSpeed != null ? SEDAPExpressMessage.numberFormatter.format(this.waterSpeed) : "").append(";").append(this.waterDirection != null ? SEDAPExpressMessage.numberFormatter.format(this.waterDirection) : "")
		.append(";").append(this.waterTemperature != null ? SEDAPExpressMessage.numberFormatter.format(this.waterTemperature) : "").append(";")
		.append(this.waterDepth != null ? SEDAPExpressMessage.numberFormatter.format(this.waterDepth) : "").append(";")

		.append(this.airTemperature != null ? SEDAPExpressMessage.numberFormatter.format(this.airTemperature) : "").append(";").append(this.dewPoint != null ? SEDAPExpressMessage.numberFormatter.format(this.dewPoint) : "")
		.append(";").append(this.humidityRel != null ? SEDAPExpressMessage.numberFormatter.format(this.humidityRel) : "").append(";").append(this.pressure != null ? SEDAPExpressMessage.numberFormatter.format(this.pressure) : "")
		.append(";")

		.append(this.windSpeed != null ? SEDAPExpressMessage.numberFormatter.format(this.windSpeed) : "").append(";").append(this.windDirection != null ? SEDAPExpressMessage.numberFormatter.format(this.windDirection) : "")
		.append(";")

		.append(this.visibility != null ? SEDAPExpressMessage.numberFormatter.format(this.visibility) : "").append(";").append(this.cloudHeight != null ? SEDAPExpressMessage.numberFormatter.format(this.cloudHeight) : "").append(";")
		.append(this.cloudCover != null ? SEDAPExpressMessage.numberFormatter.format(this.cloudCover) : "").toString());
    }

}
