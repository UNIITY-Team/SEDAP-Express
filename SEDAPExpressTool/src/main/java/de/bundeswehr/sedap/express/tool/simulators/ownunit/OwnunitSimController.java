/*******************************************************************************
 * Copyright (C)2012-2026, German Federal Armed Forces - All rights reserved.
 *
 * This source code is part of the UNIITY (MESE) project.
 * Person of contact (POC): Volker Voß, volker1voss@bundeswehr.org
 *
 * Unauthorized use, modification, redistributing, copying, selling and
 * printing of this file in source and binary form including accompanying
 * materials is STRICTLY prohibited.
 *
 * This source code and it's parts is classified as OFFEN / NATO UNCLASSIFIED!
 *******************************************************************************/
package de.bundeswehr.sedap.express.tool.simulators.ownunit;

import java.util.TreeMap;

import de.bundeswehr.sedap.express.tool.SEDAPExpressTool;
import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class OwnunitSimController {

    private static TreeMap<String, String[]> presetMap = new TreeMap<>();
    private static ObservableList<String> presetList = FXCollections.observableArrayList();

    private Thread interpolationThread;
    private boolean runInterpolation = false;
    private double speedCalculationFactor = 1;

    private SEDAPExpressTool expressTool;

    @FXML
    private TextField altTextField;

    @FXML
    private TextField crsTextField;

    @FXML
    private CheckBox hdgCrsCheckBox;

    @FXML
    private TextField hdgTextField;

    @FXML
    private Label speedUnitLabel;

    @FXML
    private TextField latTextField;

    @FXML
    private TextField lonTextField;

    @FXML
    private ComboBox<String> presetComboBox;

    @FXML
    private ToggleButton simToggleButton;

    @FXML
    private TextField spdTextField;

    @FXML
    private ComboBox<String> spdUnitChooseBox;

    @FXML
    private RadioButton staticToggleButton;

    @FXML
    private RadioButton orbitToggleButton;

    @FXML
    private ToggleGroup veloToggleGroup;

    @FXML
    void simToggleButtonAction(final ActionEvent event) {
	if (this.runInterpolation) {
	    stopInterpolation();
	    Platform.runLater(() -> this.simToggleButton.setText("Start simulation"));

	} else {
	    startInterpolation();
	    Platform.runLater(() -> this.simToggleButton.setText("Stop simulation"));
	}
    }

    @FXML
    void initialize() {
	assert this.altTextField != null : "fx:id=\"altTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.crsTextField != null : "fx:id=\"crsTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.hdgCrsCheckBox != null : "fx:id=\"hdgCrsCheckBox\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.hdgTextField != null : "fx:id=\"hdgTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.latTextField != null : "fx:id=\"latTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.lonTextField != null : "fx:id=\"lonTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.orbitToggleButton != null : "fx:id=\"orbitToggleButton\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.presetComboBox != null : "fx:id=\"presetComboBox\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.simToggleButton != null : "fx:id=\"simToggleButton\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.spdTextField != null : "fx:id=\"spdTextField\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.spdUnitChooseBox != null : "fx:id=\"spdUnitChooseBox\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.speedUnitLabel != null : "fx:id=\"speedUnitLabel\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.staticToggleButton != null : "fx:id=\"staticToggleButton\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";
	assert this.veloToggleGroup != null : "fx:id=\"veloToggleGroup\" was not injected: check your FXML file 'OwnunitSimView.fxml'.";

	// Befüllen der Presets:
	// ORT, Lat, Lon ,Alt, HDg, CRS, SPD
	OwnunitSimController.presetMap.put("Hamburg", new String[] { "Hamburg", "53.5503", "10.000", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Bremen", new String[] { "Bremen", "53.0759", "8.8073", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Berlin", new String[] { "Berlin", "52.5186", "13.4083", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Bonn", new String[] { "Bonn", "50.7374", "7.0982", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Dortmund", new String[] { "Dortmund", "51.51388", "7.465278", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Frankfurt", new String[] { "Frankfurt", "50.11", "8.68", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Stuttgart", new String[] { "Stuttgart", "48.7755", "9.1828", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("München", new String[] { "München", "48.137", "11.576", "0", "0", "0", "0" });
	OwnunitSimController.presetMap.put("Erding", new String[] { "Erding", "48.306", "11.908", "0", "0", "0", "0" });

	OwnunitSimController.presetList.addAll(OwnunitSimController.presetMap.keySet());

	this.presetComboBox.setItems(OwnunitSimController.presetList);
	this.presetComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
	    final String[] preset = OwnunitSimController.presetMap.get(newVal);
	    Platform.runLater(() -> {
		this.latTextField.setText(preset[1]);
		this.lonTextField.setText(preset[2]);
		this.altTextField.setText(preset[3]);
		this.hdgTextField.setText(preset[4]);
		this.crsTextField.setText(preset[5]);
		this.spdTextField.setText(preset[6]);

	    });

	});
	this.presetComboBox.getSelectionModel().select(2);

	this.spdUnitChooseBox.setItems(FXCollections.observableArrayList("m/s", "km/h", "kn"));
	this.spdUnitChooseBox.getSelectionModel().selectedIndexProperty().addListener((_, _, nv) -> {
	    switch (nv.intValue()) {
	    case 0: { // m/s
		this.speedUnitLabel.setText("Speed [m/s]");
		this.speedCalculationFactor = 1;
		break;
	    }
	    case 1: { // km/h
		this.speedUnitLabel.setText("Speed [km/h]");
		this.speedCalculationFactor = 1d / 3.6d;
		break;
	    }
	    case 2: { // kn
		this.speedCalculationFactor = 1d / 3600d / 1852d;
		this.speedUnitLabel.setText("Speed [kn]");
		break;
	    }
	    }

	});

	this.hdgTextField.disableProperty().bind(this.hdgCrsCheckBox.selectedProperty());

	this.spdUnitChooseBox.getSelectionModel().select(0);

    }

    /**
     * 
     * @param expressTool
     */
    public OwnunitSimController(SEDAPExpressTool expressTool) {
	this.expressTool = expressTool;
    }

    /**
     * 
     * @param startPosition
     * @param azimuthDegrees
     * @param distanceMeters
     * @return
     */
    public static Position calculateNewPosition(Position startPosition, double azimuthDegrees, double distanceMeters) {

	// 1. Erdradius definieren (Standard-Erdradius in Metern, z. B. WGS84 Äquatorradius 6.378.137 m)
	double earthRadiusMeters = Earth.WGS84_EQUATORIAL_RADIUS; // ca. 6378137.0 m

	// 2. Azimut in Angle umwandeln
	Angle azimuth = Angle.fromDegrees(azimuthDegrees);

	// 3. Distanz in ein Angle-Objekt (Bogenlänge auf der Kugel) umrechnen: Radians = Distanz / Radius
	Angle pathLength = Angle.fromRadians(distanceMeters / earthRadiusMeters);

	// 4. Zielkoordinate über LatLon.greatCircleEndPosition berechnen
	LatLon targetLatLon = LatLon.greatCircleEndPosition(startPosition, azimuth, pathLength);

	// 5. Neue Position mit der ursprünglichen Höhe (Elevation) zurückgeben
	return new Position(targetLatLon, startPosition.getElevation());
    }

    /**
     * 
     * @param startPos
     * @param bearingDeg
     * @param speedMps
     * @param timeSec
     * 
     * @return neue Position
     */
    public static Position moveFromWithSpeed(Position startPos, double bearingDeg, double speedMps, double timeSec) {

	if (timeSec < 0)
	    timeSec = 0;
	double distanceM = speedMps * timeSec;

	return OwnunitSimController.calculateNewPosition(startPos, bearingDeg, distanceM);
    }

    public void startInterpolation() {

	this.runInterpolation = true;

	enableDisableFields(this.runInterpolation);

	this.interpolationThread = new Thread(() -> {

	    final OWNUNIT ownunitMessage = new OWNUNIT();

	    ownunitMessage.setSender("SIM");
	    ownunitMessage.setClassification(Classification.Public);
	    ownunitMessage.setAcknowledgement(Acknowledgement.FALSE);
	    ownunitMessage.setMAC(null);

	    ownunitMessage.setName("SimTrack");
	    ownunitMessage.setSIDC("SFSPO----------".toCharArray());
	    ownunitMessage.setPitch(0.0);
	    ownunitMessage.setRoll(0.0);

	    byte messageCounter = 0;

	    readDataIntoMessage(ownunitMessage);

	    Position pos = Position.fromDegrees(ownunitMessage.getLatitude(), ownunitMessage.getLongitude(), ownunitMessage.getAltitude());

	    while (this.runInterpolation) {
		try {

		    pos = OwnunitSimController.moveFromWithSpeed(pos, ownunitMessage.getCourse(), ownunitMessage.getSpeed(), 1);

		    // Kurs anpassen gem. Interpolationmode
		    if (this.orbitToggleButton.isSelected()) {// Orbit

			// Kurs ändern in Abhängigkeit von der Geschwindigkeit
			if (ownunitMessage.getSpeed() == 0) {
			    ownunitMessage.setCourse((ownunitMessage.getCourse() + 0.25) % 360);
			} else {
			    ownunitMessage.setCourse((ownunitMessage.getCourse() + (Math.log(ownunitMessage.getSpeed() * 3.6) / 10)) % 360);
			}

		    }

		    if (this.hdgCrsCheckBox.isSelected()) {
			ownunitMessage.setHeading(ownunitMessage.getCourse());
		    } else {
			ownunitMessage.setHeading(Double.parseDouble(this.hdgTextField.getText()));
		    }

		    // Neue Daten in die Nachricht schreiben ...
		    ownunitMessage.setLatitude(pos.getLatitude().getDegrees());
		    ownunitMessage.setLongitude(pos.getLongitude().getDegrees());

		    ownunitMessage.setTime(System.currentTimeMillis());
		    ownunitMessage.setNumber(messageCounter++);
		    if (messageCounter == 0x7f)
			messageCounter = 0;

		    // Felder aktualisieren und Nachricht senden
		    writeToFields(ownunitMessage);

		    this.expressTool.sendSEDAPExpressMessage(ownunitMessage);

		    Thread.sleep(1000); // Updaterate = 1Hz
		} catch (final InterruptedException e) {
		    // wird nicht eintreten muss aber behandelt werden ¯\_(ツ)_/¯
		    e.printStackTrace();
		}
	    }
	});
	this.interpolationThread.start();
    }

    public void stopInterpolation() {

	this.runInterpolation = false;
	enableDisableFields(this.runInterpolation);
	this.interpolationThread = null;
    }

    /**
     * Aktiviert/Deaktiviert die Bearbeitbarkeit des Felder anhand des Simulationsstatus
     *
     * @param disable True zum aktivieren/False zum deaktivieren der Felder
     */
    private void enableDisableFields(final boolean disable) {
	Platform.runLater(() -> {
	    this.presetComboBox.setDisable(disable);
	    this.latTextField.setDisable(disable);
	    this.lonTextField.setDisable(disable);
	    this.altTextField.setDisable(disable);
	    this.crsTextField.setDisable(disable);
	    this.spdTextField.setDisable(disable);
	    this.spdUnitChooseBox.setDisable(disable);

	    if (!this.hdgCrsCheckBox.isSelected()) {
		this.hdgTextField.setDisable(disable);
	    }
	});

    }

    /**
     * Speichert den Inhalt der Textfelder in der angegebenen OWNUNIT Nachricht.
     *
     * @param msg OWNUNIT Message in die gespeichert werden soll.
     */
    private void readDataIntoMessage(final OWNUNIT msg) {

	msg.setLatitude(Double.parseDouble(this.latTextField.getText()));
	msg.setLongitude(Double.parseDouble(this.lonTextField.getText()));
	msg.setAltitude(Double.valueOf(this.altTextField.getText()));

	msg.setCourse(Double.parseDouble(this.crsTextField.getText()));
	msg.setSpeed(Double.valueOf(this.spdTextField.getText()) * this.speedCalculationFactor);

	if (this.hdgCrsCheckBox.isSelected()) {
	    msg.setHeading(Double.parseDouble(this.crsTextField.getText()));
	} else {
	    msg.setHeading(Double.parseDouble(this.hdgTextField.getText()));
	}

    }

    /**
     * Ändert den Inhalt der Textfelder zu den Daten aus der angegebenen OWNUNIT Nachricht.
     *
     * @param msg OWNUNIT Message die gelesen werden soll.
     */
    private void writeToFields(final OWNUNIT msg) {

	Platform.runLater(() -> {

	    this.latTextField.setText(String.valueOf(msg.getLatitude()));
	    this.lonTextField.setText(String.valueOf(msg.getLongitude()));
	    this.altTextField.setText(String.valueOf(msg.getAltitude()));

	    this.crsTextField.setText(String.valueOf(msg.getCourse()));
	    this.spdTextField.setText(String.valueOf(msg.getSpeed() * this.speedCalculationFactor));

	    if (this.hdgCrsCheckBox.isSelected()) {
		msg.setHeading(Double.parseDouble(this.crsTextField.getText()));
	    } else {
		msg.setHeading(Double.parseDouble(this.hdgTextField.getText()));
	    }

	});
    }

}
