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

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import de.bundeswehr.uniity.sedapexpress.messages.EMISSION;
import de.bundeswehr.uniity.sedapexpress.messages.EMISSION.FreqAgility;
import de.bundeswehr.uniity.sedapexpress.messages.EMISSION.Function;
import de.bundeswehr.uniity.sedapexpress.messages.EMISSION.PRFAgility;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DeleteFlag;
import de.bundeswehr.uniity.sedapexpress.messages.SIDCCodes;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;

/**
 * 
 * @author Volker Voß
 *
 */
public class EMISSIONPanelController extends MessagePanelController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label altitudeLabel;

    @FXML
    private TextField altitudeTextField;

    @FXML
    private Label bandwidthLabel;

    @FXML
    private TextField bandwithTextField;

    @FXML
    private Label bearingLabel;

    @FXML
    private TextField bearingTextField;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Label contactIdLabel;

    @FXML
    private ComboBox<DeleteFlag> deleteFlagComboBox;

    @FXML
    private TextField emissionIdTextField;

    @FXML
    private Label emitterAltLabel;

    @FXML
    private TextField emitterAltTextField;

    @FXML
    private Label emitterLatLabel;

    @FXML
    private TextField emitterLatTextField;

    @FXML
    private Label emitterLongLabel;

    @FXML
    private TextField emitterLongTextField;

    @FXML
    private Label freqAgilityLabel;

    @FXML
    private ComboBox<FreqAgility> frequencAgilityComboBox;

    @FXML
    private Label frequenciesLabel;

    @FXML
    private TextField frequenciesTextField;

    @FXML
    private ComboBox<Function> functionComboBox;

    @FXML
    private Label functionLabel;

    @FXML
    private Label latitudeLabel;

    @FXML
    private TextField latitudeTextField;

    @FXML
    private Label longitudeLabel;

    @FXML
    private TextField longitudeTextField;

    @FXML
    private Label powerLabel;

    @FXML
    private TextField powerTextField;

    @FXML
    private ComboBox<PRFAgility> prfAgilityComboBox;

    @FXML
    private Label prfAgilityLabel;

    @FXML
    private ComboBox<String> sidcComboBox;

    @FXML
    private ComboBox<String> sidcDimComboBox;

    @FXML
    private ComboBox<String> sidcIDComboBox;

    @FXML
    private TextField spotNumberTextField;

    @FXML
    private Label spotnumberLabel;

    @FXML
    void initialize() {
	assert this.altitudeLabel != null : "fx:id=\"altitudeLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.altitudeTextField != null : "fx:id=\"altitudeTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.bandwidthLabel != null : "fx:id=\"bandwidthLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.bandwithTextField != null : "fx:id=\"bandwithTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.bearingLabel != null : "fx:id=\"bearingLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.bearingTextField != null : "fx:id=\"bearingTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.commentTextArea != null : "fx:id=\"commentTextArea\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.contactIdLabel != null : "fx:id=\"contactIdLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.deleteFlagComboBox != null : "fx:id=\"deleteFlagComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emissionIdTextField != null : "fx:id=\"emissionIdTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterAltLabel != null : "fx:id=\"emitterAltLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterAltTextField != null : "fx:id=\"emitterAltTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterLatLabel != null : "fx:id=\"emitterLatLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterLatTextField != null : "fx:id=\"emitterLatTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterLongLabel != null : "fx:id=\"emitterLongLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.emitterLongTextField != null : "fx:id=\"emitterLongTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.freqAgilityLabel != null : "fx:id=\"freqAgilityLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.frequencAgilityComboBox != null : "fx:id=\"frequencAgilityComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.frequenciesLabel != null : "fx:id=\"frequenciesLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.frequenciesTextField != null : "fx:id=\"frequenciesTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.functionComboBox != null : "fx:id=\"functionComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.functionLabel != null : "fx:id=\"functionLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.latitudeLabel != null : "fx:id=\"latitudeLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.latitudeTextField != null : "fx:id=\"latitudeTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.longitudeLabel != null : "fx:id=\"longitudeLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.longitudeTextField != null : "fx:id=\"longitudeTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.powerLabel != null : "fx:id=\"powerLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.powerTextField != null : "fx:id=\"powerTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.prfAgilityComboBox != null : "fx:id=\"prfAgilityComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.prfAgilityLabel != null : "fx:id=\"prfAgilityLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.sidcComboBox != null : "fx:id=\"sidcComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.sidcDimComboBox != null : "fx:id=\"sidcDimComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.sidcIDComboBox != null : "fx:id=\"sidcIDComboBox\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.spotNumberTextField != null : "fx:id=\"spotNumberTextField\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";
	assert this.spotnumberLabel != null : "fx:id=\"spotnumberLabel\" was not injected: check your FXML file 'EMISSIONPanel.fxml'.";

	Tooltip tooltipEmissionID = new Tooltip("A positive identification unique number of the emission chosen by the sender of this message. This number should also be unique in terms of contact numbers.");
	Tooltip tooltipDeleteFlag = new Tooltip("Contact has to be removed");

	// EmissionID
	this.emissionIdTextField.setTooltip(tooltipEmissionID);
	this.emissionIdTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.equals("")) {
		this.contactIdLabel.setVisible(false);
	    } else {
		this.contactIdLabel.setVisible(true);
	    }
	});
	this.emissionIdTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	// DeleteFlaGComboBo
	this.deleteFlagComboBox.setItems(FXCollections.observableList(Arrays.asList(DeleteFlag.values())));
	this.deleteFlagComboBox.getSelectionModel().select(0);
	this.deleteFlagComboBox.setTooltip(tooltipDeleteFlag);

	// Sensor Latitude
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

	// Sensor Longitude
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

	// Sensor Altitude
	this.altitudeTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.altitudeLabel.setVisible(false);

	// Emitter Latitude
	this.emitterLatTextField.setTextFormatter(MessagePanelController.createLatitudeFormatter());
	this.emitterLatTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("-")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.emitterLatTextField.setText(this.latitudeFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.emitterLatTextField.getSelection().getLength() == 0) {
		    this.emitterLatTextField.setText(oldValue);
		}
	    }
	});

	this.emitterLatTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		validateAndFormatLatField(this.emitterLatTextField);
	    }
	});

	this.emitterLatLabel.setVisible(false);

	// Emitter Longitude
	this.emitterLongTextField.setTextFormatter(MessagePanelController.createLongitudeFormatter());
	this.emitterLongTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("-")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.emitterLongTextField.setText(this.longitudeFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.emitterLongTextField.getSelection().getLength() == 0) {
		    this.emitterLongTextField.setText(oldValue);
		}
	    }
	});

	this.emitterLongTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		validateAndFormatLongField(this.emitterLongTextField);
	    }
	});

	this.emitterLongLabel.setVisible(false);

	// Emitter Altitude
	this.emitterAltTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.emitterAltLabel.setVisible(false);

	// Bearing
	this.bearingTextField.setTextFormatter(MessagePanelController.createBearingFormatter());
	this.bearingTextField.textProperty().addListener((_, oldValue, newValue) -> {
	    if (!newValue.isEmpty() && !newValue.equals("")) {
		try {
		    double value = Double.parseDouble(newValue);
		    this.bearingTextField.setText(this.bearingFormat.format(value));
		} catch (NumberFormatException e) {
		    // Ignorieren, da der Filter ungültige Eingaben bereits verhindert
		}
	    } else if (newValue.isEmpty()) {
		if (oldValue != null && oldValue.length() > 1 && this.bearingTextField.getSelection().getLength() == 0) {
		    this.bearingTextField.setText(oldValue);
		}
	    }
	});

	this.bearingTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		this.bearingLabel.setVisible(!validateAndFormatCourseField(this.bearingTextField));
	    }
	});

	// Frequency
	this.frequenciesTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("[0-9#]*")) {
		return change;
	    }
	    return null;
	}));
	this.frequenciesLabel.setVisible(false);

	this.frequenciesTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		frequenciesInput(this.frequenciesTextField.getText());
	    }
	});
	// Bandwidth
	this.bandwithTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.bandwidthLabel.setVisible(false);

	// Power
	this.powerTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));
	this.powerLabel.setVisible(false);

	// Frequency Agility
	this.frequencAgilityComboBox.setItems(FXCollections.observableList(Arrays.asList(FreqAgility.values())));
	this.frequencAgilityComboBox.getSelectionModel().select(0);
	this.freqAgilityLabel.setVisible(false);
	// PRF Agility
	this.prfAgilityComboBox.setItems(FXCollections.observableList(Arrays.asList(PRFAgility.values())));
	this.prfAgilityComboBox.getSelectionModel().select(0);
	this.prfAgilityLabel.setVisible(false);

	// Function
	this.functionComboBox.setItems(FXCollections.observableList(Arrays.asList(Function.values())));
	this.functionComboBox.getSelectionModel().select(0);
	this.functionLabel.setVisible(false);

	// SpotNumber
	this.spotnumberLabel.setVisible(false);

	// SIDC ComboBoxen
	this.sidcIDComboBox.setItems(FXCollections.observableList(MessagePanelController.identitiesList));
	this.sidcIDComboBox.getSelectionModel().select(0);
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
	this.sidcDimComboBox.getSelectionModel().select(2);
	this.sidcComboBox.getSelectionModel().select(5);
	setSIDC();

    }

    private List<Double> frequencies = new ArrayList<>();;
    private char[] sidc;

    private void setSIDC() {
	StringBuilder sb = new StringBuilder(MessagePanelController.getDimensionCharFromIndex(this.sidcDimComboBox.getSelectionModel().getSelectedIndex())
		+ " " + this.sidcComboBox.getSelectionModel().getSelectedItem());
	sb.setCharAt(1, MessagePanelController.getIdentiy(this.sidcIDComboBox.getValue()));
	int pipeIndex = sb.indexOf("|");
	if (pipeIndex != -1) {
	    sb.setLength(pipeIndex);
	}
	sb.append("-----");
	String tmp = sb.toString();
	this.sidc = tmp.toCharArray();
    }

    private void frequenciesInput(String input) {
	String[] numbers = input.split("#");
	this.frequencies.clear();
	for (String number : numbers) {
	    if (!number.isEmpty()) {
		this.frequencies.add(Double.parseDouble(number));
	    }
	}
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

    DecimalFormat latitudeFormat = new DecimalFormat("00.0000");
    DecimalFormat longitudeFormat = new DecimalFormat("000.0000");
    DecimalFormat bearingFormat = new DecimalFormat("000.0000");

    @Override
    public EMISSION createMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

	EMISSION emission = new EMISSION(number, time, sender,
		classification, acknowledgement, mac,
		this.emissionIdTextField.getText(),
		this.deleteFlagComboBox.getValue(),
		Double.parseDouble(this.latitudeTextField.getText()),
		Double.parseDouble(this.longitudeTextField.getText()),
		this.altitudeTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.altitudeTextField.getText()),
		this.emitterLatTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.emitterLatTextField.getText()),
		this.emitterLongTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.emitterLongTextField.getText()),
		this.emitterAltTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.emitterAltTextField.getText()),
		this.bearingTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.bearingTextField.getText()),
		this.frequencies.isEmpty() ? null : this.frequencies,
		this.bandwithTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.bandwithTextField.getText()),
		this.powerTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(this.powerTextField.getText()),
		this.frequencAgilityComboBox.getValue(),
		this.prfAgilityComboBox.getValue(),
		this.functionComboBox.getValue(),
		this.spotNumberTextField.getText().isEmpty() ? 0 : Integer.parseInt(this.spotNumberTextField.getText()),
		this.sidc,
		this.commentTextArea.getText());

	return emission;
    }

    @Override
    public boolean isValidFilled() {
	// nur Pflichtfelder zur Überprüfung, ab wann eine Nachricht gesendet werden kann
	return (!this.contactIdLabel.isVisible() && !this.latitudeLabel.isVisible() && !this.longitudeLabel.isVisible() && !this.bearingLabel.isVisible());
    }

}
