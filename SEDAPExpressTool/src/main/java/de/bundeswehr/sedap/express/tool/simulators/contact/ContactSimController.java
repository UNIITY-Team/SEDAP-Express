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
package de.bundeswehr.sedap.express.tool.simulators.contact;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import de.bundeswehr.sedap.express.tool.SEDAPExpressTool;
import de.bundeswehr.sedap.express.tool.simulators.ownunit.OwnunitSimController;
import de.bundeswehr.uniity.sedapexpress.messages.CONTACT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import gov.nasa.worldwind.geom.Position;
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

public class ContactSimController {

    private static TreeMap<String, String[]> presetMap = new TreeMap<>();
    private static ObservableList<String> presetList = FXCollections.observableArrayList();
    private static AtomicInteger messageCounter = new AtomicInteger(0);

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
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField sidcTextField;

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
	assert this.altTextField != null : "fx:id=\"altTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.crsTextField != null : "fx:id=\"crsTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.hdgCrsCheckBox != null : "fx:id=\"hdgCrsCheckBox\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.hdgTextField != null : "fx:id=\"hdgTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.idTextField != null : "fx:id=\"idTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.latTextField != null : "fx:id=\"latTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.lonTextField != null : "fx:id=\"lonTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.orbitToggleButton != null : "fx:id=\"orbitToggleButton\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.presetComboBox != null : "fx:id=\"presetComboBox\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.sidcTextField != null : "fx:id=\"sidcTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.simToggleButton != null : "fx:id=\"simToggleButton\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.spdTextField != null : "fx:id=\"spdTextField\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.spdUnitChooseBox != null : "fx:id=\"spdUnitChooseBox\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.speedUnitLabel != null : "fx:id=\"speedUnitLabel\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.staticToggleButton != null : "fx:id=\"staticToggleButton\" was not injected: check your FXML file 'ContactSimView.fxml'.";
	assert this.veloToggleGroup != null : "fx:id=\"veloToggleGroup\" was not injected: check your FXML file 'ContactSimView.fxml'.";

	// Befüllen der Presets:
	// ORT, Lat, Lon ,Alt, HDg, CRS, SPD
	ContactSimController.presetMap.put("Hamburg", new String[] { "Hamburg", "53.7", "10.200", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Bremen", new String[] { "Bremen", "53.3", "8.5", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Berlin", new String[] { "Berlin", "52.7", "13.8", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Bonn", new String[] { "Bonn", "50.5", "7.2", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Dortmund", new String[] { "Dortmund", "51.71388", "7.6", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Frankfurt", new String[] { "Frankfurt", "50.3", "8.5", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Stuttgart", new String[] { "Stuttgart", "48.5", "9.4", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("München", new String[] { "München", "48.0", "11.7", "0", "0", "0", "0" });
	ContactSimController.presetMap.put("Erding", new String[] { "Erding", "48.1", "11.7", "0", "0", "0", "0" });

	ContactSimController.presetList.addAll(ContactSimController.presetMap.keySet());

	this.presetComboBox.setItems(ContactSimController.presetList);
	this.presetComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
	    final String[] preset = ContactSimController.presetMap.get(newVal);
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
     * @param contactID
     */
    public ContactSimController(SEDAPExpressTool expressTool, String contactID) {
	this.expressTool = expressTool;
	Platform.runLater(() -> this.idTextField.setText(contactID));
    }

    public void startInterpolation() {

	this.runInterpolation = true;

	enableDisableFields(this.runInterpolation);

	this.interpolationThread = new Thread(() -> {

	    final CONTACT contactMessage = new CONTACT();

	    contactMessage.setSender("SIM");
	    contactMessage.setClassification(Classification.Public);
	    contactMessage.setAcknowledgement(Acknowledgement.FALSE);
	    contactMessage.setMAC(null);

	    contactMessage.setContactID(this.idTextField.getText());
	    contactMessage.setName(this.nameTextField.getText());
	    contactMessage.setSIDC(this.sidcTextField.getText().toCharArray());

	    contactMessage.setPitch(0.0);
	    contactMessage.setRoll(0.0);

	    readDataIntoMessage(contactMessage);

	    Position pos = Position.fromDegrees(contactMessage.getLatitude(), contactMessage.getLongitude(), contactMessage.getAltitude());

	    while (this.runInterpolation) {
		try {

		    pos = OwnunitSimController.moveFromWithSpeed(pos, contactMessage.getCourse(), contactMessage.getSpeed(), 1);

		    // Kurs anpassen gem. Interpolationmode
		    if (this.orbitToggleButton.isSelected()) {// Orbit

			// Kurs ändern in Abhängigkeit von der Geschwindigkeit
			if (contactMessage.getSpeed() == 0) {
			    contactMessage.setCourse((contactMessage.getCourse() + 0.25) % 360);
			} else {
			    contactMessage.setCourse((contactMessage.getCourse() + (Math.log(contactMessage.getSpeed() * 3.6) / 10)) % 360);
			}

		    }

		    if (this.hdgCrsCheckBox.isSelected()) {
			contactMessage.setHeading(contactMessage.getCourse());
		    } else {
			contactMessage.setHeading(Double.parseDouble(this.hdgTextField.getText()));
		    }

		    // Neue Daten in die Nachricht schreiben ...
		    contactMessage.setLatitude(pos.getLatitude().getDegrees());
		    contactMessage.setLongitude(pos.getLongitude().getDegrees());

		    contactMessage.setTime(System.currentTimeMillis());
		    contactMessage.setNumber((byte) ContactSimController.messageCounter.addAndGet(1));
		    ContactSimController.messageCounter.compareAndExchange(0x7f, -1);

		    // Felder aktualisieren und Nachricht senden
		    writeToFields(contactMessage);

		    this.expressTool.sendSEDAPExpressMessage(contactMessage);

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
    private void readDataIntoMessage(final CONTACT msg) {

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
    private void writeToFields(final CONTACT msg) {

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

    public static class CourseEntry {
	private Integer nbr;
	private Double course;
	private Double speed;
	private Double heading;
	private Double timeToFollow;

	public CourseEntry(final int nbr, final double course, final double speed, final double heading, final double timeToFollow) {
	    super();
	    this.nbr = nbr;
	    this.course = course;
	    this.speed = speed;
	    this.heading = heading;
	    this.timeToFollow = timeToFollow;
	}

	public CourseEntry(final int nbr) {
	    this();
	    this.nbr = nbr;
	}

	public CourseEntry() {
	    super();
	    this.nbr = 0;
	    this.course = (double) 0;
	    this.speed = (double) 0;
	    this.heading = Double.NaN;
	    this.timeToFollow = (double) 0;

	}

	public Integer getNbr() {
	    return this.nbr;
	}

	public void setNbr(final Integer nbr) {
	    this.nbr = nbr;
	}

	public Double getCourse() {
	    return this.course;
	}

	public void setCourse(final Double course) {
	    this.course = course;
	}

	public Double getSpeed() {
	    return this.speed;
	}

	public void setSpeed(final Double speed) {
	    this.speed = speed;
	}

	public Double getHeading() {
	    return this.heading;
	}

	public void setHeading(final Double heading) {
	    this.heading = heading;
	}

	public Double getTimeToFollow() {
	    return this.timeToFollow;
	}

	public void setTimeToFollow(final Double timeToFollow) {
	    this.timeToFollow = timeToFollow;
	}

    }

}
