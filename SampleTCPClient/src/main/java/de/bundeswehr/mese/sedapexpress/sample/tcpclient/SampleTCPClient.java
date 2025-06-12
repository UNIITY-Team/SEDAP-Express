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
package de.bundeswehr.mese.sedapexpress.sample.tcpclient;

import java.io.IOException;

import de.bundeswehr.mese.sedapexpress.messages.CONTACT;
import de.bundeswehr.mese.sedapexpress.messages.HEARTBEAT;
import de.bundeswehr.mese.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import de.bundeswehr.mese.sedapexpress.messages.STATUS;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.CommandState;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.OperationalState;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.TechnicalState;
import de.bundeswehr.mese.sedapexpress.network.SEDAPExpressCommunicator;
import de.bundeswehr.mese.sedapexpress.network.SEDAPExpressTCPClient;
import de.bundeswehr.mese.sedapexpress.processing.SEDAPExpressSubscriber;

public class SampleTCPClient implements SEDAPExpressSubscriber {

    private final SEDAPExpressCommunicator communicator;

    private final String senderId;

    private byte numberSTATUS = 0;

    /**
     * Instantiate a sample TCP client
     *
     */
    public SampleTCPClient() {

	this.communicator = new SEDAPExpressTCPClient("localhost", 50000);
	this.communicator.connect();

	this.communicator.subscribeMessages(this, MessageType.OWNUNIT, MessageType.CONTACT, MessageType.HEARTBEAT, MessageType.STATUS);
	this.senderId = this.communicator.createSenderId();

	// Sample thread as example for how producing messages
	new Thread(() -> {

	    final STATUS status = new STATUS(this.numberSTATUS++, System.currentTimeMillis(), this.senderId, Classification.Confidential, Acknowledgement.FALSE, null, TechnicalState.Operational, OperationalState.Operational, "MLG", 50.0,
		    "Tank", 75.3, "MainAkku", 10.8, 23, CommandState.Executed_successfully, "10.8.0.6", "rtsp://10.8.0.6/stream1", "This is a sample!");

	    try {
		this.communicator.sendSEDAPExpressMessage(status);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    if (this.numberSTATUS == 7f) {
		this.numberSTATUS = 0;
	    }

	}).start();

    }

    @Override
    public void processSEDAPExpressMessage(SEDAPExpressMessage message) {

	System.out.println("Received: " + message); // Use e.g. a Logger or output in a HMI

	if (message instanceof OWNUNIT ownunitMessage) {
	    // Write here your own processing code
	}

	else if (message instanceof CONTACT contactMessage) {

	    // Write here your own processing code
	}

	else if (message instanceof HEARTBEAT heartbeat) {

	    // Write here your own processing code
	    try {
		this.communicator.sendSEDAPExpressMessage(new HEARTBEAT());
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    System.out.println("Answered: HEARTBEAT");
	}

	else if (message instanceof STATUS status) {
	    // Write here your own processing code
	}

	else
	    throw new IllegalArgumentException("Unexpected value: " + message);

    }

    public static void main(String[] args) {
	new SampleTCPClient();

	try {
	    Thread.sleep(Integer.MAX_VALUE);
	} catch (InterruptedException e) {

	}
    }
}
