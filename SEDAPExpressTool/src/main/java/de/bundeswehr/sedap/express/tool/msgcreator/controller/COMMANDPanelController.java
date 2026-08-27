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
import java.util.Arrays;

import de.bundeswehr.uniity.sedapexpress.messages.COMMAND;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.CommandFlag;
import de.bundeswehr.uniity.sedapexpress.messages.COMMAND.CommandType;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
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
public class COMMANDPanelController extends MessagePanelController {

    private Short commandID;

    @FXML
    private ComboBox<COMMAND.CommandFlag> cmdFlagComboBox;

    @FXML
    private TextArea addParametersTextArea;

    @FXML
    private Label cmdFlagLabel;

    @FXML
    private TextField cmdIDTextField;

    @FXML
    private ComboBox<COMMAND.CommandType> cmdTypeComboBox;

    @FXML
    private Label recipientLabel;

    @FXML
    private TextField recipientTextField;

    @FXML
    private Label cmdTypeLabel;

    @FXML
    void initialize() {
	assert this.cmdFlagComboBox != null : "fx:id=\"cmdFlagComboBox\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.addParametersTextArea != null : "fx:id=\"addParametersTextArea\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.cmdFlagLabel != null : "fx:id=\"cmdFlagLabel\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.cmdIDTextField != null : "fx:id=\"cmdIDTextField\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.cmdTypeComboBox != null : "fx:id=\"cmdTypeComboBox\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.cmdTypeLabel != null : "fx:id=\"cmdTypeLabel\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.recipientLabel != null : "fx:id=\"recipientLabel\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";
	assert this.recipientTextField != null : "fx:id=\"recipientTextField\" was not injected: check your FXML file 'COMMANDPanel.fxml'.";

	// TOOLTIPS
	Tooltip tooltipID = new Tooltip("Hexadecimal identifier of the command (0000 means all last commands)");
	Tooltip tooltipRecipient = new Tooltip("You can use a freely selected textual identifiers, as explained in the table from chapter IV.1.1.");
	Tooltip tooltipAdditional = new Tooltip("additional cmdType-dependent parameters, see chapter 2.7 SEDAP Express ICD");

	this.addParametersTextArea.setTooltip(tooltipAdditional);
	this.cmdIDTextField.setTooltip(tooltipID);
	this.recipientTextField.setTooltip(tooltipRecipient);

	// CommandTyp ComboBox
	this.cmdTypeComboBox.setItems(FXCollections.observableArrayList(CommandType.values()));
	this.cmdTypeComboBox.setCellFactory(new Callback<ListView<CommandType>, ListCell<CommandType>>() {
	    @Override
	    public ListCell<CommandType> call(ListView<CommandType> l) {
		return new ListCell<CommandType>() {
		    @Override
		    protected void updateItem(CommandType item, boolean empty) {
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
	this.cmdTypeComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(CommandType item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.cmdTypeComboBox.getSelectionModel().select(1);
	this.cmdTypeLabel.setVisible(false);

	// CommandFlag ComboBox
	this.cmdFlagComboBox.setItems(FXCollections.observableArrayList(CommandFlag.values()));
	this.cmdFlagComboBox.setCellFactory(new Callback<ListView<CommandFlag>, ListCell<CommandFlag>>() {
	    @Override
	    public ListCell<CommandFlag> call(ListView<CommandFlag> l) {
		return new ListCell<CommandFlag>() {
		    @Override
		    protected void updateItem(CommandFlag item, boolean empty) {
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
	this.cmdFlagComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(CommandFlag item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.cmdFlagComboBox.getSelectionModel().select(1);
	this.cmdFlagLabel.setVisible(false);

	this.recipientTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.equals("")) {
		this.recipientLabel.setVisible(false);
	    } else {
		this.recipientLabel.setVisible(true);
	    }
	});

	this.cmdIDTextField.setTextFormatter(MessagePanelController.createHexFormatter());
	this.cmdIDTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		try {
		    this.commandID = Short.parseShort(newValue, 16);
		} catch (NumberFormatException e) {
		    System.out.println("Ungültige Eingabe");
		}
	    } else {
		this.commandID = null;
	    }
	});

    }

    @Override // SEDAPExpressMessage
    public COMMAND createMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

	ArrayList<String> lines = new ArrayList<>(
		Arrays.asList(this.addParametersTextArea.getText().replace(System.lineSeparator(), ";").replace("\n", ";").split(";")));
	// letztes ";" abtrennen

	COMMAND command = new COMMAND();
//	COMMAND command = new COMMAND(number,
//		time,
//		sender,
//		classification,
//		acknowledgement,
//		mac,
//		this.recipientTextField.getText(),
//		this.commandID != null ? this.commandID : 0,
//		this.cmdFlagComboBox.getValue(),
//		this.cmdTypeComboBox.getValue(),
//		lines);

	return command;
    }

    @Override
    public boolean isValidFilled() {

	return (!this.cmdFlagLabel.isVisible() && !this.cmdTypeLabel.isVisible() && !this.recipientLabel.isVisible());
    }
}
