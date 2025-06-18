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
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * UDP receiver/sender class for SEDAP-Express
 *
 * @author Volker Voß
 *
 */
public class SEDAPExpressRESTServer extends SEDAPExpressCommunicator implements Runnable {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressTCPClient.logger.setLevel(Level.ALL);
    }

    private Exception lastException = null;

    private HttpServer socket;

    private final String receiver;

    private final int port;

    private boolean status = true;

    private Thread ownThread;

    private static final LinkedBlockingQueue<SEDAPExpressMessage> messageBuffer = new LinkedBlockingQueue<>();

    /**
     * Instantiate a new SEDAP-Express UDP Client
     *
     * @param receiver Receiver hostname/IP to be used
     * @param port     Port to be used
     */
    public SEDAPExpressRESTServer(String receiver, final int port) {

	super();

	this.receiver = receiver;
	this.port = port;
    }

    public boolean connect() {

	SEDAPExpressRESTServer.logger.logp(Level.INFO, "SEDAPExpressRESTServer", "run()", "Starting REST server");
	logInput("Starting REST server");

	try {
	    final HttpServerProvider provider = HttpServerProvider.provider();
	    final HttpServer server = provider.createHttpServer(new InetSocketAddress(80), 64);
	    final HttpContext context = server.createContext("/SEDAPExpress");
	    context.getFilters().add(new TracingFilter());
	    context.setHandler(SEDAPExpressRESTServer.respondWith(x -> HttpResponse.ok("Hello " + Instant.now()).text()));
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

    static HttpHandler respondWith(final HttpFunc hf) {
	return exchange -> {

	    final HttpRequest req = HttpRequest.of(exchange);
	    final HttpResponse res = hf.apply(req);

	    exchange.getResponseHeaders().putAll(res.headers());

	    try {
		exchange.sendResponseHeaders(res.status(), 0);

		try (OutputStream os = exchange.getResponseBody()) {

		    while (true) {
			final String entry = SEDAPExpressRESTServer.messageBuffer.take().toString();
			os.write(entry.getBytes());
			os.write('|');
			System.out.println("> " + entry);
		    }
		} catch (final IOException | InterruptedException e) {

		}
	    } catch (final IOException e) {

	    }
	};
    }

    interface HttpFunc extends Function<HttpRequest, HttpResponse> {
    }

    record HttpRequest(String method, URI requestUri, Headers headers, HttpExchange exchange) {

	static HttpRequest of(final HttpExchange exchange) {
	    return new HttpRequest(exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRequestHeaders(), exchange);
	}
    }

    record HttpResponse(int status, Headers headers, String body) {

	static HttpResponse ok() {
	    return HttpResponse.ok("");
	}

	static HttpResponse ok(final String body) {
	    return new HttpResponse(200, new Headers(), body);
	}

	HttpResponse header(final String name, final String value) {
	    final var res = new HttpResponse(status(), headers(), body());
	    res.headers().add(name, value);
	    return res;
	}

	HttpResponse json() {
	    return header("Content-type", "application/json");
	}

	HttpResponse text() {
	    return header("Content-type", "text/plain");
	}
    }

    static class TracingFilter extends Filter {

	@Override
	public void doFilter(final HttpExchange exchange, final Chain chain) throws IOException {
	    final var req = HttpRequest.of(exchange);
	    System.out.println(req.toString());
	    chain.doFilter(exchange);
	}

	@Override
	public String description() {
	    return "Trace";
	}
    }

    @Override
    public void run() {

	while (this.status) {

	    try {

		// distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage.deserialize(message);

	    } catch (final Exception e) {
		this.lastException = e;

		if (this.status) {
		    SEDAPExpressTCPServer.logger.logp(Level.SEVERE, "SEDAPExpressRESTServer", "run()", "Waiting 2 seconds for reconnect on port:" + this.port);
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

	return false;
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
