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

import de.bundeswehr.uniity.sedapexpress.messages.GENERIC;
import de.bundeswehr.uniity.sedapexpress.messages.GENERIC.ContentType;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;

/**
 * 
 * @author Volker Voß
 *
 */
public class GENERICPanelController extends MessagePanelController {

    @FXML
    private ComboBox<ContentType> contentComboBox;

    @FXML
    private ComboBox<DataEncoding> encodingComboBox;

    @FXML
    private TextArea contentTeatArea;

    @FXML
    void initialize() {
	assert this.contentComboBox != null : "fx:id=\"contentComboBox\" was not injected: check your FXML file 'GENERICPanel.fxml'.";
	assert this.contentTeatArea != null : "fx:id=\"contentTeatArea\" was not injected: check your FXML file 'GENERICPanel.fxml'.";
	assert this.encodingComboBox != null : "fx:id=\"encodingComboBox\" was not injected: check your FXML file 'GENERICPanel.fxml'.";

	Tooltip tooltipContent = new Tooltip("Choose trype of content");
	Tooltip tooltipEncoding = new Tooltip("Content can be BASE64 encoded or not encoded");
	Tooltip tooltipTextArea = new Tooltip("Any content in printable ASCII or BASE64 encoded");

	this.contentComboBox.setItems(FXCollections.observableArrayList(ContentType.values()));
	this.contentComboBox.getSelectionModel().select(0);
	this.contentComboBox.setTooltip(tooltipContent);
	this.encodingComboBox.setItems(FXCollections.observableArrayList(DataEncoding.values()));
	this.encodingComboBox.getSelectionModel().select(0);
	this.encodingComboBox.setTooltip(tooltipEncoding);
	this.contentTeatArea.setTooltip(tooltipTextArea);
    }
    // private String contentType;
    // private String encoding;
    // private String content;

    @Override
    public GENERIC createMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {
// TODO:  GENERIC muss angepasst werden, content und encoding sind String, sollten enum sein
	GENERIC generic = new GENERIC(number, time, sender, classification, acknowledgement, mac, this.contentComboBox.getValue(), this.encodingComboBox.getValue(), this.contentTeatArea.getText());

	return generic;
    }

    @Override
    public boolean isValidFilled() {

	return true;
    }
}
