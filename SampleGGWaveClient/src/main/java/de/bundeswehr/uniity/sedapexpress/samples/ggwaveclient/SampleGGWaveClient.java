/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2026 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided with
 *    the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.samples.ggwaveclient;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bundeswehr.uniity.sedapexpress.messages.CONTACT;
import de.bundeswehr.uniity.sedapexpress.messages.EMISSION;
import de.bundeswehr.uniity.sedapexpress.messages.HEARTBEAT;
import de.bundeswehr.uniity.sedapexpress.messages.OWNUNIT;
import de.bundeswehr.uniity.sedapexpress.messages.POINT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.CommandState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.OperationalState;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS.TechnicalState;
import de.bundeswehr.uniity.sedapexpress.network.SEDAPExpressCommunicator;
import de.bundeswehr.uniity.sedapexpress.network.SEDAPExpressGGWaveClient;
import de.bundeswehr.uniity.sedapexpress.processing.SEDAPExpressSubscriber;

/**
 * Sample SEDAP-Express client using GGWave for acoustic, device-to-device
 * communication. The sample retains the message production and subscription
 * behavior of the original UDP client; only the transport is replaced.
 *
 * Both communication peers must use compatible GGWave settings and have a
 * microphone and speaker available to Java Sound.
 *
 * @author Volker Voß
 */
public class SampleGGWaveClient implements SEDAPExpressSubscriber {

	private static final Logger LOGGER = Logger.getLogger(SampleGGWaveClient.class.getName());

	private static final int TX_VOLUME = Integer.getInteger("sedap.ggwave.volume", 100);
	private static final int AUDIO_BUFFER_SAMPLES = Integer.getInteger("sedap.ggwave.audioBufferSamples", 20_480);
	private static final int FRAGMENT_DATA_SIZE = Integer.getInteger("sedap.ggwave.fragmentDataSize", 96);

	private final SEDAPExpressCommunicator communicator;
	private final String senderId;

	private volatile byte numberSTATUS;

	/**
	 * Instantiate a sample GGWave client.
	 */
	public SampleGGWaveClient() {
		this.senderId = System.getProperty("sedap.sender", "GGWAVE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		final String classification = System.getProperty("sedap.classification", "C");

		this.communicator = new SEDAPExpressGGWaveClient(SEDAPExpressGGWaveClient.GGWaveLib.GGWAVE_TX_PROTOCOL_AUDIBLE_FAST, TX_VOLUME, AUDIO_BUFFER_SAMPLES, FRAGMENT_DATA_SIZE, this.senderId,
				classification);

		this.communicator.subscribeMessages(this, MessageType.OWNUNIT, MessageType.CONTACT, MessageType.POINT, MessageType.EMISSION, MessageType.HEARTBEAT, MessageType.STATUS);

		if (!this.communicator.connect()) {
			final Exception cause = this.communicator.getLastException();
			throw new IllegalStateException("Could not start SEDAP-Express GGWave client", cause);
		}

		LOGGER.info(() -> "SEDAP-Express GGWave client started; senderId=" + this.senderId);

		// Sample thread as example for how to produce messages.
		final Thread producerThread = new Thread(this::produceStatusMessages, "SEDAP-GGWave-Sample-Producer");
		producerThread.setDaemon(true);
		producerThread.start();
	}

	private void produceStatusMessages() {
		while (!Thread.currentThread().isInterrupted()) {
			final STATUS status = new STATUS(this.numberSTATUS++, System.currentTimeMillis(), this.senderId, Classification.Confidential, Acknowledgement.FALSE, null, TechnicalState.Operational,
					OperationalState.Operational, "MLG", 50.0, "Tank", 75.3, "MainAkku", 10.8, 23, CommandState.Executed_successfully, "10.8.0.6", "rtsp://10.8.0.6/stream1", "This is a sample!");

			try {
				this.communicator.sendSEDAPExpressMessage(status);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Could not send sample STATUS message", e);
			}

			if (this.numberSTATUS == 0x7f) {
				this.numberSTATUS = 0;
			}

			try {
				Thread.sleep(5_000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void processSEDAPExpressMessage(SEDAPExpressMessage message) {
		System.out.println("Received: " + message); // Use e.g. a Logger or output in an HMI.

		if (message instanceof OWNUNIT ownunitMessage) {
			// Write your own processing code here.
		} else if (message instanceof CONTACT contactMessage) {
			// Write your own processing code here.
		} else if (message instanceof POINT pointMessage) {
			// Write your own processing code here.
		} else if (message instanceof EMISSION emissionMessage) {
			// Write your own processing code here.
		} else if (message instanceof HEARTBEAT heartbeat) {
			// Write your own processing code here.
			try {
				this.communicator.sendSEDAPExpressMessage(new HEARTBEAT());
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Could not answer HEARTBEAT", e);
			}
			System.out.println("Answered: HEARTBEAT");
		} else if (message instanceof STATUS status) {
			// Write your own processing code here.
		} else {
			throw new IllegalArgumentException("Unexpected value: " + message);
		}
	}

	public static void main(String[] args) {
		new SampleGGWaveClient();

		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
