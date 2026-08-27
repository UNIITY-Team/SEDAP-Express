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
package de.bundeswehr.sedap.express.tool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SEDAPExpressMockUpServer {

    private static final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    private static final SEDAPExpressMockUpServer serverInstance = new SEDAPExpressMockUpServer();

    private static String serverAddress = "0.0.0.0";
    private static int serverPort = 50000;

    public class ClientReceiveThread implements Runnable {

	private final SocketChannel clientChannel;

	public ClientReceiveThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		try {
		    if (this.clientChannel.socket().getInputStream().available() > 0) {
			final ByteBuffer buffer = ByteBuffer.allocate(this.clientChannel.socket().getInputStream().available());

			this.clientChannel.read(buffer);

			System.out.println(new String(buffer.array()));

		    } else {
			Thread.sleep(1);
		    }
		} catch (final IOException | InterruptedException e) {
		    break;
		}
	    }

	    try {
		System.out.println("Client diconnected: " + this.clientChannel.getRemoteAddress());
	    } catch (final IOException e) {
	    }

	}
    }

    public class ClientWatchThread implements Runnable {

	private final SocketChannel clientChannel;

	public ClientWatchThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    final ByteBuffer heartBeatMessage = ByteBuffer.wrap("HEARTBEAT\n".getBytes(StandardCharsets.ISO_8859_1));

	    while (this.clientChannel.isConnected()) {
		try {
		    Thread.sleep(100);
		} catch (final InterruptedException e) {
		}

		try {
		    this.clientChannel.write(heartBeatMessage);
		    heartBeatMessage.rewind();
		} catch (final IOException e) {
		    break;
		}

	    }

	    try {
		System.out.println("Client diconnected: " + this.clientChannel.getRemoteAddress());
	    } catch (final IOException e) {
	    }

	}
    }

    public class NAVDATAThread implements Runnable {

	private final SocketChannel clientChannel;
	private double lat = 53;
	private double lon = 8;
	private final double alt = 0;

	private double cog = 10;
	private double sog = 15;

	public NAVDATAThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		incrementVariables();

		try {
		    this.clientChannel.write(
			    ByteBuffer.wrap(("NAVDATA;" + (Math.round(this.lat * 100000d) / 100000d) + ";" + (Math.round(this.lon * 100000d) / 100000d) + ";" + this.alt + ";" + Math.round(this.cog) + ";" + Math.round(this.sog) + "\n")
				    .getBytes(StandardCharsets.ISO_8859_1)));
		} catch (final Exception e) {
		    return;
		}

		try {
		    Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

	    }
	}

	private void incrementVariables() {
	    this.lat = this.lat * 1.01;
	    if (this.lat > 54) {
		this.lat = 53;
	    }

	    this.lon = this.lon * 1.02;
	    if (this.lon > 9) {
		this.lon = 7;
	    }

	    // alt bleibt konstant bei 0

	    this.cog = this.cog * 1.05;
	    if (this.cog > 180) {
		this.cog = 0;
	    }

	    this.sog = this.sog + 1.2;
	    if (this.sog > 20) {
		this.sog = 10;
	    }

	}

    }

    public class METEOThread implements Runnable {

	private final SocketChannel clientChannel;
	private double airTemp = 22.0;
	private double waterTemp = 15.3;
	private double windSpeed = 12.3;
	private double windDirection = 45.0;

	public METEOThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		incrementVariables();

		try {
		    this.clientChannel.write(ByteBuffer.wrap(
			    ("METEO;" + (Math.round(this.airTemp * 10d) / 10d) + ";" + (Math.round(this.waterTemp * 10d) / 10d) + ";" + (Math.round(this.windSpeed * 10d) / 10d) + ";" + (Math.round(this.windDirection * 10d) / 10d) + "\n")
				    .getBytes(StandardCharsets.ISO_8859_1)));
		} catch (final Exception e) {
		    return;
		}

		try {
		    Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

	    }
	}

	private void incrementVariables() {
	    this.airTemp = this.airTemp * 1.07;
	    if (this.airTemp > 24.5) {
		this.airTemp = 22.5;
	    }

	    this.waterTemp = this.waterTemp * 1.05;
	    if (this.waterTemp > 17) {
		this.waterTemp = 15.3;
	    }

	    this.windSpeed = this.windSpeed * 1.04;
	    if (this.windSpeed > 15) {
		this.windSpeed = 12.3;
	    }

	    this.windDirection = this.windDirection + 1.02;
	    if (this.windDirection > 60) {
		this.windDirection = 45;
	    }

	}

    }

    public class CONTACTThread implements Runnable {

	private final SocketChannel clientChannel;

	private double xContact1 = 500;
	private double yContact1 = 300;
	private double zContact1 = 0;

	private double latContact1 = 55;
	private double lonContact1 = 11;
	private final double altContact1 = 0;
	private double cogContact1 = 22;
	private double sogContact1 = 11;

	private double xContact2 = 1500;
	private double yContact2 = 2000;
	private final double zContact2 = 150;

	private double latContact2 = 55.5;
	private double lonContact2 = 10;
	private double altContact2 = 500;
	private double cogContact2 = 222;
	private double sogContact2 = 111;

	private byte counter = 0;

	public CONTACTThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		// Alle 10 Nachrichten den Kontakt löschen
		if (this.counter++ == 10) {
		    this.counter = 0;

		    try {
			this.clientChannel.write(ByteBuffer.wrap(("CONTACT;1;1000\n").getBytes(StandardCharsets.ISO_8859_1)));
			this.clientChannel.write(ByteBuffer.wrap(("CONTACT;1;2000\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }

		    try {
			Thread.sleep(1000);
		    } catch (final InterruptedException e) {
		    }

		} else {

		    // Werte der Kontakte verändern
		    incrementVariables();

		    try {
			this.clientChannel.write(ByteBuffer.wrap(("CONTACT;0;1000;"

				+ (Math.round(this.xContact1 * 1000d) / 1000d) + ";" + (Math.round(this.yContact1 * 1000d) / 1000d) + ";" + (Math.round(this.zContact1 * 1000d) / 1000d) + ";"

				+ (Math.round(this.latContact1 * 100000d) / 100000d) + ";" + (Math.round(this.lonContact1 * 100000d) / 100000d) + ";" + this.altContact1 + ";" + Math.round(this.cogContact1) + ";"
				+ Math.round(this.sogContact1) + ";" + "Meermaid;snspxr---------" + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }

		    try {
			Thread.sleep(100);
		    } catch (final InterruptedException e) {
		    }

		    try {
			this.clientChannel.write(ByteBuffer.wrap(("CONTACT;0;2000;"

				+ (Math.round(this.xContact2 * 1000d) / 1000d) + ";" + (Math.round(this.yContact2 * 1000d) / 1000d) + ";" + (Math.round(this.zContact2 * 1000d) / 1000d) + ";"

				+ (Math.round(this.latContact2 * 100000d) / 100000d) + ";" + (Math.round(this.lonContact2 * 100000d) / 100000d) + ";" + (Math.round(this.altContact2 * 100d) / 100d) + ";" + Math.round(this.cogContact2) + ";"
				+ Math.round(this.sogContact2) + ";" + "UH60 Blackhawk;sfapmh---------" + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }

		    try {
			Thread.sleep(1000);
		    } catch (final InterruptedException e) {
		    }

		}

	    }
	}

	private void incrementVariables() {

	    // Contact 1
	    this.latContact1 = this.latContact1 * 1.01;
	    if (this.latContact1 > 56) {
		this.latContact1 = 55;
	    }

	    this.lonContact1 = this.lonContact1 * 1.02;
	    if (this.lonContact1 > 12) {
		this.lonContact1 = 11;
	    }

	    // altContact1 bleibt konstant bei 0, da SurfaceContact

	    this.cogContact1 = this.cogContact1 * 1.05;
	    if (this.cogContact1 > 60) {
		this.cogContact1 = 22;
	    }

	    this.sogContact1 = this.sogContact1 + 1.2;
	    if (this.sogContact1 > 25) {
		this.sogContact1 = 11;
	    }

	    this.xContact1 = this.xContact1 * 0.97;
	    if (this.xContact1 > 1000) {
		this.xContact1 = 1500;
	    }

	    this.yContact1 = this.yContact1 + 1.03;
	    if (this.yContact1 > 900) {
		this.yContact1 = 700;
	    }

	    this.zContact1 = this.altContact1;

	    // Contact 2
	    this.latContact2 = this.latContact2 * 1.01;
	    if (this.latContact2 > 56) {
		this.latContact2 = 55.5;
	    }

	    this.lonContact2 = this.lonContact2 * 1.02;
	    if (this.lonContact2 > 12) {
		this.lonContact2 = 10;
	    }

	    this.altContact2 = this.altContact2 * 1.05;
	    if (this.altContact2 > 1000) {
		this.altContact2 = 5000;
	    }

	    this.cogContact2 = this.cogContact2 * 1.05;
	    if (this.cogContact2 > 270) {
		this.cogContact2 = 222;
	    }

	    this.sogContact2 = this.sogContact2 + 1.2;
	    if (this.sogContact2 == 350) {
		this.sogContact2 = 333;
	    }

	    this.xContact2 = this.xContact2 * 1.04;
	    if (this.xContact2 > 1300) {
		this.xContact2 = 800;
	    }

	    this.yContact2 = this.yContact2 * 0.97;
	    if (this.yContact2 > 800) {
		this.yContact2 = 1200;
	    }

	    this.zContact1 = this.altContact1;
	}

    }

    public class MESSAGEThread implements Runnable {

	private final SocketChannel clientChannel;
	private int alertCounter = 1, warningCounter = 1, infoCounter = 1, chatCounter = 1;

	private final String alertMessage = "This is alert no.";
	private final String warningMessage = "This is warning no.";
	private final String infoMessage = "This is info no.";
	private final String chatMessage = "This is chat no.";

	public MESSAGEThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		if (Math.random() > (1d - (1d / 8d))) { // 1:8 Alert
		    try {
			this.clientChannel.write(ByteBuffer.wrap(("MESSAGE;0;" + this.alertMessage + (this.alertCounter++) + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }
		}

		if (Math.random() > (1d - (1d / 5d))) { // 1:5 Warning
		    try {
			this.clientChannel.write(ByteBuffer.wrap(("MESSAGE;1;" + this.warningMessage + (this.warningCounter++) + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }
		}

		if (Math.random() > (1d - (1d / 3d))) { // 1:3 Info
		    try {
			this.clientChannel.write(ByteBuffer.wrap(("MESSAGE;2;" + this.infoMessage + (this.infoCounter++) + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }
		}

		if (Math.random() > (1d - (1d / 8d))) { // 1:8 Chat
		    try {
			this.clientChannel.write(ByteBuffer.wrap(("MESSAGE;3;" + this.chatMessage + (this.chatCounter++) + "\n").getBytes(StandardCharsets.ISO_8859_1)));
		    } catch (final Exception e) {
			return;
		    }
		}

		try {
		    Thread.sleep(2000);
		} catch (final InterruptedException e) {
		}

	    }
	}

    }

    public class COMMANDThread implements Runnable {

	private final SocketChannel clientChannel;

	public COMMANDThread(final SocketChannel clientChannel) {
	    this.clientChannel = clientChannel;
	}

	@Override
	public void run() {

	    while (this.clientChannel.isConnected()) {

		try {
		    this.clientChannel.write(ByteBuffer.wrap(("COMMAND;0;ED-R11B-Helo\n".getBytes(StandardCharsets.ISO_8859_1))));
		} catch (final Exception e) {
		    return;
		}

		try {
		    Thread.sleep(10000);
		} catch (final InterruptedException e) {
		}

		try {
		    this.clientChannel.write(ByteBuffer.wrap(("COMMAND;1;ED-R11B-Helo\n".getBytes(StandardCharsets.ISO_8859_1))));
		} catch (final Exception e) {
		    return;
		}

		try {
		    Thread.sleep(10000);
		} catch (final InterruptedException e) {
		}
	    }
	}

    }

    public static void main(final String[] args) {

	System.out.println("""
		-----------------------------------------------------------------------------------
		              SEDAP Express MockUp v1.0 - (C)2024, Simplified-BSD-Licence, Bundeswehr, Volker Voß
		              -----------------------------------------------------------------------------------""");

	if ((args.length != 0) && (args.length != 1) && (args.length != 2)) {
	    System.out.println("Only none, [serverport] or [bind IP address] [bind port] allowed as command line parameters!");
	    System.out.println("Example: 10.0.0.2 50000");
	    System.out.println("Defaults: 0.0.0.0 50000");
	    System.exit(1);
	}

	if (args.length == 1) {
	    try {
		SEDAPExpressMockUpServer.serverPort = Integer.parseInt(args[0]);
	    } catch (final Exception e) {
		System.out.println("Server port must be an valid integer number [1-65535]");
		System.exit(1);
	    }
	}

	if (args.length == 2) {
	    SEDAPExpressMockUpServer.serverAddress = args[0];
	    try {
		SEDAPExpressMockUpServer.serverPort = Integer.parseInt(args[1]);
	    } catch (final Exception e) {
		System.out.println("Server port must be an valid integer number [1-65535]");
		System.exit(1);
	    }
	}

	try {
	    final ServerSocketChannel serverChannel = SelectorProvider.provider().openServerSocketChannel();
	    serverChannel.configureBlocking(true);
	    serverChannel.bind(new InetSocketAddress(SEDAPExpressMockUpServer.serverAddress, SEDAPExpressMockUpServer.serverPort));

	    System.out.println("Server is listening on " + SEDAPExpressMockUpServer.serverAddress + ":" + SEDAPExpressMockUpServer.serverPort + "!");

	    for (;;) {

		final SocketChannel clientChannel = serverChannel.accept();
		clientChannel.configureBlocking(true);

		System.out.println("New client connected: " + clientChannel.getRemoteAddress());

		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new ClientReceiveThread(clientChannel));

		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new ClientWatchThread(clientChannel));

		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new NAVDATAThread(clientChannel));
		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new METEOThread(clientChannel));
		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new CONTACTThread(clientChannel));
		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new MESSAGEThread(clientChannel));
		SEDAPExpressMockUpServer.clientExecutor.execute(SEDAPExpressMockUpServer.serverInstance.new COMMANDThread(clientChannel));

	    }

	} catch (final Exception e) {
	    System.err.println("Could not start server on port " + SEDAPExpressMockUpServer.serverAddress + ":" + SEDAPExpressMockUpServer.serverPort + "!");
	    System.exit(1);
	}
    }

}
