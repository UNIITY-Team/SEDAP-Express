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

import java.math.BigInteger;
import java.security.PublicKey;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AlgorithmType;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

/**
 * 
 * @author Volker Voß
 *
 */
public class KEYEXCHANGEPanelController extends MessagePanelController {

    @FXML
    private ComboBox<AlgorithmType> algorithmComboBox;

    @FXML
    private TextField contactIdTextField;

    @FXML
    private TextField encryptetSecretKeyTextField;

    @FXML
    private ComboBox<Integer> keyLenghtComboBox;

    @FXML
    private TextField naturalNumberTextField;

    @FXML
    private ComboBox<Integer> phaseComboBox;

    @FXML
    private TextField primeTextField;

    @FXML
    private TextField publicKeyTextField;

    @FXML
    void initialize() {
	assert this.algorithmComboBox != null : "fx:id=\"algorithmComboBox\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.contactIdTextField != null : "fx:id=\"contactIdTextField\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.encryptetSecretKeyTextField != null : "fx:id=\"encryptetSecretKeyTextField\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.keyLenghtComboBox != null : "fx:id=\"keyLenghtComboBox\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.naturalNumberTextField != null : "fx:id=\"naturalNumberTextField\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.phaseComboBox != null : "fx:id=\"phaseComboBox\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.primeTextField != null : "fx:id=\"primeTextField\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";
	assert this.publicKeyTextField != null : "fx:id=\"publicKeyTextField\" was not injected: check your FXML file 'KEYEXCHANGEPanel.fxml'.";

	// TOOLTIPS
	Tooltip tooltipPhase = new Tooltip("0 = Exchange the public variables and public key (DH only)\n1 = Exchange public key(s) (ECDH and Kyber)\n2 = Shared key successfully generated (MAC should also already generated with that key)");
	Tooltip tooltipKeyLenght = new Tooltip("128-1024 Bit Length of the key (Phase 0, DH only)");
	Tooltip tooltipPrime = new Tooltip("HexString Publicly known prime number (> 3000 bits / 375 byte) (Phase 0, DH only)");
	Tooltip tooltipNaturalNumbber = new Tooltip("HexString Publicly known natural number smaller than p (Phase 0, DH only)");
	Tooltip tooltipPublicKey = new Tooltip("BASE64 The public key of the sender (Phase 1)");
	Tooltip tooltipEncrypytedSecretKey = new Tooltip("BASE64 The shared secret encrypted with public key of the recipient (Kyber only)");

	this.phaseComboBox.setTooltip(tooltipPhase);
	this.keyLenghtComboBox.setTooltip(tooltipKeyLenght);
	this.primeTextField.setTooltip(tooltipPrime);
	this.naturalNumberTextField.setTooltip(tooltipNaturalNumbber);
	this.publicKeyTextField.setTooltip(tooltipPublicKey);
	this.encryptetSecretKeyTextField.setTooltip(tooltipEncrypytedSecretKey);

	// AlgorithmComboBox
	this.algorithmComboBox.setItems(FXCollections.observableArrayList(AlgorithmType.values()));
	this.algorithmComboBox.setCellFactory(new Callback<ListView<AlgorithmType>, ListCell<AlgorithmType>>() {
	    @Override
	    public ListCell<AlgorithmType> call(ListView<AlgorithmType> l) {
		return new ListCell<AlgorithmType>() {
		    @Override
		    protected void updateItem(AlgorithmType item, boolean empty) {
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
	this.algorithmComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(AlgorithmType item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
		    setGraphic(null);
		} else {
		    setText(item.name());
		}
	    }
	});

	this.algorithmComboBox.getSelectionModel().selectedIndexProperty().addListener((_, _, newValue) -> {
	    switch (newValue.intValue()) {
	    case 0:
		this.phase = FXCollections.observableArrayList(0, 1, 3);

		break;
	    case 1:
		this.phase = FXCollections.observableArrayList(1, 3);

		break;
	    case 2, 3, 4, 5, 6, 7:
		this.phase = FXCollections.observableArrayList(1, 2, 3);
		break;
	    default:
		this.phase = FXCollections.observableArrayList(0, 1, 2, 3);
		break;
	    }

	    // Phase
	    this.phaseComboBox.setItems(this.phase);
	    this.phaseComboBox.getSelectionModel().select(0);

	    if (newValue.intValue() <= 1) {
		this.keyLenghtComboBox.setDisable(false);
		this.primeTextField.setDisable(false);
		// this.primeTextField.setText(null);
		this.primeNumber = null;
		this.naturalNumberTextField.setDisable(false);
		// this.naturalNumberTextField.setText(null);
		this.naturalNumber = null;
	    } else {
		this.keyLenghtComboBox.setDisable(true);
		this.primeTextField.setDisable(true);
		this.naturalNumberTextField.setDisable(true);
	    }
	});

	this.algorithmComboBox.getSelectionModel().select(0);

	// KeyLength
	ObservableList<Integer> keyLength = FXCollections.observableArrayList(128, 256);
	this.keyLenghtComboBox.setItems(keyLength);
	this.keyLenghtComboBox.getSelectionModel().select(0);
	// Prime
	this.primeTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		try {
		    this.primeNumber = new BigInteger(newValue, 16);
		} catch (NumberFormatException e) {
		    System.out.println("Ungültige Eingabe");
		}
	    } else {
		this.primeNumber = null;
	    }
	});
	// Natural Number
	// this.naturalNumberTextField.setTextFormatter(MessagePanelController.createHexFormatter());
	this.naturalNumberTextField.textProperty().addListener((_, _, newValue) -> {
	    if (!newValue.isEmpty()) {
		try {
		    this.naturalNumber = new BigInteger(newValue, 16);
		} catch (NumberFormatException e) {
		    System.out.println("Ungültige Eingabe");
		}
	    } else {
		this.naturalNumber = null;
	    }
	});

	// Public Key
	this.publicKey = null;
    }

    // private AlgorithmType algorithm;
    private ObservableList<Integer> phase;

    // private Integer keyLength;
    private BigInteger primeNumber;
    private BigInteger naturalNumber;
    private Long iv;
    private PublicKey publicKey;

//   Integer algorithm, Integer phase, Integer keyLength, BigInteger primeNumber, BigInteger naturalNumber, PublicKey publicKey, SecretKey encryptedKey) {
    @Override
    public KEYEXCHANGE createMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

//	KEYEXCHANGE keyexchange = new KEYEXCHANGE(number, time, sender, classification, acknowledgement, mac,
//		this.contactIdTextField.getText(),
//		this.algorithmComboBox.getValue(),
//		this.phaseComboBox.getValue(),
//		this.keyLenghtComboBox.getValue(),
//		this.primeNumber,
//		this.naturalNumber,
//		this.iv,
//		this.publicKey);
//
//	return keyexchange;
	return null;
    }

    @Override
    public boolean isValidFilled() {

	return true;
    }
}
