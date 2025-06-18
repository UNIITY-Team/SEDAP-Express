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

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * UDP receiver/sender class for SEDAP-Express
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressUDPClient extends SEDAPExpressCommunicator implements Runnable {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private Exception lastException = null;

    private DatagramSocket socket;

    private final String receiver;

    private final int port;

    private boolean status = true;

    private Thread ownThread;

    /**
     * Instantiate a new SEDAP-Express UDP Client
     *
     * @param receiver Receiver hostname/IP to be used
     * @param port     Port to be used
     */
    public SEDAPExpressUDPClient(String receiver, final int port) {

	super();

	this.receiver = receiver;
	this.port = port;
    }

    public boolean connect() {

	try {
	    if (InetAddress.getByName(this.receiver).isMulticastAddress()) {
		this.socket = new MulticastSocket(this.port);
	    } else {
		this.socket = new DatagramSocket(this.port);
	    }

	    SEDAPExpressTCPServer.logger.logp(Level.INFO, "SEDAPExpressUDPClient", "run()", "UDP server listening on port: " + this.port);
	    logInput("UDP server listening on port: " + this.port);

	    this.socket.setBroadcast(true);
	    this.socket.setReuseAddress(true);

	    this.lastException = null;

	    if (this.ownThread == null) {
		this.ownThread = new Thread(this);
		this.ownThread.start();// Start receiving thread
	    }

	    return true;
	} catch (final Exception e) {

	    if (e instanceof BindException) {
		SEDAPExpressTCPServer.logger.logp(Level.INFO, "SEDAPExpressUDPClient", "run()", "Could not listening on port " + this.port + " - port is already in use!");
		logInput("Could not listening on port " + this.port + " - port is already in use!");
	    } else {
		SEDAPExpressTCPServer.logger.logp(Level.INFO, "SEDAPExpressUDPClient", "run()", "Could not listening on port " + this.port);
		logInput("Could not listening on port " + this.port);
	    }
	    this.lastException = e;
	    return false;
	}

    }

    @Override
    public void run() {

	HashSet<InetAddress> localIPAddresses = new HashSet<>();

	try {
	    final Enumeration<NetworkInterface> enumInterf = NetworkInterface.getNetworkInterfaces();

	    while (enumInterf.hasMoreElements()) {
		final NetworkInterface networkInterface = enumInterf.nextElement();

		if (networkInterface.isUp()
			&& !networkInterface.getInterfaceAddresses().isEmpty()) {

		    for (final InterfaceAddress ip : networkInterface.getInterfaceAddresses()) {

			localIPAddresses.add(ip.getAddress());
		    }
		}
	    }
	} catch (SocketException e) {

	}

	while (this.status) {

	    try {

		final DatagramPacket packet = new DatagramPacket(new byte[65000], 65000);

		this.socket.receive(packet);

		final InetAddress sender = packet.getAddress();

		// Do not receive packages from yourself
		if (localIPAddresses.contains(sender)) {
		    continue;
		}

		Arrays.asList(new String(packet.getData(), 0, packet.getLength()).split("\n")).forEach(message -> distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(message)));

	    } catch (final Exception e) {
		this.lastException = e;

		if (this.status) {
		    SEDAPExpressTCPServer.logger.logp(Level.SEVERE, "SEDAPExpressUDPClient", "run()", "Waiting 2 seconds for reconnect on port:" + this.port);
		    logInput("Waiting 2 seconds for reconnect on port:" + this.port);
		    try {
			Thread.sleep(2000);
		    } catch (InterruptedException ex) {
		    }
		}
	    }

	}
    }

    @Override
    public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

	byte[] data = SEDAPExpressMessage.serialize(message).getBytes();
	try {
	    this.socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(this.receiver), this.port));
	} catch (IOException e) {
	    this.lastException = e;
	    throw e;
	}

	return false;
    }

    @Override
    public void stopCommunicator() {

	this.status = false;

	if (this.socket != null) {
	    this.socket.close();
	}

	SEDAPExpressUDPClient.logger.logp(Level.INFO, "SEDAPExpressUDPClient", "stopCommunicator()", "UDP server stopped");
	logInput("UDP server stopped");
    }

    @Override
    public Exception getLastException() {

	return this.lastException;
    }

}
