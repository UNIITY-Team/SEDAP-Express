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

import java.util.Arrays;

import de.bundeswehr.uniity.sedapexpress.messages.RESEND;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * 
 * @author Volker Voß
 *
 */
public class RESENDPanelController extends MessagePanelController {

    @FXML
    private Label contactIdLabel;

    @FXML
    private TextField contactIdTextField;

    @FXML
    private ComboBox<MessageType> nameComboBox;

    @FXML
    private Label numberLabel;

    @FXML
    private TextField numberTextField;

    @FXML
    void initialize() {
	assert this.contactIdLabel != null : "fx:id=\"contactIdLabel\" was not injected: check your FXML file 'RESENDPanel.fxml'.";
	assert this.contactIdTextField != null : "fx:id=\"contactIdTextField\" was not injected: check your FXML file 'RESENDPanel.fxml'.";
	assert this.nameComboBox != null : "fx:id=\"nameComboBox\" was not injected: check your FXML file 'RESENDPanel.fxml'.";
	assert this.numberLabel != null : "fx:id=\"numberLabel\" was not injected: check your FXML file 'RESENDPanel.fxml'.";
	assert this.numberTextField != null : "fx:id=\"numberTextField\" was not injected: check your FXML file 'RESENDPanel.fxml'.";

	Tooltip tooltipID = new Tooltip("You can use a freely selected textual identifiers, as explained in the table from chapter IV.1.1.");
	Tooltip tooltipName = new Tooltip("The name of the message which should resend");
	Tooltip tooltipNumber = new Tooltip("hexadecimal string \r\n"
		+ "representation of an 8-bit \r\n"
		+ "00 - FF");

	this.contactIdTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.equals("")) {
		this.contactIdLabel.setVisible(false);
	    } else {
		this.contactIdLabel.setVisible(true);
	    }
	});

	this.contactIdTextField.setTooltip(tooltipID);

	this.nameComboBox.setItems(FXCollections.observableList(Arrays.asList(MessageType.values())));
	this.nameComboBox.getSelectionModel().select(1);
	this.nameComboBox.setTooltip(tooltipName);

	this.numberTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.equals("")) {
		if (!newValue.equals(newValue.toUpperCase())) {
		    this.numberTextField.setText(newValue.toUpperCase());
		}
		if (MessagePanelController.isValid8BitHex(newValue) == true) {
		    this.numberLabel.setVisible(false);
		} else {
		    this.numberLabel.setVisible(true);
		}
	    } else {
		this.numberTextField.setText("");
		this.numberLabel.setVisible(true);
	    }
	});

	this.numberTextField.setTooltip(tooltipNumber);
	// numberTextField.setTextFormatter(MessagePanelController.createHexFormatter());

    }
    // private Short numberOfTheMissingMessage;

    @Override
    public RESEND createMessage(Byte number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

	RESEND resend = new RESEND(number, time, sender,
		classification, acknowledgement, mac,
		this.contactIdTextField.getText(),
		this.nameComboBox.getValue(),
		Byte.parseByte(this.numberTextField.getText(), 16));

	return resend;
    }

    @Override
    public boolean isValidFilled() {

	return !this.contactIdLabel.isVisible() && !this.numberLabel.isVisible();
    }
}
