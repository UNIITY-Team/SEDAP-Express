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
package de.bundeswehr.mese.sedapexpress.controls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SEDAPExpressLoggingArea extends GridPane {

    private final ListView<String> textList = new ListView<>();

    private final ToggleButton startStopLoggingButton;

    private final boolean pause = false;

    @FXML
    public String text;

    @FXML
    public int maxLogEntries = 50;

    @FXML
    public int deleteSize = 20;

    private boolean selected;

    @FXML
    public String getText() {
	return this.text;
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
	this.text = text;
    }

    /**
     * @return the maxLogEntries
     */
    public int getMaxLogEntries() {
	return this.maxLogEntries;
    }

    /**
     * @param maxLogEntries the maxLogEntries to set
     */
    public void setMaxLogEntries(final int maxLogEntries) {
	this.maxLogEntries = maxLogEntries;
    }

    /**
     * @return the deleteSize
     */
    public int getDeleteSize() {
	return this.deleteSize;
    }

    /**
     * @param deleteSize the deleteSize to set
     */
    public void setDeleteSize(final int deleteSize) {
	this.deleteSize = deleteSize;
    }

    public SEDAPExpressLoggingArea() {

	final Button saveButton = new Button("Save");
	saveButton.setOnAction(_ -> {
	    final FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save logging to a file");
	    fileChooser.setInitialDirectory(new File("."));
	    fileChooser.getExtensionFilters().add(new ExtensionFilter("Logging files", "*.txt"));

	    final File logFile = fileChooser.showSaveDialog(null);

	    if (logFile != null) {

		try (BufferedWriter out = new BufferedWriter(new FileWriter(logFile, false))) {

		    for (final String entrie : this.textList.getItems()) {
			out.write(entrie);
			out.newLine();
		    }

		} catch (final IOException e) {

		}
	    }
	});

	final Button clipboardButton = new Button("Clipboard");
	clipboardButton.setOnAction(_ -> {
	    final StringBuilder allText = new StringBuilder();
	    this.textList.getItems().forEach(allText::append);
	    final HashMap<DataFormat, Object> data = new HashMap<>();
	    data.put(DataFormat.PLAIN_TEXT, allText.toString());
	    Clipboard.getSystemClipboard().setContent(data);
	});

	final Button clearButton = new Button("Clear");
	clearButton.setOnAction(_ -> {
	    this.textList.getItems().clear();
	});

	this.startStopLoggingButton = new ToggleButton("Stop logging");
	this.startStopLoggingButton.selectedProperty().addListener((_, _, n) -> {
	    this.selected = n;
	});

	setPrefWidth(Region.USE_COMPUTED_SIZE);
	setPrefHeight(Region.USE_COMPUTED_SIZE);
	setMaxWidth(Double.MAX_VALUE);
	setMaxHeight(Double.MAX_VALUE);

	setVgap(5);
	this.textList.setPrefHeight(50);

	final ButtonBar buttonBar = new ButtonBar();
	buttonBar.getButtons().add(saveButton);
	buttonBar.getButtons().add(clipboardButton);
	buttonBar.getButtons().add(clearButton);
	buttonBar.getButtons().add(this.startStopLoggingButton);

	add(this.textList, 0, 0, 1, 1);
	add(buttonBar, 0, 1, 1, 1);

	final ColumnConstraints colContrains = new ColumnConstraints();
	colContrains.setMaxWidth(Double.MAX_VALUE);
	colContrains.setHgrow(Priority.ALWAYS);

	final RowConstraints rowContrains = new RowConstraints();
	rowContrains.setMaxHeight(Double.MAX_VALUE);
	rowContrains.setVgrow(Priority.ALWAYS);

	getColumnConstraints().add(colContrains);
	getRowConstraints().add(rowContrains);
    }

    /**
     *
     * @param logText
     */
    public void log(final String logText) {

	if (this.selected || (logText == null)) {
	    return;
	}

	// System.out.println(logText);
	if (Platform.isFxApplicationThread()) {
	    if (this.textList.getItems().size() >= this.maxLogEntries) {
		this.textList.getItems().remove(0, this.deleteSize);
	    }
	    this.textList.getItems().add(logText.trim());
	    this.textList.scrollTo(this.textList.getItems().size());
	} else {
	    Platform.runLater(() -> {
		if (this.textList.getItems().size() >= this.maxLogEntries) {
		    this.textList.getItems().remove(0, this.deleteSize);
		}
		this.textList.getItems().add(logText.trim());
		this.textList.scrollTo(this.textList.getItems().size());
	    });
	}
    }

    public void newLine() {
	if (this.pause) {
	    return;
	}
	this.textList.getItems().add("\n");
    }

    public void clearLog() {
	this.textList.getItems().clear();
    }

    public void setTooltipText(final String text) {
	this.textList.setTooltip(new Tooltip(text));
    }

    public void deactivateLogging() {
	this.startStopLoggingButton.setSelected(true);
	this.selected = true;
    }

    public void activateLogging() {
	this.startStopLoggingButton.setSelected(false);
	this.selected = false;
    }

    public boolean isLoggingActive() {

	return !this.selected;
    }

}
