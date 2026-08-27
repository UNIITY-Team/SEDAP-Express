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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.CommandState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.OperationalState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.TechnicalState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

/**
 * 
 * @author Volker Voß
 *
 */
public class STATUSPanelController extends MessagePanelController {

    @FXML
    private TextField ammunitionLevelTextField;

    @FXML
    private Label ammunitionLevelLabel;

    @FXML
    private Label batterieLevelLabel;

    @FXML
    private TextField batterieLevelTextField;

    @FXML
    private TextField ipHostTextField;

    @FXML
    private Label cmdIdLabel;

    @FXML
    private TextField cmdIdTextField;

    @FXML
    private ComboBox<CommandState> cmdStateComboBox;

    @FXML
    private Label fuelLevelLabel;

    @FXML
    private TextField fuelLevelTextField;

    @FXML
    private Label ipHostLabel;

    @FXML
    private Label mediaLabel;

    @FXML
    private TextField mediaTextField;

    @FXML
    private ComboBox<TechnicalState> tecStateComboBox;

    @FXML
    private ComboBox<OperationalState> opsStateComboBox;

    @FXML
    private Label tecStatusLabel;

    @FXML
    private TextArea textTextArea;

    @FXML
    void initialize() {
	assert this.ammunitionLevelTextField != null : "fx:id=\"ammunitionLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ipHostTextField != null : "fx:id=\"ipHostTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ammunitionLevelLabel != null : "fx:id=\"ammunitionLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.batterieLevelLabel != null : "fx:id=\"batterieLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.batterieLevelTextField != null : "fx:id=\"batterieLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdIdLabel != null : "fx:id=\"cmdIdLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdIdTextField != null : "fx:id=\"cmdIdTextBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdStateComboBox != null : "fx:id=\"cmdStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.fuelLevelLabel != null : "fx:id=\"fuelLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.fuelLevelTextField != null : "fx:id=\"fuelLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ipHostLabel != null : "fx:id=\"ipHostLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.mediaLabel != null : "fx:id=\"mediaLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.mediaTextField != null : "fx:id=\"mediaTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.opsStateComboBox != null : "fx:id=\"optStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.tecStateComboBox != null : "fx:id=\"tecStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.tecStatusLabel != null : "fx:id=\"tecStatusLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.textTextArea != null : "fx:id=\"textTextArea\" was not injected: check your FXML file 'STATUSPanel.fxml'.";

	// keine Mandatory-Felder, daher kein invalid, die Felder können zum Ende hin auch gelöscht werden
	this.ammunitionLevelLabel.setVisible(false);
	this.fuelLevelLabel.setVisible(false);
	this.batterieLevelLabel.setVisible(false);
	this.cmdIdLabel.setVisible(false);
	this.ipHostLabel.setVisible(false);
	this.mediaLabel.setVisible(false);

	// TOOLTIPS
	Tooltip tooltipammunitionLevel = new Tooltip("Relative remaining ammunition: <name of the weapon>#<ammunition level>#.... \n Validation when field lost focus. \n Example: OTOMELARA#123#RAM#21#MLG_1#500");
	Tooltip tooltipbatterieLevel = new Tooltip("Relative remaining batterie capacity: <name of the batterie>#<batterie level>#.... \n Validation when field lost focus. \nExample: BAT_1#50#BAT_2#100#BAT_DROHNE#50");
	Tooltip tooltipfuelLevel = new Tooltip("Relative remaining fuel capacity: <name of the fuel tank>#<ammunition level>#.... \n Validation when field lost focus. \nExample: TANK1#5200#TANK2#300#TANK3#0");
	Tooltip tooltipCmdId = new Tooltip("ID of the releated command (message)");
	Tooltip tooltipIPHost = new Tooltip("IP or hostname of the platform");
	Tooltip tooltipMedia = new Tooltip("List of video stream or image URLs with ; as separator");
	Tooltip tooltipText = new Tooltip("Human readable free text description of the status");

	this.ammunitionLevelTextField.setTooltip(tooltipammunitionLevel);
	this.batterieLevelTextField.setTooltip(tooltipbatterieLevel);
	this.fuelLevelTextField.setTooltip(tooltipfuelLevel);
	this.cmdIdTextField.setTooltip(tooltipCmdId);
	this.ipHostTextField.setTooltip(tooltipIPHost);
	this.mediaTextField.setTooltip(tooltipMedia);
	this.textTextArea.setTooltip(tooltipText);

	// TecStateComboBox
	this.tecStateComboBox.setItems(FXCollections.observableArrayList(TechnicalState.values()));
	this.tecStateComboBox.setCellFactory(new Callback<ListView<TechnicalState>, ListCell<TechnicalState>>() {
	    @Override
	    public ListCell<TechnicalState> call(ListView<TechnicalState> l) {
		return new ListCell<TechnicalState>() {
		    @Override
		    protected void updateItem(TechnicalState item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
			    setGraphic(null);
			} else {
			    setText(item.name());
			}
		    }
		};
	    }
	});
	this.tecStateComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(TechnicalState item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.tecStateComboBox.getSelectionModel().select(1);

	// OpsStateComboBox
	this.opsStateComboBox.setItems(FXCollections.observableArrayList(OperationalState.values()));
	this.opsStateComboBox.setCellFactory(new Callback<ListView<OperationalState>, ListCell<OperationalState>>() {
	    @Override
	    public ListCell<OperationalState> call(ListView<OperationalState> l) {
		return new ListCell<OperationalState>() {
		    @Override
		    protected void updateItem(OperationalState item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
			    setGraphic(null);
			} else {
			    setText(item.name());
			}
		    }
		};
	    }
	});
	this.opsStateComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(OperationalState item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.opsStateComboBox.getSelectionModel().select(0);

	// AmmunitionLevelTextField
	this.ammunitionLevelTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		getAmmunitionLevel(this.ammunitionLevelTextField.getText());
	    }
	});

	// FuelLevelTextField
	this.fuelLevelTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		getFuelLevel(this.fuelLevelTextField.getText());
	    }
	});
	// BatterieLevelTextField
	this.batterieLevelTextField.focusedProperty().addListener((_, _, newValue) -> {
	    if (!newValue) {
		getBatterieLevel(this.batterieLevelTextField.getText());
	    }
	});

	// CmdID
	this.cmdIdTextField.setTextFormatter(MessagePanelController.createHexFormatter());
	this.cmdIdTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		try {
		    this.commandID = Integer.parseInt(newValue, 16);
		} catch (NumberFormatException e) {
		    System.out.println("Ungültige Eingabe");
		}
	    } else {
		this.commandID = null;
	    }
	});

	// CmdStateComboBox
	this.cmdStateComboBox.setItems(FXCollections.observableArrayList(CommandState.values()));
	this.cmdStateComboBox.setCellFactory(new Callback<ListView<CommandState>, ListCell<CommandState>>() {
	    @Override
	    public ListCell<CommandState> call(ListView<CommandState> l) {
		return new ListCell<CommandState>() {
		    @Override
		    protected void updateItem(CommandState item, boolean empty) {
			super.updateItem(item, empty);
			if (item == null || empty) {
			    setGraphic(null);
			} else {
			    setText(item.name());
			}
		    }
		};
	    }
	});
	this.cmdStateComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(CommandState item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.cmdStateComboBox.getSelectionModel().select(0);

	// IP/Host
	this.ipHostTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		this.hostname = this.ipHostTextField.getText();
	    } else {
		this.hostname = null;
	    }
	});

	// Media
	this.mediaTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		getMedia(this.mediaTextField.getText());
	    } else {
		this.mediaUrls = null;
	    }
	});

	// Text
	this.textTextArea.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		this.freeText = this.textTextArea.getText();
	    } else {
		this.freeText = null;
	    }
	});

    }

    private List<Double> ammunitionLevels = new ArrayList<>();
    private List<Double> fuelLevels = new ArrayList<>();
    private List<Double> batterieLevels = new ArrayList<>();
    private List<String> ammunitionLevelNames = new ArrayList<>();
    private List<String> fuelLevelNames = new ArrayList<>();
    private List<String> batterieLevelNames = new ArrayList<>();
    private Integer commandID;
    private String hostname;
    private List<String> mediaUrls = new ArrayList<>();
    private String freeText = "";

    private void getAmmunitionLevel(String input) {
	String[] pairs = input.split("#");
	this.ammunitionLevelNames.clear();
	this.ammunitionLevels.clear();
	this.ammunitionLevelLabel.setVisible(false);
	for (int i = 0; i < pairs.length - 1; i += 2) {
	    try {
		this.ammunitionLevelNames.add(pairs[i].toString());
		this.ammunitionLevels.add(Double.parseDouble(pairs[i + 1]));
	    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
		this.ammunitionLevelLabel.setVisible(true);
		System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
		this.ammunitionLevelNames.remove(i);
	    }
	}
    }

    private void getFuelLevel(String input) {
	String[] pairs = input.split("#");
	this.fuelLevels.clear();
	this.fuelLevelNames.clear();
	this.fuelLevelLabel.setVisible(false);
	for (int i = 0; i < pairs.length - 1; i += 2) {
	    try {
		this.fuelLevelNames.add(pairs[i].toString());
		this.fuelLevels.add(Double.parseDouble(pairs[i + 1]));
	    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
		this.fuelLevelLabel.setVisible(true);
		System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
		this.fuelLevelNames.remove(i);
	    }
	}
    }

    private void getBatterieLevel(String input) {
	String[] pairs = input.split("#");
	this.batterieLevels.clear();
	this.batterieLevelNames.clear();
	this.batterieLevelLabel.setVisible(false);
	for (int i = 0; i < pairs.length - 1; i += 2) {
	    try {
		this.batterieLevelNames.add(pairs[i].toString());
		this.batterieLevels.add(Double.parseDouble(pairs[i + 1]));
	    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
		this.batterieLevelLabel.setVisible(true);
		System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
		this.batterieLevelNames.remove(i);
	    }
	}
    }

    private void getMedia(String input) {
	String[] medias = input.split(";");
	this.mediaUrls.clear();
	Collections.addAll(this.mediaUrls, medias);
    }

    @Override
    public STATUS createMessage(Byte number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

	if (this.mediaUrls != null && this.mediaUrls.isEmpty()) {
	    this.mediaUrls = null;
	}

	STATUS status = new STATUS(number, time, sender,
		classification, acknowledgement, mac,
		this.tecStateComboBox.getValue(),
		this.opsStateComboBox.getValue(),
		this.ammunitionLevelNames,
		this.ammunitionLevels,
		this.fuelLevelNames,
		this.fuelLevels,
		this.batterieLevelNames,
		this.batterieLevels,
		this.commandID != null ? this.commandID : 0,
		this.cmdStateComboBox.getValue(),
		this.hostname,
		this.mediaUrls != null ? this.mediaUrls : null,
		this.freeText != null ? this.freeText : null);

	return status;
    }

    @Override
    public boolean isValidFilled() {

	return !this.ammunitionLevelLabel.isVisible() && !this.batterieLevelLabel.isVisible() && !this.fuelLevelLabel.isVisible();
    }

}
