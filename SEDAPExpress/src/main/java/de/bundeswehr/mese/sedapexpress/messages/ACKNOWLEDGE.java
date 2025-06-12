/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024 MESE POC: Volker Voß, Federal Armed Forces of Germany
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
 * CONTRIBUTORS BEn LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.mese.sedapexpress.messages;

import java.util.Iterator;
import java.util.logging.Level;

public class ACKNOWLEDGE extends SEDAPExpressMessage {

    private static final long serialVersionUID = 906671527741355989L;

    private String recipient;

    private MessageType typeOfTheMessage;

    private Short numberOfTheMessage;

    public String getRecipient() {
	return this.recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public MessageType getTypeOfTheMessage() {
	return this.typeOfTheMessage;
    }

    public void setTypeOfTheMessage(MessageType typeOfTheMessage) {
	this.typeOfTheMessage = typeOfTheMessage;
    }

    public Short getNumberOfTheMessage() {
	return this.numberOfTheMessage;
    }

    public void setNumberOfTheMessage(Short numberOfTheMessage) {
	this.numberOfTheMessage = numberOfTheMessage;
    }

    /**
     * Instantiate a new ACKNOWLEDGE message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param nameOfTheMessage
     * @param numberOfTheMessage
     */
    public ACKNOWLEDGE(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, MessageType nameOfTheMessage, Short numberOfTheMessage) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.typeOfTheMessage = nameOfTheMessage;
	this.numberOfTheMessage = numberOfTheMessage;

    }

    /**
     *
     * @param message
     */
    public ACKNOWLEDGE(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
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
		this.numberOfTheMessage = Short.parseShort(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "ACKNOWLEDGE", "ACKNOWLEDGE(Iterator<String> message)", "Mandatory field \"numberOfTheMessage\" contains invalid value!", value);
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof ACKNOWLEDGE)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.recipient == null) && (((ACKNOWLEDGE) obj).recipient == null)) || ((this.recipient != null) && this.recipient.equals(((ACKNOWLEDGE) obj).recipient))) &&

		    (((this.typeOfTheMessage == null) && (((ACKNOWLEDGE) obj).typeOfTheMessage == null)) || ((this.typeOfTheMessage != null) && this.typeOfTheMessage.equals(((ACKNOWLEDGE) obj).typeOfTheMessage))) &&

		    (this.numberOfTheMessage == (((ACKNOWLEDGE) obj).numberOfTheMessage));
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.typeOfTheMessage != null) ? this.typeOfTheMessage : "").append(";")
		.append((this.numberOfTheMessage != null) ? this.numberOfTheMessage : "").toString());
    }

}
