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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import de.bundeswehr.uniity.sedapexpress.json.Message;
import de.bundeswehr.uniity.sedapexpress.json.SEDAPExpressJSONMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * REST client class for SEDAP-Express
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressRESTClient extends SEDAPExpressCommunicator implements Runnable {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private HttpClient client;

    private Exception lastException = null;

    private final String url;

    private int requestDelay;

    private boolean status = true;

    private final Gson gson = new Gson();

    private static final LinkedBlockingQueue<SEDAPExpressMessage> messageBuffer = new LinkedBlockingQueue<>();

    /**
     * Instantiate a new SEDAP-Express REST client
     *
     * @param url          URL of the SEDAP-Express REST server
     * @param requestDelay Delay between HTTP requests
     */
    public SEDAPExpressRESTClient(String url, int requestDelay) {

	super();

	if (url.endsWith("/"))
	    this.url = url;
	else
	    this.url = url + "/";

	this.requestDelay = requestDelay;
    }

    public boolean connect() {

	SEDAPExpressRESTClient.logger.logp(Level.INFO, "SEDAPExpressRESTClient", "run()", "Starting REST client");
	logInput("Starting REST client");

	try {
	    this.client = HttpClient.newHttpClient();

	    new Thread(this).start();

	    return true;

	} catch (

	final Exception e) {

	    SEDAPExpressRESTClient.logger.logp(Level.SEVERE, "SEDAPExpressRESTClient", "run()", e.getLocalizedMessage());
	    logInput("REST client for \"" + this.url + "\" could not be created!");

	    return false;
	}

    }

    @Override
    public void run() {

	while (this.status) {

	    try {

		// Messages abrufen
		logInput("REST client sending request for new messages...");
		HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(this.url + "SEDAPEXPRESS"))
			.headers("Content-Type", "text/plain;charset=UTF-8")
			.GET()
			.build();

		HttpResponse<String> requestResponse = this.client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

		switch (requestResponse.statusCode()) {

		case 200 -> {

		    try {
			SEDAPExpressJSONMessage jsonMessage = this.gson.fromJson(requestResponse.body(), SEDAPExpressJSONMessage.class);

			logInput("REST client received " + jsonMessage.getMessages().size() + " messages from the server!");

			jsonMessage.getMessages().forEach(message -> distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(message.getMessage())));

		    } catch (Exception e) {
			SEDAPExpressRESTClient.logger.logp(Level.SEVERE, "SEDAPExpressRESTClient", "run()", "Could not porcess server response!", e.getLocalizedMessage());

		    }
		}
		default -> {
		    logInput("REST client received HTTP error code: " + requestResponse.statusCode());
		    SEDAPExpressRESTClient.logger.logp(Level.SEVERE, "SEDAPExpressRESTClient", "run()", "REST request got response error: " + requestResponse.statusCode());
		}

		}

		// Messages senden
		if (!SEDAPExpressRESTClient.messageBuffer.isEmpty()) {

		    logInput("REST client sending " + SEDAPExpressRESTClient.messageBuffer.size() + " messages to the server...");

		    SEDAPExpressJSONMessage jsonMessage = new SEDAPExpressJSONMessage();
		    int msgCounter = 0;
		    while (!SEDAPExpressRESTClient.messageBuffer.isEmpty()) {
			jsonMessage.getMessages().add(new Message(SEDAPExpressRESTClient.messageBuffer.take().toString()));
			msgCounter++;
		    }

		    HttpRequest post = HttpRequest.newBuilder()
			    .uri(new URI(this.url + "SEDAPEXPRESS"))
			    .headers("Content-Type", "application/json;charset=UTF-8")
			    .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(jsonMessage)))
			    .build();

		    HttpResponse<String> postResponse = this.client.send(post, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

		    switch (postResponse.statusCode()) {

		    case 200 -> {

			logInput("REST client sent " + msgCounter + " messages successfully to the server!");

		    }
		    default -> {
			logInput("REST client received HTTP error code: " + postResponse.statusCode());
			SEDAPExpressRESTClient.logger.logp(Level.SEVERE, "SEDAPExpressRESTClient", "run()", "REST client got post error: " + postResponse.statusCode());
		    }

		    }

		}

	    } catch (final Exception e) {
		this.lastException = e;

		if (this.status) {
		    SEDAPExpressTCPServer.logger.logp(Level.SEVERE, "SEDAPExpressRESTServer", "run()", "Could not connect to HTTP server... waiting 2 seconds for next try!", e.getLocalizedMessage());
		    logInput("Could not connect to HTTP server... waiting 2 seconds for next try!");
		    logInput("Error message: " + e.getLocalizedMessage());
		    try {
			Thread.sleep(2000);
		    } catch (InterruptedException ex) {
		    }
		}
	    }

	    try {
		Thread.sleep(this.requestDelay);
	    } catch (InterruptedException e) {
	    }

	}
    }

    @Override
    public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

	SEDAPExpressRESTClient.messageBuffer.add(message);

	return true;
    }

    @Override
    public void stopCommunicator() {

	this.status = false;

	SEDAPExpressRESTClient.logger.logp(Level.INFO, "SEDAPExpressRESTServer", "stopCommunicator()", "REST server stopped");
	logInput("REST server stopped");
    }

    @Override
    public Exception getLastException() {

	return this.lastException;
    }

}
