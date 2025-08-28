/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2025 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
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
package de.bundeswehr.uniity.sedapexpress.network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

/**
 * MQTT Client for the communication with a MQTT SEDAP-Express server (e.g. Eclipse Mosquitto)
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressMQTTClient extends SEDAPExpressCommunicator implements MqttCallback {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private Exception lastException = null;

    private final String mqttAddress;

    private final String mqttRoot;

    private final String clientUsername;

    private final String clientPassword;

    private InputStream caCertificateStream;

    private InputStream clientCertificateStream;

    private InputStream clientKeyFileStream;

    private final HashSet<String> filterTopics;

    private boolean status = true;

    private MqttClient client;

    private final LinkedBlockingQueue<SEDAPExpressMessage> outQueue = new LinkedBlockingQueue<>();

    /**
     * Instantiate a new SEDAP-Express MQTT client
     * 
     * @param mqttAddress
     * @param name
     * @param clientUsername
     * @param clientPassword
     * @param caCertificate
     * @param clientCertificate
     * @param clientKeyFile
     */
    public SEDAPExpressMQTTClient(final String mqttAddress, final String name,
	    final String clientUsername, final String clientPassword,
	    final InputStream caCertificate, final InputStream clientCertificate, final InputStream clientKeyFile) {
	this(mqttAddress, name, clientUsername, clientPassword, caCertificate, clientCertificate, clientKeyFile, new ArrayList<String>());
    }

    /**
     * Instantiate a new SEDAP-Express MQTT client
     * 
     * @param mqttAddress
     * @param name
     * @param clientUsername
     * @param clientPassword
     * @param caCertificateStream
     * @param clientCertificateStream
     * @param clientKeyFileStream
     * @param filterNames             List of topics/path which should be filted
     */
    public SEDAPExpressMQTTClient(final String mqttAddress, final String name,
	    final String clientUsername, final String clientPassword,
	    final InputStream caCertificateStream, final InputStream clientCertificateStream, InputStream clientKeyFileStream, final List<String> filterNames) {

	this(mqttAddress, name, clientUsername, clientPassword, caCertificateStream, clientCertificateStream, clientKeyFileStream, filterNames, "SEDAP-X/");
    }

    /**
     * Instantiate a new SEDAP-Express MQTT client
     * 
     * @param mqttAddress
     * @param name
     * @param clientUsername
     * @param clientPassword
     * @param caCertificateStream
     * @param clientCertificateStream
     * @param clientKeyFileStream
     * @param filterNames             List of topics/path which should be filted
     * @param rootPath                default "SEDAP-X"
     */
    public SEDAPExpressMQTTClient(final String mqttAddress, final String name,
	    final String clientUsername, final String clientPassword,
	    final InputStream caCertificateStream, final InputStream clientCertificateStream, InputStream clientKeyFileStream, final List<String> filterNames, String rootPath) {

	this.mqttAddress = mqttAddress;
	if (rootPath.endsWith("/")) {
	    this.mqttRoot = rootPath + name;
	} else {
	    this.mqttRoot = rootPath + "/" + name;
	}

	this.clientUsername = clientUsername;
	this.clientPassword = clientPassword;
	this.caCertificateStream = caCertificateStream;
	this.clientCertificateStream = clientCertificateStream;
	this.clientKeyFileStream = clientKeyFileStream;
	this.filterTopics = new HashSet<>(filterNames);

    }

    /**
     * Instantiate a new SEDAP-Express MQTT client
     * 
     * @param mqttAddress
     * @param name
     * @param clientUsername
     * @param clientPassword
     * @param caCertificateFile
     * @param clientCertificateFile
     * @param clientKeyFileFile
     * @param filterNames           List of topics/path which should be filted
     * @throws FileNotFoundException
     */
    public SEDAPExpressMQTTClient(final String mqttAddress, final String name,
	    final String clientUsername, final String clientPassword,
	    final String caCertificateFile, final String clientCertificateFile, String clientKeyFileFile, final List<String> filterNames) throws FileNotFoundException {

	this(mqttAddress, name, clientUsername, clientPassword,
		new FileInputStream(caCertificateFile),
		new FileInputStream(clientCertificateFile),
		new FileInputStream(clientKeyFileFile), new ArrayList<String>());
    }

    /**
     * Instantiate a new SEDAP-Express MQTT client
     * 
     * @param mqttAddress
     * @param name
     * @param clientUsername
     * @param clientPassword
     * @param caCertificateFile
     * @param clientCertificateFile
     * @param clientKeyFileFile
     * @param filterNames           List of topics/path which should be filted
     * @param rootPath              default "SEDAP-X"
     * @throws FileNotFoundException
     */
    public SEDAPExpressMQTTClient(final String mqttAddress, final String name,
	    final String clientUsername, final String clientPassword,
	    final String caCertificateFile, final String clientCertificateFile, String clientKeyFileFile, final List<String> filterNames, String rootPath) throws FileNotFoundException {

	this(mqttAddress, name, clientUsername, clientPassword,
		new FileInputStream(caCertificateFile),
		new FileInputStream(clientCertificateFile),
		new FileInputStream(clientKeyFileFile), new ArrayList<String>(), rootPath);
    }

    @Override
    public boolean connect() {

	new Thread(() -> {

	    this.status = true;

	    while (this.status) {

		logInput("Trying to connect to MQTT server ...");

		try {

		    this.client = new MqttClient(this.mqttAddress, this.clientUsername, new MemoryPersistence());
		    this.client.setCallback(this);

		    final MqttConnectionOptions options = new MqttConnectionOptions();
		    options.setUserName(this.clientUsername);
		    options.setPassword(this.clientPassword.getBytes(StandardCharsets.ISO_8859_1));
		    options.setAutomaticReconnect(true);
		    options.setCleanStart(true);
		    options.setKeepAliveInterval(60);
		    options.setMaxReconnectDelay(10);

		    if (this.mqttAddress.startsWith("ssl")) {

			options.setSocketFactory(SSLUtils.getSocketFactory(this.caCertificateStream, this.clientCertificateStream, this.clientKeyFileStream, ""));
		    }

		    this.client.connect(options);

		    this.client.subscribe(this.mqttRoot + "/+/+", 1);

		    while (this.status) {

			final MqttMessage mqttMmessage = new MqttMessage();

			final SEDAPExpressMessage msg = this.outQueue.take();

			logInput(msg.toString());
			mqttMmessage.setPayload(msg.toString().getBytes());

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

    @Override
    public void messageArrived(final String topic, final MqttMessage message) throws Exception {

	try {
	    if (message != null) {

		if (!this.filterTopics.contains(topic.substring(topic.lastIndexOf("/"))))
		    distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(new String(message.getPayload())));
	    }
	} catch (Exception e) {

	    if (this.status) { // Only if not manually triggered
		this.lastException = e;
		SEDAPExpressTCPClient.logger.log(Level.SEVERE, "SEDAPExpressMQTTClient, could not deserialize message: " + message, e);
		this.status = false;
	    }
	}
    }

    @Override
    public void stopCommunicator() {

	logInput("Stopping MQTT Client...");

	this.status = false;
    }

    public boolean isReady() {

	return (this.client != null) && this.client.isConnected();
    }

    @Override
    public void authPacketArrived(final int reasonCode, final MqttProperties properties) {
	// Ignore
    }

    @Override
    public void connectComplete(final boolean arg0, final String arg1) {
	SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "connectComplete()", "Successfully connected to MQTT server ...");
	logInput("Successfully connected to MQTT server ...");
    }

    @Override
    public void deliveryComplete(final IMqttToken token) {
	// Ignore
    }

    @Override
    public void disconnected(final MqttDisconnectResponse response) {
	SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "disconnected()", "Disconnected from MQTT server ...");
	logInput("Disconnected from MQTT server ...");
    }

    @Override
    public void mqttErrorOccurred(final MqttException exception) {
	SEDAPExpressMQTTClient.logger.logp(Level.INFO, "SEDAPExpressMQTTClient", "mqttErrorOccurred()", "MQTT error occurred: " + exception.getLocalizedMessage());
	logInput("MQTT error occurred: " + exception.getLocalizedMessage());
    }

    @Override
    public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

	this.outQueue.add(message);

	return true;
    }

    @Override
    public Exception getLastException() {

	return this.lastException;
    }

}
