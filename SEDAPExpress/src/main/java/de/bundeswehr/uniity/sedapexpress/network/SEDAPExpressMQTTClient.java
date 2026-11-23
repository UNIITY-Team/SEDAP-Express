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
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSecurityException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.utils.SSLUtils;

/**
 * MQTT Client for the communication with a MQTT SEDAP-Express server (e.g.
 * Eclipse Mosquitto)
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressMQTTClient extends SEDAPExpressCommunicator implements MqttCallback {

	protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	static {
		SEDAPExpressMQTTClient.logger.setLevel(Level.ALL);
	}

	private Exception lastException = null;

	private final String mqttAddress;

	private final String mqttRoot;

	private final String clientUsername;

	private final String clientPassword;

	private String caCertificateFile;

	private String clientCertificateFile;

	private String clientKeyFile;

	private final HashSet<String> filterTopics;

	private boolean status = true;

	private MqttClient client;

	private static final int OUTGOING_QUEUE_CAPACITY = 10_000;
	private final LinkedBlockingQueue<SEDAPExpressMessage> outQueue = new LinkedBlockingQueue<>(OUTGOING_QUEUE_CAPACITY);

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress value for mqtt address
	 * @param name        value for name
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name) {
		this(mqttAddress, name, null, null, new ArrayList<>(), "SEDAP-X");
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress value for mqtt address
	 * @param name        value for name
	 * @param filterNames List of topics/path which should be filted
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final List<String> filterNames) {
		this(mqttAddress, name, null, null, filterNames, "SEDAP-X");
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress value for mqtt address
	 * @param name        value for name
	 * @param filterNames List of topics/path which should be filted
	 * @param rootPath    default "SEDAP-X"
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final List<String> filterNames, String rootPath) {
		this(mqttAddress, name, null, null, filterNames, rootPath);
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress    value for mqtt address
	 * @param name           value for name
	 * @param clientUsername value for client username
	 * @param clientPassword value for client password
	 * @param filterNames    List of topics/path which should be filted
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final String clientUsername, final String clientPassword, final List<String> filterNames) {
		this(mqttAddress, name, clientUsername, clientPassword, filterNames, "SEDAP-X");
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress    value for mqtt address
	 * @param name           value for name
	 * @param clientUsername value for client username
	 * @param clientPassword value for client password
	 * @param filterNames    List of topics/path which should be filted
	 * @param rootPath       default "SEDAP-X"
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final String clientUsername, final String clientPassword, final List<String> filterNames, String rootPath) {

		this.mqttAddress = mqttAddress;
		if (rootPath.endsWith("/")) {
			this.mqttRoot = rootPath + name;
		} else {
			this.mqttRoot = rootPath + "/" + name;
		}

		this.clientUsername = clientUsername;
		this.clientPassword = clientPassword;
		this.caCertificateFile = null;
		this.clientCertificateFile = null;
		this.clientKeyFile = null;
		this.filterTopics = new HashSet<>(filterNames);
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress           value for mqtt address
	 * @param name                  value for name
	 * @param caCertificateFile     value for ca certificate file
	 * @param clientCertificateFile value for client certificate file
	 * @param clientKeyFileFile     value for client key file file
	 * @param filterNames           List of topics/path which should be filted
	 * @throws FileNotFoundException if the operation cannot be completed because of this condition
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final String caCertificateFile, final String clientCertificateFile, String clientKeyFileFile,
			final List<String> filterNames) throws FileNotFoundException {

		this(mqttAddress, name, null, null, caCertificateFile, clientCertificateFile, clientKeyFileFile, filterNames, "SEDAP-X");
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress           value for mqtt address
	 * @param name                  value for name
	 * @param caCertificateFile     value for ca certificate file
	 * @param clientCertificateFile value for client certificate file
	 * @param clientKeyFileFile     value for client key file file
	 * @param filterNames           List of topics/path which should be filted
	 * @param rootPath              default "SEDAP-X"
	 * @throws FileNotFoundException if the operation cannot be completed because of this condition
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final String caCertificateFile, final String clientCertificateFile, String clientKeyFileFile,
			final List<String> filterNames, String rootPath) throws FileNotFoundException {

		this(mqttAddress, name, null, null, caCertificateFile, clientCertificateFile, clientKeyFileFile, filterNames, rootPath);
	}

	/**
	 * Instantiate a new SEDAP-Express MQTT client
	 * 
	 * @param mqttAddress           value for mqtt address
	 * @param name                  value for name
	 * @param clientUsername        value for client username
	 * @param clientPassword        value for client password
	 * @param caCertificateFile     value for ca certificate file
	 * @param clientCertificateFile value for client certificate file
	 * @param clientKeyFile         value for client key file
	 * @param filterNames           List of topics/path which should be filted
	 * @param rootPath              default "SEDAP-X"
	 */
	public SEDAPExpressMQTTClient(final String mqttAddress, final String name, final String clientUsername, final String clientPassword, final String caCertificateFile,
			final String clientCertificateFile, String clientKeyFile, final List<String> filterNames, String rootPath) {

		this.mqttAddress = mqttAddress;
		if (rootPath.endsWith("/")) {
			this.mqttRoot = rootPath + name;
		} else {
			this.mqttRoot = rootPath + "/" + name;
		}

		this.clientUsername = clientUsername;
		this.clientPassword = clientPassword;
		this.caCertificateFile = caCertificateFile;
		this.clientCertificateFile = clientCertificateFile;
		this.clientKeyFile = clientKeyFile;
		this.filterTopics = new HashSet<>(filterNames);

	}

	/**
	 * Establishes the connection to the configured endpoint.
	 * 
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	@Override
	public boolean connect() {

		new Thread(() -> {

			this.status = true;

			while (this.status) {

				logInput("Trying to connect to MQTT server ...");

				try {

					this.client = new MqttClient(this.mqttAddress, UUID.randomUUID().toString(), new MemoryPersistence());
					this.client.setCallback(this);

					final MqttConnectionOptions options = new MqttConnectionOptions();
					if (this.clientUsername != null) {
						options.setUserName(this.clientUsername);
						options.setPassword(this.clientPassword.getBytes(StandardCharsets.ISO_8859_1));
					}
					options.setAutomaticReconnect(true);
					options.setCleanStart(true);
					options.setKeepAliveInterval(60);
					options.setMaxReconnectDelay(10);

					if (this.caCertificateFile != null && this.clientCertificateFile != null && this.clientKeyFile != null && this.mqttAddress.startsWith("ssl")) {

						options.setSocketFactory(SSLUtils.getSocketFactory(this.caCertificateFile, this.clientCertificateFile, this.clientKeyFile, ""));
					}

					this.client.connect(options);

					this.client.subscribe(this.mqttRoot.substring(0, this.mqttRoot.lastIndexOf('/') + 1) + "+/+", 1);

					while (this.status) {

						final MqttMessage mqttMmessage = new MqttMessage();

						final SEDAPExpressMessage msg = this.outQueue.take();

						logInput(msg.toString());
						mqttMmessage.setPayload(msg.toString().getBytes(StandardCharsets.ISO_8859_1));

						try {
							SEDAPExpressMQTTClient.this.client.publish(this.mqttRoot + "/" + msg.getClass().getSimpleName(), mqttMmessage);
						} catch (final MqttException e) {
							e.printStackTrace();
						}

					}

				} catch (final MqttSecurityException e) {

					SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "connect()", "MQTT error occurred: " + e.getLocalizedMessage());
					logInput("Wrong username/password!");

					if (this.client != null) {
						try {
							this.client.close();
						} catch (final Exception ex) {
						}
					}
					this.status = false;
					e.printStackTrace();

				} catch (final Exception e) {
					SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "connect()", "MQTT error occurred: " + e.getLocalizedMessage());
					logInput("Could not connect to MQTT server ...");
					e.printStackTrace();
					this.status = false;
				}

				try {
					Thread.sleep(2000);
				} catch (final InterruptedException e) {
				}

			}

		}).start();

		// Warten solange Status ob irgendwann verbunden
		// Wenn Verbindung fehlschlägt, dann auch status == false
		while (this.status) {

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}

			if (this.client != null && this.client.isConnected())
				return true;

		}

		return false;

	}

	/**
	 * Processes the incoming communication or work item.
	 * 
	 * @param topic   value for topic
	 * @param message value for message
	 * @throws Exception if the operation cannot be completed because of this condition
	 */
	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws Exception {

		try {
			if (message != null) {

				if (!this.filterTopics.contains(topic.substring(topic.lastIndexOf("/") + 1)))
					distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(new String(message.getPayload(), StandardCharsets.ISO_8859_1)));
			}
		} catch (Exception e) {

			if (this.status) { // Only if not manually triggered
				this.lastException = e;
				SEDAPExpressTCPClient.logger.log(Level.SEVERE, "SEDAPExpressMQTTClient, could not deserialize message: " + message, e);
				this.status = false;
			}
		}
	}

	/**
	 * Stops the communication resource and releases its active connection.
	 */
	@Override
	public void stopCommunicator() {

		logInput("Stopping MQTT Client...");

		this.status = false;
	}

	/**
	 * Indicates whether the ready condition is met.
	 * 
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	public boolean isReady() {

		return (this.client != null) && this.client.isConnected();
	}

	/**
	 * Performs the auth packet arrived operation.
	 * 
	 * @param reasonCode value for reason code
	 * @param properties value for properties
	 */
	@Override
	public void authPacketArrived(final int reasonCode, final MqttProperties properties) {
		// Ignore
	}

	/**
	 * Performs the connect complete operation.
	 * 
	 * @param arg0 value for arg0
	 * @param arg1 value for arg1
	 */
	@Override
	public void connectComplete(final boolean arg0, final String arg1) {
		SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "connectComplete()", "Successfully connected to MQTT server ...");
		logInput("Successfully connected to MQTT server ...");
	}

	/**
	 * Performs the delivery complete operation.
	 * 
	 * @param token value for token
	 */
	@Override
	public void deliveryComplete(final IMqttToken token) {
		// Ignore
	}

	/**
	 * Performs the disconnected operation.
	 * 
	 * @param response value for response
	 */
	@Override
	public void disconnected(final MqttDisconnectResponse response) {
		SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "disconnected()", "Disconnected from MQTT server ...");
		logInput("Disconnected from MQTT server ...");
	}

	/**
	 * Performs the mqtt error occurred operation.
	 * 
	 * @param exception value for exception
	 */
	@Override
	public void mqttErrorOccurred(final MqttException exception) {
		SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "mqttErrorOccurred()", "MQTT error occurred: " + exception.getLocalizedMessage());
		logInput("MQTT error occurred: " + exception.getLocalizedMessage());
	}

	/**
	 * Sends the supplied message through the configured transport.
	 * 
	 * @param message value for message
	 * @return {@code true} if the message was accepted by the outgoing queue; {@code false}
	 *         if the message is {@code null} or the bounded queue is full
	 * @throws IOException if the transport cannot accept the message because of an I/O failure
	 */
	@Override
	public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

		return message != null && this.outQueue.offer(message);
	}

	/**
	 * Returns the last exception.
	 * 
	 * @return the last exception
	 */
	@Override
	public Exception getLastException() {

		return this.lastException;
	}

}
