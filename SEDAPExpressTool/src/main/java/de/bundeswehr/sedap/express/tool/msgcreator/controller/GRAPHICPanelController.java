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

import de.bundeswehr.uniity.sedapexpress.messages.GRAPHIC;
import de.bundeswehr.uniity.sedapexpress.messages.GRAPHIC.GraphicType;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
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
public class GRAPHICPanelController extends MessagePanelController {

    @FXML
    private TextArea annotationTextArea;

    @FXML
    private Label contactIdLabel;

    @FXML
    private ComboBox<DataEncoding> encodingComboBox;

    @FXML
    private TextField fillColorTextField;

    @FXML
    private ComboBox<GraphicType> graficTypeComboBox;

    @FXML
    private TextField lineColorTextField;

    @FXML
    private TextField lineWidthTextField;

    @FXML
    private TextField textColorTextField;

    @FXML
    void initialize() {
	assert this.annotationTextArea != null : "fx:id=\"annotationTextArea\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.contactIdLabel != null : "fx:id=\"contactIdLabel\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.encodingComboBox != null : "fx:id=\"encodingComboBox\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.fillColorTextField != null : "fx:id=\"fillColorTextField\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.graficTypeComboBox != null : "fx:id=\"graficTypeComboBox\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.lineColorTextField != null : "fx:id=\"lineColorTextField\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.lineWidthTextField != null : "fx:id=\"lineWidthTextField\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";
	assert this.textColorTextField != null : "fx:id=\"textColorTextField\" was not injected: check your FXML file 'GRAPHICPanel.fxml'.";

	// TODO: Tooltips anpassen und zuweisen
	Tooltip tooltipLineColor = new Tooltip("Color of the line or the point in Web notation 800000FF for a darker red");
	Tooltip tooltipFillColor = new Tooltip("Color of the line or the point in Web notation 00FF0080 for translucent green");
	Tooltip tooltipTextColor = new Tooltip("Color of the line or the point in Web notation 0000FF80 for translucent blue");
	Tooltip tooltipAnnotation = new Tooltip("Text for an annotation to this graphic");
	this.lineColorTextField.setTooltip(tooltipLineColor);
	this.fillColorTextField.setTooltip(tooltipFillColor);
	this.textColorTextField.setTooltip(tooltipTextColor);
	this.annotationTextArea.setTooltip(tooltipAnnotation);

	// GraphicType
	this.graficTypeComboBox.setItems(FXCollections.observableList(Arrays.asList(GraphicType.values())));
	this.graficTypeComboBox.getSelectionModel().select(0);

	this.graficTypeComboBox.setCellFactory(new Callback<ListView<GraphicType>, ListCell<GraphicType>>() {
	    @Override
	    public ListCell<GraphicType> call(ListView<GraphicType> l) {
		return new ListCell<GraphicType>() {
		    @Override
		    protected void updateItem(GraphicType item, boolean empty) {
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
	this.graficTypeComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(GraphicType item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});
	// Encoding
	this.encodingComboBox.setItems(FXCollections.observableList(Arrays.asList(DataEncoding.values())));
	this.encodingComboBox.getSelectionModel().select(0);

	// LineWidth
	this.lineWidthTextField.setTextFormatter(MessagePanelController.posIntegerFormatter);
	this.lineWidthTextField.setText("3");
	// LineColor
	this.lineColorTextField.setTextFormatter(MessagePanelController.createRGBAHexFormatter());

	// FillColor
	this.fillColorTextField.setTextFormatter(MessagePanelController.createRGBAHexFormatter());
    }

    @Override
    public GRAPHIC createMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

	GRAPHIC graphic = new GRAPHIC();
//	GRAPHIC graphic = new GRAPHIC(number, time, sender, classification, acknowledgement, mac,
//		this.graficTypeComboBox.getValue(),
//		this.lineWidthTextField.getText() != null ? Double.parseDouble(this.lineWidthTextField.getText()) : null,
//		!this.lineColorTextField.getText().equals("") ? Integer.parseInt(this.lineColorTextField.getText()) : null,
//		!this.fillColorTextField.getText().equals("") ? Integer.parseInt(this.fillColorTextField.getText()) : null,
//		!this.textColorTextField.getText().equals("") ? Integer.parseInt(this.textColorTextField.getText()) : null,
//		this.encodingComboBox.getValue(), this.annotationTextArea.getText());

	return graphic;
    }

    @Override
    public boolean isValidFilled() {

	return true;
    }
}
