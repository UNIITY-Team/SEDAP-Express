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
 * Represents or provides the acknowledge functionality.
 * 
 * @author Volker Voß
 *
 */
public class ACKNOWLEDGE extends SEDAPExpressMessage {

	private static final long serialVersionUID = 906671527741355989L;

	private String recipient;

	private MessageType typeOfTheMessage;

	private Byte numberOfTheMessage;

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
	 * Returns the type of the message.
	 * @return the type of the message
	 */
	public MessageType getTypeOfTheMessage() {
		return this.typeOfTheMessage;
	}

	/**
	 * Sets the type of the message.
	 * @param typeOfTheMessage value for type of the message
	 */
	public void setTypeOfTheMessage(MessageType typeOfTheMessage) {
		this.typeOfTheMessage = typeOfTheMessage;
	}

	/**
	 * Returns the number of the message.
	 * @return the number of the message
	 */
	public Byte getNumberOfTheMessage() {
		return this.numberOfTheMessage;
	}

	/**
	 * Sets the number of the message.
	 * @param numberOfTheMessage value for number of the message
	 */
	public void setNumberOfTheMessage(Byte numberOfTheMessage) {
		this.numberOfTheMessage = numberOfTheMessage;
	}

	/**
	 * Instantiate a new default ACKNOWLEDGE message
	 */
	public ACKNOWLEDGE() {

		super(null, null, null, null, null, null);

		this.recipient = null;
		this.typeOfTheMessage = null;
		this.numberOfTheMessage = null;
	}

	/**
	 * Instantiate a new ACKNOWLEDGE message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param recipient value for recipient
	 * @param nameOfTheMessage value for name of the message
	 * @param numberOfTheMessage value for number of the message
	 */
	public ACKNOWLEDGE(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, MessageType nameOfTheMessage,
			Byte numberOfTheMessage) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.recipient = recipient;
		this.typeOfTheMessage = nameOfTheMessage;
		this.numberOfTheMessage = numberOfTheMessage;

	}

	/**
 * Creates a acknowledge instance.
	 *
	 * @param message value for message
	 */
	public ACKNOWLEDGE(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
 * Creates a acknowledge instance.
	 *
	 * @param message value for message
	 */
	public ACKNOWLEDGE(Iterator<String> message) {

		super(message);

		String value;

		// Recipient
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Optional field \"recipient\" is empty!");
			} else {
				this.recipient = value;
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Incomplete message!");
		}

		// Name
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NAME_MATCHER, value)) {
				this.typeOfTheMessage = MessageType.valueOfMessageType(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Mandatory field \"nameOfTheMessage\" invalid value: \"" + value + "\"");
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Incomplete message!");
		}

		// Number
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Mandatory field \"numberOfTheMessage\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, value)) {
				this.numberOfTheMessage = Byte.parseByte(value, 16);
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Mandatory field \"numberOfTheMessage\" contains invalid value!", value);
			}
		}
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

		return SEDAPExpressMessage
				.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.typeOfTheMessage != null) ? this.typeOfTheMessage : "").append(";")
						.append((this.numberOfTheMessage != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.numberOfTheMessage) : "").toString());
	}

}
