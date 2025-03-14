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
package de.bundeswehr.mese.sedapexpress.messagetool.controller;

import java.util.Arrays;

import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
import de.bundeswehr.mese.sedapexpress.messages.TEXT;
import de.bundeswehr.mese.sedapexpress.messages.TEXT.TextType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class TEXTPanelController extends MessagePanelController {

    @FXML
    private TextField recipientTextField;

    @FXML
    private ComboBox<DataEncoding> encodingComboBox;

    @FXML
    private ComboBox<TextType> typeComboBox;

    @FXML
    private TextArea textTextArea;

    @FXML
    void initialize() {
	assert this.recipientTextField != null : "fx:id=\"contactIdTextField\" was not injected: check your FXML file 'TEXTPanel.fxml'.";
	assert this.encodingComboBox != null : "fx:id=\"encodingComboBox\" was not injected: check your FXML file 'TEXTPanel.fxml'.";
	assert this.typeComboBox != null : "fx:id=\"typeComboBox\" was not injected: check your FXML file 'TEXTPanel.fxml'.";

	// TOOLTIPS
	Tooltip toolTipTextArea = new Tooltip("Free text of the message. Field is mandatory");
	this.textTextArea.setTooltip(toolTipTextArea);

	// Encoding
	this.encodingComboBox.setItems(FXCollections.observableList(Arrays.asList(DataEncoding.values())));
	this.encodingComboBox.getSelectionModel().select(0);
	// Text Type
	this.typeComboBox.setItems(FXCollections.observableList(Arrays.asList(TextType.values())));
	this.typeComboBox.setCellFactory(new Callback<ListView<TextType>, ListCell<TextType>>() {
	    @Override
	    public ListCell<TextType> call(ListView<TextType> l) {
		return new ListCell<TextType>() {
		    @Override
		    protected void updateItem(TextType item, boolean empty) {
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
	this.typeComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(TextType item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	this.typeComboBox.getSelectionModel().select(0);
    }

    @Override
    public TEXT createMessage(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

	return new TEXT(number, time, sender, classification, acknowledgement, mac, this.recipientTextField.getText(), this.typeComboBox.getSelectionModel().getSelectedItem(), this.encodingComboBox.getSelectionModel().getSelectedItem(),
		this.textTextArea.getText(), null);
    }

    @Override
    public boolean isValidFilled() {

	return this.textTextArea.getText().isEmpty() ? false : true;
    }
}
