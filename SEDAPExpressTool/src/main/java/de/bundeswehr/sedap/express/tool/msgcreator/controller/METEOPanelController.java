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
 * FITNESS FOR A PARTICULAR PURPOSEnARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.sedap.express.tool.msgcreator.controller;

import de.bundeswehr.uniity.sedapexpress.messages.METEO;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * 
 * @author Volker Voß
 *
 */
public class METEOPanelController extends MessagePanelController {

    @FXML
    private Label airTemperatureLabel;

    @FXML
    private TextField airTemperatureTextField;

    @FXML
    private Label cloudCoverLabel;

    @FXML
    private TextField cloudCoverTextField;

    @FXML
    private Label cloudHeightLabel;

    @FXML
    private TextField cloudHeightTextField;

    @FXML
    private Label contactIdLabel5;

    @FXML
    private Label dewPointLabel;

    @FXML
    private TextField dewPointTextField;

    @FXML
    private Label humidityRelLabel;

    @FXML
    private TextField humidityRelTextField;

    @FXML
    private Label pressureLabel;

    @FXML
    private TextField pressureTextField;

    @FXML
    private Label speedThroughWaterLabel;

    @FXML
    private TextField speedThroughWaterTextfield;

    @FXML
    private Label visibilityLabel;

    @FXML
    private TextField visibilityTextField;

    @FXML
    private Label waterDepthLabel;

    @FXML
    private TextField waterDepthTextField;

    @FXML
    private Label waterDirectionLabel;

    @FXML
    private TextField waterDirectionTextField;

    @FXML
    private Label waterSpeedLabel;

    @FXML
    private TextField waterSpeedTextField;

    @FXML
    private Label waterTemperatureLabel;

    @FXML
    private TextField waterTemperatureTextField;

    @FXML
    private Label windDirectionLabel;

    @FXML
    private TextField windDirectionTextField;

    @FXML
    private Label windSpeedLabel;

    @FXML
    private TextField windSpeedTextField;

    @FXML
    void initialize() {
	assert this.airTemperatureLabel != null : "fx:id=\"airTemperatureLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.airTemperatureTextField != null : "fx:id=\"airTemperatureTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.cloudCoverLabel != null : "fx:id=\"cloudCoverLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.cloudCoverTextField != null : "fx:id=\"cloudCoverTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.cloudHeightLabel != null : "fx:id=\"cloudHeightLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.cloudHeightTextField != null : "fx:id=\"cloudHeightTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.contactIdLabel5 != null : "fx:id=\"contactIdLabel5\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.dewPointLabel != null : "fx:id=\"dewPointLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.dewPointTextField != null : "fx:id=\"dewPointTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.humidityRelLabel != null : "fx:id=\"humidityRelLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.humidityRelTextField != null : "fx:id=\"humidityRelTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.pressureLabel != null : "fx:id=\"pressureLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.pressureTextField != null : "fx:id=\"pressureTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.speedThroughWaterLabel != null : "fx:id=\"speedThroughWaterLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.speedThroughWaterTextfield != null : "fx:id=\"speedThroughWaterTextfield\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.visibilityLabel != null : "fx:id=\"visibilityLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.visibilityTextField != null : "fx:id=\"visibilityTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterDepthLabel != null : "fx:id=\"waterDepthLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterDepthTextField != null : "fx:id=\"waterDepthTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterDirectionLabel != null : "fx:id=\"waterDirectionLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterDirectionTextField != null : "fx:id=\"waterDirectionTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterSpeedLabel != null : "fx:id=\"waterSpeedLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterSpeedTextField != null : "fx:id=\"waterSpeedTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterTemperatureLabel != null : "fx:id=\"waterTemperatureLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.waterTemperatureTextField != null : "fx:id=\"waterTemperatureTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.windDirectionLabel != null : "fx:id=\"windDirectionLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.windDirectionTextField != null : "fx:id=\"windDirectionTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.windSpeedLabel != null : "fx:id=\"windSpeedLabel\" was not injected: check your FXML file 'METEOPanel.fxml'.";
	assert this.windSpeedTextField != null : "fx:id=\"windSpeedTextField\" was not injected: check your FXML file 'METEOPanel.fxml'.";

	// SpeedThrougWater
	this.speedThroughWaterTextfield.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.speedThroughWaterTextfield.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.speedThroughWaterTextfield.getText();
		if (text.isEmpty()) {
		    this.speedThroughWater = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.speedThroughWaterTextfield.setText(text);
		    this.speedThroughWater = Double.parseDouble(text);
		}
	    }
	});
	this.speedThroughWaterLabel.setVisible(false);

	// WaterSpeed
	this.waterSpeedTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.waterSpeedTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.waterSpeedTextField.getText();
		if (text.isEmpty()) {
		    this.speedThroughWater = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.waterSpeedTextField.setText(text);
		    this.waterSpeed = Double.parseDouble(text);
		}
	    }
	});
	this.waterSpeedLabel.setVisible(false);

	// WaterDirection
	this.waterDirectionTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.waterDirectionTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.waterDirectionTextField.getText();
		if (text.isEmpty()) {
		    this.waterDirection = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.waterDirectionTextField.setText(text);
		    this.waterDirection = Double.parseDouble(text);
		}
	    }
	});
	this.waterDirectionLabel.setVisible(false);

	// WaterTemperature
	this.waterTemperatureTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.waterTemperatureTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.waterTemperatureTextField.getText();
		if (text.isEmpty()) {
		    this.waterTemperature = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.waterTemperatureTextField.setText(text);
		    this.waterTemperature = Double.parseDouble(text);
		}
	    }
	});
	this.waterTemperatureLabel.setVisible(false);

	// WaterDepth
	this.waterDepthTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.waterDepthTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.waterDepthTextField.getText();
		if (text.isEmpty()) {
		    this.waterDepth = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.waterDepthTextField.setText(text);
		    this.waterDepth = Double.parseDouble(text);
		}
	    }
	});
	this.waterDepthLabel.setVisible(false);

	// AirTemperatur
	this.airTemperatureTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.airTemperatureTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.airTemperatureTextField.getText();
		if (text.isEmpty()) {
		    this.airTemperature = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.airTemperatureTextField.setText(text);
		    this.airTemperature = Double.parseDouble(text);
		}
	    }
	});
	this.airTemperatureLabel.setVisible(false);

	// DewPoint
	this.dewPointTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.dewPointTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.dewPointTextField.getText();
		if (text.isEmpty()) {
		    this.dewPoint = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.dewPointTextField.setText(text);
		    this.dewPoint = Double.parseDouble(text);
		}
	    }
	});
	this.dewPointLabel.setVisible(false);

	// Humidity Rel
	this.humidityRelTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.humidityRelTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.humidityRelTextField.getText();
		if (text.isEmpty()) {
		    this.humidityRel = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.humidityRelTextField.setText(text);
		    this.humidityRel = Double.parseDouble(text);
		}
	    }
	});
	this.humidityRelLabel.setVisible(false);

	// Pressure Text
	this.pressureTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.pressureTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.pressureTextField.getText();
		if (text.isEmpty()) {
		    this.pressure = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.pressureTextField.setText(text);
		    this.pressure = Double.parseDouble(text);
		}
	    }
	});
	this.pressureLabel.setVisible(false);

	// Wind Speed
	this.windSpeedTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.windSpeedTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.windSpeedTextField.getText();
		if (text.isEmpty()) {
		    this.windSpeed = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.windSpeedTextField.setText(text);
		    this.windSpeed = Double.parseDouble(text);
		}
	    }
	});
	this.windSpeedLabel.setVisible(false);

	// Wind Direction
	this.windDirectionTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.windDirectionTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.windDirectionTextField.getText();
		if (text.isEmpty()) {
		    this.windDirection = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.windDirectionTextField.setText(text);
		    this.windDirection = Double.parseDouble(text);
		}
	    }
	});
	this.windDirectionLabel.setVisible(false);

	// Visibility
	this.visibilityTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.visibilityTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.visibilityTextField.getText();
		if (text.isEmpty()) {
		    this.visibility = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.visibilityTextField.setText(text);
		    this.visibility = Double.parseDouble(text);
		}
	    }
	});
	this.visibilityLabel.setVisible(false);

	// Cloud Height
	this.cloudHeightTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.cloudHeightTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.cloudHeightTextField.getText();
		if (text.isEmpty()) {
		    this.cloudHeight = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.cloudHeightTextField.setText(text);
		    this.cloudHeight = Double.parseDouble(text);
		}
	    }
	});
	this.cloudHeightLabel.setVisible(false);

	// Cloud Cover
	this.cloudCoverTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*\\.?\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.cloudCoverTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) { // Wenn der Fokus verloren geht
		String text = this.cloudCoverTextField.getText();
		if (text.isEmpty()) {
		    this.cloudCover = null;
		} else {
		    if (text.startsWith(".")) {
			text = "0" + text;
		    }
		    if (!text.contains(".")) {
			text = text + ".0";
		    }
		    if (text.endsWith(".")) {
			text = text + "0";
		    }
		    this.cloudCoverTextField.setText(text);
		    this.cloudCover = Double.parseDouble(text);
		}
	    }
	});
	this.cloudCoverLabel.setVisible(false);

    }

    Double speedThroughWater, waterSpeed, waterDirection, waterTemperature, waterDepth, airTemperature, dewPoint, humidityRel,
	    pressure, windSpeed, windDirection, visibility, cloudHeight, cloudCover;

    String reference;

    @Override
    public METEO createMessage(Byte number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

	METEO meteo = new METEO(number, time, sender,
		classification, acknowledgement, mac,

		this.speedThroughWater,
		this.waterSpeed,
		this.waterDirection,
		this.waterTemperature,
		this.waterDepth,
		this.airTemperature,
		this.dewPoint,
		this.humidityRel,
		this.pressure,
		this.windSpeed,
		this.windDirection,
		this.visibility,
		this.cloudHeight,
		this.cloudCover,

		this.reference);

	return meteo;
    }

    @Override
    public boolean isValidFilled() {

	return true;
    }
}
