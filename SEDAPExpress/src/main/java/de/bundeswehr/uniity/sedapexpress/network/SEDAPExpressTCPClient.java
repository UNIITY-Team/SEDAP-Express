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

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import de.bundeswehr.uniity.sedapexpress.processing.SEDAPExpressSubscriber;

/**
 * TCP receiver/sender class for SEDAP-Express
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressTCPClient extends SEDAPExpressCommunicator implements Runnable {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private Exception lastException = null;

    private SocketChannel socket;

    private String host;

    private final int port;

    private boolean status = true;

    private final ConcurrentLinkedDeque<SEDAPExpressTCPClient> clients;

    private Thread ownThread;

    public String getHost() {

	return this.host;
    }

    /**
     * Instantiate a new SEDAP-Express TCP Client
     *
     * @param host Host/IP to be used
     * @param port Port to be used
     */
    public SEDAPExpressTCPClient(String host, int port) {

	super();

	this.host = host;
	this.port = port;

	this.clients = new ConcurrentLinkedDeque<>();
	this.subscriptions = new ConcurrentHashMap<MessageType, Set<SEDAPExpressSubscriber>>();
    }

    /**
     * Instantiate a new SEDAP-Express TCP Client (initiated from a TCP Server)
     *
     * @param socket        SocketChannel to be used
     * @param clients       Client list to be used
     * @param subscriptions Subscription list to be used
     */
    protected SEDAPExpressTCPClient(SocketChannel socket, ConcurrentLinkedDeque<SEDAPExpressTCPClient> clients, ConcurrentHashMap<MessageType, Set<SEDAPExpressSubscriber>> subscriptions) {

	super();

	this.socket = socket;

	try {
	    this.host = socket.getRemoteAddress().toString().substring(1);
	} catch (IOException e) {
	    this.host = "";
	}

	this.port = 0; // Unused

	this.clients = clients;
	this.subscriptions = subscriptions;

    }

    public boolean connect() {

	if (this.clients.isEmpty())
	    logInput("TCP client started!");

	try {
	    // No socket given from server
	    if (this.socket == null) {
		this.socket = SelectorProvider.provider().openSocketChannel();
		this.socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		this.socket.socket().setSoTimeout(2000);

		// Blocking access
		this.socket.configureBlocking(true);

		this.socket.connect(new InetSocketAddress(this.host, this.port));

		SEDAPExpressTCPClient.logger.logp(Level.INFO, "SEDAPExpressTCPClient", "run()", "Connected to " + this.host);
		logInput("Connected to " + this.host);

		this.lastException = null;

		if (this.ownThread == null) {
		    this.ownThread = new Thread(this);
		    this.ownThread.start();// Start receiving thread
		}

		return true;
	    } else {

		if (this.socket.isConnected()) {
		    SEDAPExpressTCPClient.logger.logp(Level.INFO, "SEDAPExpressTCPClient", "run()", "Connected to " + this.socket.getRemoteAddress());
		    this.lastException = null;

		    if (this.ownThread == null) {
			this.ownThread = new Thread(this);
			this.ownThread.start();// Start receiving thread
		    }

		    return true;
		} else {
		    SEDAPExpressTCPClient.logger.logp(Level.INFO, "SEDAPExpressTCPClient", "run()", "Disconnected");
		    return false;
		}
	    }
	} catch (Exception e) {
	    this.lastException = e;
	    return false;
	}

    }

    @Override
    public void run() {

	while (this.status) {

	    try {
		try (final BufferedReader br = new BufferedReader(Channels.newReader(this.socket, StandardCharsets.ISO_8859_1))) {

		    while (this.status) {
			String message = br.readLine(); // Waiting for data

			try {
			    if (message != null) {
				distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(message));
			    } else {
				break;
			    }
			} catch (Exception e) {

			    if (this.status) { // Only if not manually triggered
				this.lastException = e;
				SEDAPExpressTCPClient.logger.log(Level.SEVERE, "SEDAPExpressTCPClient, could not deserialize message: " + message, e);
				this.status = this.socket.isConnected();
			    }
			}
		    }

		} catch (Exception e) {
		    this.lastException = e;

		    if (this.status) { // Only if not manually triggered
			SEDAPExpressTCPClient.logger.log(Level.WARNING, "SEDAPExpressTCPClient: " + e);
		    }

		    if (!this.clients.isEmpty()) { // If initiated by server, then end client thread
			this.status = false;
			this.clients.remove(this);
			SEDAPExpressTCPClient.logger.logp(Level.INFO, "SEDAPExpressTCPClient", "run()", "Removed client " + this.host);
			logInput("Remove client " + this.host);

		    } else if (this.status) { // If client initiated by user, then reconnect attempt after 2 seconds
			SEDAPExpressTCPServer.logger.logp(Level.SEVERE, "SEDAPExpressTCPClient", "run()", "Waiting 2 seconds for reconnect to: " + this.host + ":" + this.port);
			logInput("Waiting 2 seconds for reconnect to: " + this.host + ":" + this.port);
			Thread.sleep(2000);
			this.socket = null;
			connect();
		    }
		}

	    } catch (Exception e) {
		this.lastException = e;

		if (this.status) {// Only if not manually triggered
		    SEDAPExpressTCPClient.logger.log(Level.SEVERE, "SEDAPExpressTCPClient: " + e.getLocalizedMessage());
		}

		this.status = false;
		if (!this.clients.isEmpty()) {
		    this.clients.remove(this);
		}
	    }
	}
    }

    @Override
    public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

	try {
	    if (this.socket.write(ByteBuffer.wrap(SEDAPExpressMessage.serialize(message).getBytes())) >= 0) {
		return true;
	    }
	    return false;

	} catch (IOException e) {
	    this.lastException = e;
	    throw e;
	}

    }

    public boolean isStatus() {

	return this.status;
    }

    @Override
    public void stopCommunicator() {

	this.status = false;

	try {
	    this.socket.close();
	} catch (IOException e) {
	}

	SEDAPExpressTCPClient.logger.logp(Level.INFO, "SEDAPExpressTCPClient", "stopCommunicator()", "TCP client stopped");

	if (this.clients.isEmpty()) // Nur bei TCP Client
	    logInput("TCP client stopped");

    }

    @Override
    public Exception getLastException() {

	return this.lastException;
    }

}
