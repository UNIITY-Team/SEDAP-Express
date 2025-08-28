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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.bundeswehr.uniity.sedapexpress.json.Message;
import de.bundeswehr.uniity.sedapexpress.json.SEDAPExpressJSONMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * REST server class for SEDAP-Express
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressRESTServer extends SEDAPExpressCommunicator implements HttpHandler {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private Exception lastException = null;

    private final int port;

    private int requestDelay;

    private boolean status = true;

    private final Gson gson = new Gson();

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    private static final LinkedBlockingQueue<SEDAPExpressMessage> messageBuffer = new LinkedBlockingQueue<>();

    /**
     * Instantiate a new SEDAP-Express REST server
     *
     * @param port Port to be used
     */
    public SEDAPExpressRESTServer(final int port, int requestDelay) {

	super();

	this.port = port;
	this.requestDelay = requestDelay;
    }

    public boolean connect() {

	SEDAPExpressRESTServer.logger.logp(Level.INFO, "SEDAPExpressRESTServer", "run()", "Starting REST server");
	logInput("Starting REST server");

	try {

	    HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 64);
	    server.setExecutor(this.threadPoolExecutor);
	    server.createContext("/", this);
	    server.start();

	    SEDAPExpressRESTServer.logger.logp(Level.INFO, "SEDAPExpressRESTServer", "run()", "REST server listening on port: " + this.port);
	    logInput("REST server listening on port: " + this.port);

	    return true;

	} catch (final IOException e) {
	    SEDAPExpressRESTServer.logger.logp(Level.SEVERE, "SEDAPExpressRESTServer", "run()", "REST server cannot listening on port: " + this.port);
	    logInput("REST server cannot listening on port: " + this.port);

	    return false;
	}

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

	if ("GET".equals(exchange.getRequestMethod())) {

	    try {

		SEDAPExpressJSONMessage jsonMessage = new SEDAPExpressJSONMessage();

		while (!SEDAPExpressRESTServer.messageBuffer.isEmpty()) {
		    jsonMessage.getMessages().add(new Message(SEDAPExpressRESTServer.messageBuffer.take().toString()));

		}

		handleResponse(exchange, "", this.gson.toJson(jsonMessage), 200);

		logInput("REST server sent " + jsonMessage.getMessages().size() + " messages successfully to the client!");

	    } catch (final Exception e) {

	    }

	} else if ("POST".equals(exchange.getRequestMethod())) {

	    try {
		String postBodyStr = new String(exchange.getRequestBody().readAllBytes());

		SEDAPExpressJSONMessage jsonMessage = this.gson.fromJson(postBodyStr, SEDAPExpressJSONMessage.class);

		logInput("REST server received " + jsonMessage.getMessages().size() + " messages from the client!");

		jsonMessage.getMessages().forEach(message -> distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(message.getMessage())));

		exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
		handleResponse(exchange, postBodyStr, "{\"success\":\"true\"}", 200);
	    } catch (Exception e) {

		handleResponse(exchange, "", "{\"success\":\"false\"}", 400);
	    }
	}

    }

    private void handleResponse(HttpExchange httpExchange, String requestParamValue, String responseString, int responseCode) throws IOException {

	OutputStream outputStream = httpExchange.getResponseBody();

	// this line is a must
	httpExchange.sendResponseHeaders(responseCode, responseString.length());

	outputStream.write(responseString.getBytes());

	outputStream.flush();

	outputStream.close();

    }

    @Override
    public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {

	SEDAPExpressRESTServer.messageBuffer.add(message);

	return true;
    }

    @Override
    public void stopCommunicator() {

	this.status = false;

	SEDAPExpressRESTServer.logger.logp(Level.INFO, "SEDAPExpressRESTServer", "stopCommunicator()", "REST server stopped");
	logInput("REST server stopped");
    }

    @Override
    public Exception getLastException() {

	return this.lastException;
    }

}
