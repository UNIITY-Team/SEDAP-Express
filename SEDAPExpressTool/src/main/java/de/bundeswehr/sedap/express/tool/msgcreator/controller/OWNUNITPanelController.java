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

import java.text.DecimalFormat;

import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SIDCCodes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;

/**
 * 
 * @author Volker Voß
 *
 */
public class OWNUNITPanelController extends MessagePanelController {

    @FXML
    private TextField altitudeTextField;

    @FXML
    private Label courseLabel;

    @FXML
    private TextField courseTextField;

    @FXML
    private Label headingLabel;

    @FXML
    private TextField headingTextField;

    @FXML
    private Label latitudeLabel;

    @FXML
    private TextField latitudeTextField;

    @FXML
    private Label longitudeLabel;

    @FXML
    private TextField longitudeTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label pitchLabel;

    @FXML
    private TextField pitchTextField;

    @FXML
    private Label rollLabel;

    @FXML
    private TextField rollTextField;

    @FXML
    private ComboBox<String> sidcComboBox;

    @FXML
    private ComboBox<String> sidcDimComboBox;

    @FXML
    private TextField speedTextField;

    @FXML
    void initialize() {
	assert this.altitudeTextField != null : "fx:id=\"altitudeTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.courseLabel != null : "fx:id=\"courseLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.courseTextField != null : "fx:id=\"courseTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.headingTextField != null : "fx:id=\"headingTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.latitudeLabel != null : "fx:id=\"latitudeLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.latitudeTextField != null : "fx:id=\"latitudeTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.longitudeLabel != null : "fx:id=\"longitudeLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.longitudeTextField != null : "fx:id=\"longitudeTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.pitchLabel != null : "fx:id=\"pitchLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.pitchTextField != null : "fx:id=\"pitchTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.rollLabel != null : "fx:id=\"rollLabel\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.rollTextField != null : "fx:id=\"rollTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.sidcComboBox != null : "fx:id=\"sidcComboBox\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.sidcDimComboBox != null : "fx:id=\"sidcDimComboBox\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";
	assert this.speedTextField != null : "fx:id=\"speedTextField\" was not injected: check your FXML file 'OWNUNITPanel.fxml'.";

	this.sidcDimComboBox.setItems(FXCollections.observableList(MessagePanelController.DimensionsList));
	this.sidcDimComboBox.getSelectionModel().selectedIndexProperty().addListener((_, _, n) -> {
	    switch ((int) n) {
	    case 0 -> this.sidcComboBox.setItems(null);
	    case 1 -> this.sidcComboBox.setItems(FXCollections.observableList(SIDCCodes.spaceCodesList));
	    case 2 -> this.sidcComboBox.setItems(FXCollections.observableList(SIDCCodes.airCodesList));
	    case 3 -> this.sidcComboBox.setItems(FXCollections.observableList(SIDCCodes.surfaceCodesList));
	    case 4 -> this.sidcComboBox.setItems(FXCollections.observableList(SIDCCodes.subsurfaceCodesList));
	    case 5 -> this.sidcComboBox.setItems(FXCollections.observableList(SIDCCodes.groundCodesList));
	    default -> throw new IllegalArgumentException();
	    }
	    this.sidcComboBox.getSelectionModel().select(0);
	});

	Tooltip tooltipLat = new Tooltip("Values from (-) 0.0 - 89.99 are legal.\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipLong = new Tooltip("Values from (-)0.0 - 179.99 are legal\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipAlt = new Tooltip("Value in Meter");
	Tooltip tooltipSOG = new Tooltip("Speed over ground, value in meter/second");
	Tooltip tooltipCOG = new Tooltip("Course over ground, value 0.0 - 359.99 are legal.\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipHeading = new Tooltip("Heading of own unit, value 0.0 - 359.99 are legal.\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipRoll = new Tooltip("Roll of own unit, value 0.0 - 359.99 are legal.\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipPitch = new Tooltip("Pitch of own unit, value 0.0 - 359.99 are legal.\n" + "The input must be made with a decimal point. \n" + "Example: 23.0");
	Tooltip tooltipName = new Tooltip("Name of onw unit, all values legal");

	this.latitudeTextField.setTooltip(tooltipLat);
	this.longitudeTextField.setTooltip(tooltipLong);
	this.altitudeTextField.setTooltip(tooltipAlt);
	this.speedTextField.setTooltip(tooltipSOG);
	this.courseTextField.setTooltip(tooltipCOG);
	this.headingTextField.setTooltip(tooltipHeading);
	this.rollTextField.setTooltip(tooltipRoll);
	this.pitchTextField.setTooltip(tooltipPitch);
	this.nameTextField.setTooltip(tooltipName);

	// SIDC Combo-Boxen
	this.sidcDimComboBox.getSelectionModel().select(2);
	this.sidcComboBox.getSelectionModel().select(33);
	setSIDC();

	// Latitude
	this.latitudeTextField.setTextFormatter(MessagePanelController.createLatitudeFormatter());
	this.latitudeTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("-")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.latitudeTextField.setText(this.latitudeFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.latitudeTextField.getSelection().getLength() == 0) {
		    this.latitudeTextField.setText(oldValue);
		}
	    }
	});
	this.latitudeTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.latitudeLabel.setVisible(!validateAndFormatLatField(this.latitudeTextField));
	    }
	});

	// Longitude
	this.longitudeTextField.setTextFormatter(MessagePanelController.createLongitudeFormatter());
	this.longitudeTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("-")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.longitudeTextField.setText(this.longitudeFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.longitudeTextField.getSelection().getLength() == 0) {
		    this.longitudeTextField.setText(oldValue);
		}
	    }
	});

	this.longitudeTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.longitudeLabel.setVisible(!validateAndFormatLongField(this.longitudeTextField));
	    }
	});

	// Altitude
	this.altitudeTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	// Speed
	this.speedTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches(SEDAPExpressMessage.DOUBLE_MATCHER.toString()) || change.isDeleted()) {
		return change;
	    }
	    return null;
	}));

	// Kurs
	this.courseTextField.setTextFormatter(MessagePanelController.createBearingFormatter());
	this.courseTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.courseTextField.setText(this.bearingFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.courseTextField.getSelection().getLength() == 0) {
		    this.courseTextField.setText(oldValue);
		}
	    }
	});

	this.courseTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.courseLabel.setVisible(!validateAndFormatCourseField(this.courseTextField));
	    }
	});

	// Heading
	this.headingTextField.setTextFormatter(MessagePanelController.createBearingFormatter());
	this.headingTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.headingTextField.setText(this.bearingFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.headingTextField.getSelection().getLength() == 0) {
		    this.headingTextField.setText(oldValue);
		}
	    }
	});

	this.headingTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.headingLabel.setVisible(!validateAndFormatCourseField(this.headingTextField));
	    }
	});

	// Roll
	this.rollTextField.setTextFormatter(MessagePanelController.createBearingFormatter());
	this.rollTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.rollTextField.setText(this.bearingFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.rollTextField.getSelection().getLength() == 0) {
		    this.rollTextField.setText(oldValue);
		}
	    }
	});
	this.rollTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.rollLabel.setVisible(!validateAndFormatCourseField(this.rollTextField));
	    }
	});

	// Pitch
	this.pitchTextField.setTextFormatter(MessagePanelController.createBearingFormatter());
	this.pitchTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.pitchTextField.setText(this.bearingFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.pitchTextField.getSelection().getLength() == 0) {
		    this.pitchTextField.setText(oldValue);
		}
	    }
	});
	this.pitchTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.pitchLabel.setVisible(!validateAndFormatCourseField(this.pitchTextField));
	    }
	});

	this.sidcDimComboBox.valueProperty().addListener((_, _, newValue) -> {
	    if (newValue != null) {
		setSIDC();
	    } else {
		this.sidc = null;
	    }
	});

	this.sidcComboBox.valueProperty().addListener((_, _, newValue) -> {
	    if (newValue != null) {
		setSIDC();
	    } else {
		this.sidc = null;
	    }
	});

    }

    private boolean validateAndFormatCourseField(TextField textField) {
	try {
	    double value = Double.parseDouble(textField.getText().replace(',', '.'));
	    if (value >= 0.0 && value <= 359.99) {
		textField.setText(String.format("%06.2f", value).replace(',', '.'));
		return true;
	    } else {
		textField.setText("");
		return false;
	    }
	} catch (NumberFormatException ex) {
	    textField.setText("");
	    return false;
	}
    }

    private boolean validateAndFormatLatField(TextField textField) {
	try {
	    double value = Double.parseDouble(textField.getText().replace(',', '.'));
	    if (value >= -90 && value <= 90) {
		textField.setText(this.latitudeFormat.format(value).replace(',', '.'));
		return true;
	    } else {
		textField.setText("");
		return false;
	    }
	} catch (NumberFormatException ex) {
	    textField.setText("");
	    return false;
	}
    }

    private boolean validateAndFormatLongField(TextField textField) {
	try {
	    double value = Double.parseDouble(textField.getText().replace(',', '.'));
	    if (value >= -180 && value <= 180) {
		textField.setText(this.longitudeFormat.format(value).replace(',', '.'));
		return true;
	    } else {
		textField.setText("");
		return false;
	    }
	} catch (NumberFormatException ex) {
	    textField.setText("");
	    return false;
	}
    }

    private void setSIDC() {
	StringBuilder sb = new StringBuilder(MessagePanelController.getDimensionCharFromIndex(this.sidcDimComboBox.getSelectionModel().getSelectedIndex())
		+ " " + this.sidcComboBox.getSelectionModel().getSelectedItem());
	sb.setCharAt(1, MessagePanelController.getIdentiy(this.sidcComboBox.getValue()));
	int pipeIndex = sb.indexOf("|");
	if (pipeIndex != -1) {
	    sb.setLength(pipeIndex);
	}
	sb.append("-----");
	String tmp = sb.toString();
	this.sidc = tmp.toCharArray();
    }

    DecimalFormat latitudeFormat = new DecimalFormat("00.0000");
    DecimalFormat longitudeFormat = new DecimalFormat("000.0000");
    DecimalFormat bearingFormat = new DecimalFormat("000.0000");

    private char[] sidc;

    @Override
    public OWNUNIT createMessage(Byte number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

	OWNUNIT ownunit = new OWNUNIT(number, time, sender,
		classification, acknowledgement, mac,
		Double.parseDouble(this.latitudeTextField.getText()),
		Double.parseDouble(this.longitudeTextField.getText()),
		this.altitudeTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.altitudeTextField.getText()),
		this.speedTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.speedTextField.getText()),
		this.courseTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.courseTextField.getText()),
		this.headingTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.headingTextField.getText()),
		this.rollTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.rollTextField.getText()),
		this.pitchTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.pitchTextField.getText()),
		this.nameTextField.getText(),
		this.sidc);

	return ownunit;
    }

    @Override
    public boolean isValidFilled() {
	// nur Pflichtfelder zur Überprüfung, ab wann eine Nachricht gesendet werden kann
	return (!this.latitudeLabel.isVisible() && !this.longitudeLabel.isVisible());
	// && !courseLabel.isVisible() && !headingLabel.isVisible()
	// && !rollLabel.isVisible() && !pitchLabel.isVisible());
    }

}
