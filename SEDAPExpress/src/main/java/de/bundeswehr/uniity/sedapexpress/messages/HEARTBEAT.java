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
package de.bundeswehr.uniity.sedapexpress.messages;

import java.util.Iterator;
import java.util.logging.Level;

/**
 * Represents or provides the heartbeat functionality.
 * 
 * @author Volker Voß
 *
 */
public class HEARTBEAT extends SEDAPExpressMessage {

	private static final long serialVersionUID = 3957328245063360161L;

	private String recipient;

	/**
	 * Returns the recipient.
	 * @return the recipient
	 */
	public String getRecipient() {
		return this.recipient;
	}

	/**
	 * Sets the recipient.
	 * @param recipient value for recipient
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Instantiate a new HEARTBEAT message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 */
	public HEARTBEAT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {

		this(number, time, sender, classification, acknowledgement, mac, null);

	}

	/**
	 * Instantiate a new HEARTBEAT message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param recipient value for recipient
	 */
	public HEARTBEAT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.recipient = recipient;
	}

	/**
	 * Instantiate a new default HEARTBEAT message
	 */
	public HEARTBEAT() {

		super(null, null, null, null, null, null);

		this.recipient = null;
	}

	/**
 * Creates a heartbeat instance.
	 *
	 * @param message value for message
	 */
	public HEARTBEAT(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
 * Creates a heartbeat instance.
	 *
	 * @param message value for message
	 */
	public HEARTBEAT(Iterator<String> message) {

		super(message);

		String value;

		// Recipient
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "HEARTBEAT", "HEARTBEAT(Iterator<String> message)", "Optional field \"recipient\" is empty!");
			} else {
				this.recipient = value;
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.INFO, "HEARTBEAT", "HEARTBEAT(Iterator<String> message)", "Optional field \"recipient\" is empty!");
		}
	}

	/**
	 * Generates a new Answer-HEARTBEAT message from a HEARTBEART-Request.
	 * 
	 * @param originalHEARTBEAT Original HEARTBEAT request message
	 * @return answer HEARTBEAT message
	 */
	public static HEARTBEAT generateAnswerHEARTBEAT(HEARTBEAT originalHEARTBEAT) {

		HEARTBEAT heartBeat = new HEARTBEAT();

		if (originalHEARTBEAT.getSender() != null) {
			heartBeat.setRecipient(originalHEARTBEAT.getSender());
		}

		if (originalHEARTBEAT.getRecipient() != null) {
			heartBeat.setSender(originalHEARTBEAT.getSender());
		}

		if (originalHEARTBEAT.getClassification() != null) {
			heartBeat.setClassification(originalHEARTBEAT.getClassification());
		}

		if (originalHEARTBEAT.getTime() != null) {
			heartBeat.setTime(System.currentTimeMillis());
		}

		return heartBeat;

	}

	/**
	 * Compares this instance with the specified object.
	 * @param obj value for obj
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		String thisKey = equalityKey();
		String otherKey = ((SEDAPExpressMessage) obj).equalityKey();
		return thisKey != null && thisKey.equals(otherKey);
	}

	/**
	 * Returns the hash code for this instance.
	 * @return the result of the check
	 */
	@Override
	public int hashCode() {
		String key = equalityKey();
		return (key != null) ? key.hashCode() : System.identityHashCode(this);
	}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
	@Override
	public String toString() {

		return SEDAPExpressMessage.removeSemicolons(serializeHeader().append(this.recipient != null ? this.recipient : "").toString());

	}

}
