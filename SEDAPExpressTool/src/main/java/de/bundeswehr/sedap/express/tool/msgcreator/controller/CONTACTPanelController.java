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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import de.bundeswehr.sedap.express.tool.msgcreator.MessageTool;
import de.bundeswehr.uniity.sedapexpress.messages.CONTACT;
import de.bundeswehr.uniity.sedapexpress.messages.CONTACT.Source;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DeleteFlag;
import de.bundeswehr.uniity.sedapexpress.messages.SIDCCodes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;

/**
 * 
 * @author Volker Voß
 *
 */
public class CONTACTPanelController extends MessagePanelController {

    @FXML
    private Label altitudeLabel;

    @FXML
    private TextField altitudeTextField;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Label contactIdLabel;

    @FXML
    private TextField contactIdTextField;

    @FXML
    private Label courseLabel;

    @FXML
    private TextField courseTextField;

    @FXML
    private ComboBox<DeleteFlag> deleteFlagComboBox;

    @FXML
    private Label headingLabel;

    @FXML
    private TextField headingTextField;

    @FXML
    private Label heightLabel;

    @FXML
    private TextField heightTextField;

    @FXML
    private Label icaoLabel;

    @FXML
    private TextField icaoTextField;

    @FXML
    private Label latitudeLabel;

    @FXML
    private TextField latitudeTextField;

    @FXML
    private Label lengthLabel;

    @FXML
    private TextField lengthTextField;

    @FXML
    private Button loadImageButton;

    @FXML
    private Label longitudeLabel;

    @FXML
    private TextField longitudeTextField;

    @FXML
    private Label mmsiLabel;

    @FXML
    private TextField mmsiTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label pitchLabel;

    @FXML
    private TextField pitchTextField;

    @FXML
    private Label relatveXdistanceLabel;

    @FXML
    private TextField relatveXdistanceTextField;

    @FXML
    private Label relatveYdistanceLabel;

    @FXML
    private TextField relatveYdistanceTextField;

    @FXML
    private Label relatveZdistanceLabel;

    @FXML
    private TextField relatveZdistanceTextField;

    @FXML
    private Label rollLabel;

    @FXML
    private TextField rollTextField;

    @FXML
    private ComboBox<String> sidcComboBox;

    @FXML
    private ComboBox<String> sidcDimComboBox;

    @FXML
    private ComboBox<String> sidcIDComboBox;

    @FXML
    private Label sourceLabel;

    @FXML
    private MenuButton sourceMenueButton;

    @FXML
    private Label speedLabel;

    @FXML
    private TextField speedTextField;

    @FXML
    private Label widthLabel;

    @FXML
    private TextField widthTextField;

    @FXML
    void loadImage(ActionEvent event) {

	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Select Graphic File");
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
		new FileChooser.ExtensionFilter("Alle Dateien", "*.*"));

	File selectedFile = fileChooser.showOpenDialog(MessageTool.getMainStage());

	if (selectedFile != null) {
	    try {
		this.imageData = Files.readAllBytes(Path.of(selectedFile.getPath()));
	    } catch (Exception e) {
		System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
	    }
	} else {
	    System.out.println("Keine Datei ausgewählt.");
	}

    }

    @FXML
    void initialize() {
	assert this.altitudeLabel != null : "fx:id=\"altitudeLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.altitudeTextField != null : "fx:id=\"altitudeTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.commentTextArea != null : "fx:id=\"commentTextArea\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.contactIdLabel != null : "fx:id=\"contactIdLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.contactIdTextField != null : "fx:id=\"contactIdTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.courseLabel != null : "fx:id=\"courseLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.courseTextField != null : "fx:id=\"courseTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.deleteFlagComboBox != null : "fx:id=\"deleteFlagComboBox\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.headingTextField != null : "fx:id=\"headingTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.heightLabel != null : "fx:id=\"heightLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.heightTextField != null : "fx:id=\"heightTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.icaoLabel != null : "fx:id=\"icaoLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.icaoTextField != null : "fx:id=\"icaoTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.latitudeLabel != null : "fx:id=\"latitudeLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.latitudeTextField != null : "fx:id=\"latitudeTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.lengthLabel != null : "fx:id=\"lengthLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.lengthTextField != null : "fx:id=\"lengthTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.loadImageButton != null : "fx:id=\"loadImageButton\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.longitudeLabel != null : "fx:id=\"longitudeLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.longitudeTextField != null : "fx:id=\"longitudeTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.mmsiLabel != null : "fx:id=\"mmsiLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.mmsiTextField != null : "fx:id=\"mmsiTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.pitchLabel != null : "fx:id=\"pitchLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.pitchTextField != null : "fx:id=\"pitchTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveXdistanceLabel != null : "fx:id=\"relatveXdistanceLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveXdistanceTextField != null : "fx:id=\"relatveXdistanceTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveYdistanceLabel != null : "fx:id=\"relatveYdistanceLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveYdistanceTextField != null : "fx:id=\"relatveYdistanceTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveZdistanceLabel != null : "fx:id=\"relatveZdistanceLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.relatveZdistanceTextField != null : "fx:id=\"relatveZdistanceTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.rollLabel != null : "fx:id=\"rollLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.rollTextField != null : "fx:id=\"rollTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.sidcComboBox != null : "fx:id=\"sidcComboBox\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.sidcDimComboBox != null : "fx:id=\"sidcDimComboBox\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.sidcIDComboBox != null : "fx:id=\"sidcIDComboBox\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.sourceLabel != null : "fx:id=\"sourceLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.sourceMenueButton != null : "fx:id=\"sourceMenueButton\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.speedLabel != null : "fx:id=\"speedLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.speedTextField != null : "fx:id=\"speedTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.widthLabel != null : "fx:id=\"widthLabel\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";
	assert this.widthTextField != null : "fx:id=\"widthTextField\" was not injected: check your FXML file 'CONTACTPanel.fxml'.";

	// TOOLTIPS
	Tooltip tooltipID = new Tooltip("A positive identification unique number or free text of the contact chosen by the sender of this message");
	Tooltip tooltipDeleteFlag = new Tooltip("Contact has to be removed");
	Tooltip tooltipICAO = new Tooltip("International Civil Aviation Organization");
	Tooltip tooltipImage = new Tooltip("Image data (JPG, PNG, TIF) encoded in BASE64");
	Tooltip tooltipComment = new Tooltip("Free text to the contact");
	this.icaoTextField.setTooltip(tooltipICAO);
	this.commentTextArea.setTooltip(tooltipComment);
	this.loadImageButton.setTooltip(tooltipImage);
	this.contactIdTextField.setTooltip(tooltipID);
	this.deleteFlagComboBox.setTooltip(tooltipDeleteFlag);

	// Contact ID
	this.contactIdTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.equals("")) {
		this.contactIdLabel.setVisible(false);
	    } else {
		this.contactIdLabel.setVisible(true);
	    }
	});

	// DeleteFlag
	this.deleteFlagComboBox.setItems(FXCollections.observableList(Arrays.asList(DeleteFlag.values())));
	this.deleteFlagComboBox.getSelectionModel().select(0);

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

	this.altitudeLabel.setVisible(false);

	// Rel. X-distance
	this.relatveXdistanceTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.relatveXdistanceLabel.setVisible(false);

	// Rel. Y-distance
	this.relatveYdistanceTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.relatveYdistanceLabel.setVisible(false);

	// Rel. Z-distance
	this.relatveZdistanceTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.relatveZdistanceLabel.setVisible(false);

	// Speed
	this.speedTextField.setTextFormatter(new TextFormatter<>(change -> {
	    // String newText = change.getControlNewText();
	    if (change.getControlNewText().matches(SEDAPExpressMessage.DOUBLE_MATCHER.toString()) || change.isDeleted()) {
		return change;
	    }
	    return null;
	}));
	this.speedLabel.setVisible(false);

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
	this.courseLabel.setVisible(false);

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
	/*
	 * headingTextField.focusedProperty().addListener((observable, oldValue,newValue) -> { if(!newValue) { headingLabel.setVisible(!validateAndFormatCourseField(headingTextField)); } });
	 */
	this.headingLabel.setVisible(false);

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
	/*
	 * rollTextField.focusedProperty().addListener((observable, oldValue,newValue) -> { if(!newValue) { rollLabel.setVisible(!validateAndFormatCourseField(rollTextField)); } });
	 */
	this.rollLabel.setVisible(false);

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
	/*
	 * pitchTextField.focusedProperty().addListener((observable, oldValue,newValue) -> { if(!newValue) { pitchLabel.setVisible(!validateAndFormatCourseField(pitchTextField)); } });
	 */
	this.pitchLabel.setVisible(false);

	// Name
	// keine Formatter oder Validatoren

	// Source ###############################################################################################################

	this.sourceMenueButton.setText("Sources");
	this.sourceMenueButton.getItems().clear();
	ObservableList<CheckMenuItem> items = FXCollections.observableArrayList();

	for (Source source : Source.values()) {
	    CheckMenuItem item = new CheckMenuItem(source.name() + " (" + source.getSourceValue() + ")");
	    item.setUserData(source);
	    items.add(item);
	}

	this.sourceMenueButton.getItems().addAll(items);

	this.sourceMenueButton.showingProperty().addListener((_, _, newValue) -> {
	    StringBuilder label = new StringBuilder();
	    this.source = "";
	    if (!newValue) {
		for (int i = 0; i < this.sourceMenueButton.getItems().size(); i++) {
		    if (items.get(i).isSelected()) {
			label.append(this.sourceMenueButton.getItems().get(i).getText().substring(0, this.sourceMenueButton.getItems().get(i).getText().length() - 3));
			this.source = this.source + this.sourceMenueButton.getItems().get(i).getText().charAt(this.sourceMenueButton.getItems().get(i).getText().length() - 2);
		    }
		}
		this.sourceLabel.setText(label.toString());
	    }
	});

	// Width
	this.widthTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.widthLabel.setVisible(false);

	// Length
	this.lengthTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.lengthLabel.setVisible(false);

	// Height
	this.heightTextField.setTextFormatter(new TextFormatter<>(change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		return change;
	    }
	    return null;
	}));

	this.heightLabel.setVisible(false);

	// SIDC
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
	this.sidcIDComboBox.valueProperty().addListener((_, _, newValue) -> {
	    if (newValue != null) {
		setSIDC();
	    } else {
		this.sidc = null;
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

	// MMSI
	// keine Formatter oder Validatoren
	this.mmsiLabel.setVisible(false);
	// ICAO
	// keine Formatter oder Validatoren
	this.icaoLabel.setVisible(false);
	// Comment
	// keine Formatter oder Validatoren

    }

    DecimalFormat latitudeFormat = new DecimalFormat("00.0000");
    DecimalFormat longitudeFormat = new DecimalFormat("000.0000");
    DecimalFormat bearingFormat = new DecimalFormat("000.0000");

    private byte[] imageData;
    private char[] sidc;
    private String source = "";

    Set<Source> selectedSources = EnumSet.noneOf(Source.class);

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

    @Override
    public CONTACT createMessage(Byte number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

	CONTACT contact = new CONTACT(number, time, sender,
		classification, acknowledgement, mac,

		this.contactIdTextField.getText(),
		this.deleteFlagComboBox.getValue(),
		Double.parseDouble(this.latitudeTextField.getText()),
		Double.parseDouble(this.longitudeTextField.getText()),
		this.altitudeTextField.getText().isEmpty() ? null : Double.parseDouble(this.altitudeTextField.getText()),
		this.relatveXdistanceTextField.getText().isEmpty() ? null : Double.parseDouble(this.relatveXdistanceTextField.getText()),
		this.relatveYdistanceTextField.getText().isEmpty() ? null : Double.parseDouble(this.relatveYdistanceTextField.getText()),
		this.relatveZdistanceTextField.getText().isEmpty() ? null : Double.parseDouble(this.relatveZdistanceTextField.getText()),
		this.speedTextField.getText().isEmpty() ? null : Double.parseDouble(this.speedTextField.getText()),
		this.courseTextField.getText().isEmpty() ? null : Double.parseDouble(this.courseTextField.getText()),
		this.headingTextField.getText().isEmpty() ? null : Double.parseDouble(this.headingTextField.getText()),
		this.rollTextField.getText().isEmpty() ? null : Double.parseDouble(this.rollTextField.getText()),
		this.pitchTextField.getText().isEmpty() ? null : Double.parseDouble(this.pitchTextField.getText()),
		this.widthTextField.getText().isEmpty() ? null : Double.parseDouble(this.widthTextField.getText()),
		this.lengthTextField.getText().isEmpty() ? null : Double.parseDouble(this.lengthTextField.getText()),
		this.heightTextField.getText().isEmpty() ? null : Double.parseDouble(this.heightTextField.getText()),
		this.nameTextField.getText(),
		this.source,
		this.sidc,
		this.mmsiTextField.getText(),
		this.icaoTextField.getText(),
		this.imageData,
		this.commentTextArea.getText());

	return contact;
    }

    @Override
    public boolean isValidFilled() {

	return (!this.contactIdLabel.isVisible() && !this.latitudeLabel.isVisible() && !this.longitudeLabel.isVisible());
    }

}
