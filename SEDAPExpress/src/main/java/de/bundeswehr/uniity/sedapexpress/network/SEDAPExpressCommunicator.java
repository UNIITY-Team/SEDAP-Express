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
import java.lang.ProcessBuilder.Redirect;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import de.bundeswehr.uniity.sedapexpress.messages.TIMESYNC;
import de.bundeswehr.uniity.sedapexpress.processing.SEDAPExpressInputLoggingSubscriber;
import de.bundeswehr.uniity.sedapexpress.processing.SEDAPExpressOutputLoggingSubscriber;
import de.bundeswehr.uniity.sedapexpress.processing.SEDAPExpressSubscriber;
import de.bundeswehr.uniity.sedapexpress.utils.EncryptionUtils;

/**
 *
 * @author Volker Voß
 *
 */
public abstract class SEDAPExpressCommunicator {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressCommunicator.logger.setLevel(Level.ALL);
    }

    protected CopyOnWriteArraySet<SEDAPExpressInputLoggingSubscriber> inputLogger = new CopyOnWriteArraySet<>();
    protected CopyOnWriteArraySet<SEDAPExpressOutputLoggingSubscriber> outputLogger = new CopyOnWriteArraySet<>();

    /**
     * Subscribe for logging messages related to data input
     * 
     * @param subscriber The class which should process the logging message
     */
    public void subscripeForInputLogging(SEDAPExpressInputLoggingSubscriber subscriber) {
	this.inputLogger.add(subscriber);
    }

    /**
     * Unsubscribe for logging messages related to data input
     * 
     * @param subscriber The class which should NOT process the logging message anymore
     */
    public void unsubscripeForInputLogging(SEDAPExpressInputLoggingSubscriber subscriber) {
	this.inputLogger.remove(subscriber);
    }

    /**
     * Subscribe for logging messages related to data output
     * 
     * @param subscriber The class which should process the logging message
     */
    public void subscripeForOutputLogging(SEDAPExpressOutputLoggingSubscriber subscriber) {
	this.outputLogger.add(subscriber);
    }

    /**
     * Unsubscribe for logging messages related to data output
     * 
     * @param subscriber The class which should NOT process the logging message anymore
     */
    public void unsubscripeForOutputLogging(SEDAPExpressOutputLoggingSubscriber subscriber) {
	this.outputLogger.remove(subscriber);
    }

    protected void logInput(String message) {
	this.inputLogger.forEach(il -> il.processSEDAPExpressInputLoggingMessage(message));
    }

    protected void logOutput(String message) {
	this.outputLogger.forEach(il -> il.processSEDAPExpressOutputLoggingMessage(message));
    }

    protected ConcurrentHashMap<MessageType, Set<SEDAPExpressSubscriber>> subscriptions = new ConcurrentHashMap<>();

    public ConcurrentHashMap<MessageType, Set<SEDAPExpressSubscriber>> getSubscriptions() {
	return this.subscriptions;
    }

    /**
     * Subscribe one or more message types
     *
     * @param subscriber the subscriber for the message types
     * @param clazzes    Collection of message types which should be subscribed
     */
    public void subscribeMessages(SEDAPExpressSubscriber subscriber, Collection<MessageType> clazzes) {

	clazzes.forEach(clazz -> {

	    this.subscriptions.computeIfAbsent(clazz, x -> new CopyOnWriteArraySet<SEDAPExpressSubscriber>());

	    this.subscriptions.get(clazz).add(subscriber);
	});

    }

    /**
     * Subscribe one or more message types
     *
     * @param subscriber the subscriber for the message types
     * @param clazzes    Collection of message types which should be subscribed
     */
    public void subscribeMessages(SEDAPExpressSubscriber subscriber, MessageType... clazzes) {

	subscribeMessages(subscriber, Arrays.asList(clazzes));

    }

    /**
     * Unsubscribe one or more message types
     *
     * @param subscriber the original subscriber of the message types
     * @param clazzes    Collection of message types which should be unsubscribe
     */
    public void unsubscribeMessages(SEDAPExpressSubscriber subscriber, Collection<MessageType> clazzes) {
	clazzes.forEach(clazz ->

	this.subscriptions.computeIfPresent(clazz, (x, value) -> {
	    value.remove(subscriber);
	    return value;
	}));
    }

    /**
     * Unsubscribe one or more message types
     *
     * @param subscriber the original subscriber of the message types
     * @param clazzes    Arrays of message types which should be unsubscribe
     */
    public void unsubscribeMessages(SEDAPExpressSubscriber subscriber, MessageType... clazzes) {

	unsubscribeMessages(subscriber, Arrays.asList(clazzes));
    }

    /**
     * Unsubscribe all message types
     *
     * @param subscriber the original subscriber of the message types
     */
    public void unsubscribeAll(SEDAPExpressSubscriber subscriber) {
	unsubscribeMessages(subscriber, MessageType.values());
    }

    /**
     * Distribute a message to the subscribers
     *
     * @param message
     */
    protected void distributeReceivedSEDAPExpressMessage(SEDAPExpressMessage message) {

	if ((message != null) && this.subscriptions.containsKey(message.getMessageType())) {
	    this.subscriptions.get(message.getMessageType()).forEach(subscriber -> subscriber.processSEDAPExpressMessage(message));
	}
    }

    private static boolean useMessageAuthentication = false;

    /**
     * Enables the authentication of messages. You also have to set a shared secret key to get it working. This method gives you the status back, if every requirement for using the authentication feature has been fullfilled.
     * 
     * @return Status of the message authentiation feature. True means the feature is activated, false means not and that not all requirements are fullfilled or the shared secret key is not useable.
     */
    public static boolean enableMessageAuthentication() {
	SEDAPExpressCommunicator.useMessageAuthentication = true && EncryptionUtils.checkKey(SEDAPExpressCommunicator.sharedSecretKey);
	return SEDAPExpressCommunicator.useMessageAuthentication;
    }

    /**
     * Disables the message authentication feature.
     */
    public static void disableMessageAuthentication() {
	SEDAPExpressCommunicator.useMessageAuthentication = false;
    }

    private static boolean useMessageEncryption = false;

    /**
     * Enables the encryption of messages. You also have to set a shared secret key to get it working. This method gives you the status back, if every requirement for using the encryption feature has been fullfilled.
     * 
     * @return Status of the message encryption feature. True means the feature is activated, false means not and that not all requirements are fullfilled or the shared secret key is not useable.
     */
    public static boolean enableMessageEncrytion() {
	SEDAPExpressCommunicator.useMessageEncryption = true && EncryptionUtils.checkKey(SEDAPExpressCommunicator.sharedSecretKey);
	return SEDAPExpressCommunicator.useMessageEncryption;
    }

    /**
     * Disables the message encryption feature.
     */
    public static void disableMessageEncryption() {
	SEDAPExpressCommunicator.useMessageEncryption = false;
    }

    private static byte[] sharedSecretKey;

    /**
     * Gives the current shared secret key back, which will be used if encryption or authentication has been enabled
     * 
     * @return the shared secrey key
     */
    public static byte[] getSharedSecretKey() {
	return SEDAPExpressCommunicator.sharedSecretKey;
    }

    /**
     * Gives the current shared secret key in a BASE64 encoded format back, which will be used if encryption or authentication has been enabled
     * 
     * @return the shared secrey key
     */
    public static String getSharedSecretKeyAsBASE64Encoded() {
	return Base64.getEncoder().encodeToString(SEDAPExpressCommunicator.sharedSecretKey);
    }

    /**
     * Sets the shared secret key, which will be used if encryption or authentication has been enabled
     * 
     * @param sharedSecretKey
     */
    public static void setSharedSecretKey(byte[] sharedSecretKey) {
	SEDAPExpressCommunicator.sharedSecretKey = sharedSecretKey;
    }

    /**
     * Sets the shared secret key, which will be used if encryption or authentication has been enabled
     * 
     * @param base64EncodedSharedSecretKey the shared secret key in BASE64 encoded format
     */
    public static void setSharedSecretKeyfromBASE64(String base64EncodedSharedSecretKey) {
	SEDAPExpressCommunicator.sharedSecretKey = Base64.getDecoder().decode(base64EncodedSharedSecretKey);
    }

    /**
     * Let the communicator establish a connection
     * 
     * @return Result of the connection attempt
     */
    public abstract boolean connect();

    /**
     * Sends a SEDAP-Express message to the connected node(s)
     * 
     * @param message SEDAP-Express to send
     * @return Result if the sending was successfully
     * 
     * @throws IOException While sending the SEDAP-Express message, somthing gone wrong
     */
    public abstract boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException;

    public byte timesyncNumber = 0;

    class TimeSyncRunnable extends Thread implements SEDAPExpressSubscriber {

	TIMESYNC timesyncAnswer;

	@Override
	public void run() {
	    SEDAPExpressCommunicator.logger.logp(Level.INFO, "SEDAPExpressCommunicator", "doTimesync()", "Started time synchronization process...");

	    this.timesyncAnswer = null;

	    if (SEDAPExpressCommunicator.this.timesyncNumber == 0x7F)
		SEDAPExpressCommunicator.this.timesyncNumber = 0;

	    try {
		sendSEDAPExpressMessage(new TIMESYNC(SEDAPExpressCommunicator.this.timesyncNumber++, System.currentTimeMillis(), SEDAPExpressCommunicator.this.createSenderId(), null, Acknowledgement.TRUE, null));
	    } catch (IOException e) {

		SEDAPExpressCommunicator.logger.logp(Level.SEVERE, "SEDAPExpressCommunicator", "doTimesync()", "Could not send TIMESYNC message", e);
	    }

	    while (this.timesyncAnswer == null) {
		try {
		    Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	    }

	    // Setting system time if the rights of the user permits it
	    final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

	    // Windows
	    if (System.getProperty("os.name").startsWith("Windows")) {
		try {

		    final Process p1 = new ProcessBuilder("cmd", "/C", "time", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "." + (calendar.get(Calendar.MILLISECOND) / 10))
			    .redirectError(Redirect.DISCARD).redirectOutput(Redirect.DISCARD).start();
		    p1.waitFor();

		    final Process p2 = new ProcessBuilder("cmd", "/C", "date", calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)).redirectError(Redirect.DISCARD)
			    .redirectOutput(Redirect.DISCARD).start();
		    p2.waitFor();

		    if ((p1.exitValue() == 0) && (p2.exitValue() == 0)) {
			SEDAPExpressCommunicator.logger.logp(Level.INFO, "SEDAPExpressCommunicator", "doTimesync()", "New system time: " + new SimpleDateFormat("MMM dd yyy HH:mm:ss.SSS").format(System.currentTimeMillis()) + "\"");
			SEDAPExpressCommunicator.logger.logp(Level.INFO, "SEDAPExpressCommunicator", "doTimesync()", "Time sync successfully!");
		    } else {

			SEDAPExpressCommunicator.logger.logp(Level.WARNING, "SEDAPExpressCommunicator", "doTimesync()", "Could not set time! No standard Windows or no rights!?");
			SEDAPExpressCommunicator.logger.logp(Level.WARNING, "SEDAPExpressCommunicator", "doTimesync()", "ExitCode \"cmd /C time\":" + p1.exitValue());
			SEDAPExpressCommunicator.logger.logp(Level.WARNING, "SEDAPExpressCommunicator", "doTimesync()", "ExitCode \"cmd /C date\":" + p2.exitValue());

		    }

		} catch (final Exception e1) {

		    SEDAPExpressCommunicator.logger.logp(Level.WARNING, "SEDAPExpressCommunicator", "doTimesync()", "Could not set time! No standard Windows or no rights!?", e1);

		}

	    } else { // Unixodide e.g. Linux, BSD
		try {

		    final Process p = new ProcessBuilder("/usr/bin/date", "-s", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(calendar.getTimeInMillis())).redirectError(Redirect.DISCARD).redirectOutput(Redirect.DISCARD).start();
		    p.waitFor();

		    if (p.exitValue() == 0) {
			SEDAPExpressCommunicator.logger.logp(Level.INFO, "SEDAPExpressCommunicator", "doTimesync()", "New system time: " + new SimpleDateFormat("MMM dd yyy HH:mm:ss.SSS").format(System.currentTimeMillis()) + "\"");
			SEDAPExpressCommunicator.logger.logp(Level.INFO, "SEDAPExpressCommunicator", "doTimesync()", "Time sync successfully!");
		    } else {

			SEDAPExpressCommunicator.logger.logp(Level.SEVERE, "SEDAPExpressCommunicator", "doTimesync()", "Could not set time! No standard Linux or no rights!?");
			SEDAPExpressCommunicator.logger.logp(Level.WARNING, "SEDAPExpressCommunicator", "doTimesync()", "ExitCode \"/usr/bin/date -s\":" + p.exitValue());

		    }

		} catch (final Exception e2) {

		    SEDAPExpressCommunicator.logger.logp(Level.SEVERE, "SEDAPExpressCommunicator", "doTimesync()", "Could not set time! No standard Linux or no rights!?", e2);

		}
	    }
	}

	@Override
	public void processSEDAPExpressMessage(SEDAPExpressMessage message) {
	    this.timesyncAnswer = (TIMESYNC) message;
	}
    };

    /**
     * Do a time synchronization via TIMESYNC message. (Please notice, that this requiries the right on the system to change the system time, which is disabled by default on windows system)
     */
    public void doTimesync() {

	new TimeSyncRunnable().start();
    }

    public String createSenderId() {

	return HexFormat.of().toHexDigits((short) Math.round(Math.random() * 65535));
    }

    /**
     * Stops and disconnects the communicator
     */
    public abstract void stopCommunicator();

    public abstract Exception getLastException();

}
